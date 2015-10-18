package controllers;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import application.Models;
import application.Services;
import model.Model;
import model.Robot;
import model.enums.ButtonFunction;
import net.java.games.input.Controller;
import protobuf.Radio.RadioProtocolCommand;
import services.Producer;
import util.Logger;

/**
 * This class is used to process a {@link ControllerLayout Controller} {@link Model} and 
 * generate a protobuf message accordingly
 * 
 *  TODO
    * read max_speed and solandoid_speed from a config file
    * calc speed vectors
    * stop_all_robots
    * calc rad/s
 * 
 * @author Jeroen
 *
 */
public class ControllerHandler extends Producer{
	
	//TODO should be read from a config of some sorts
	float MAX_SPEED = 4f,
			MAX_STRENGTH = 1f;
	
	/**
	 * Map with previous buttonstates and respective values
	 */
	private HashMap<ButtonFunction, Float> previousButtonState = new HashMap<ButtonFunction, Float>();
	/**
	 * Controllerlayout that should be used for inputprocessing
	 */
	private ControllerLayout layout;
	// respective logger
	private Logger logger = Logger.getLogger();
	
	/**
	 * Instantiates a handler assigned to a specific layout
	 * @param layout
	 */
	public ControllerHandler(ControllerLayout layout){
		super("ControllerHandler " + layout.getController().getName());
		//fill previousButtonState array
		layout.getBindings().entrySet().forEach(entry -> previousButtonState.put(entry.getValue(), 0f));
		
		this.layout = layout;
	}

	/**
	 * Mutate the given {@link RadioProtocolCommand.Builder packet} with modifier given as  
	 * 
	 * @param button
	 * @param packet {@link RadioProtocolCommand.Builder packet}  to mutate
	 * @return
	 */
	private boolean processInput(Entry<ButtonFunction, Float> button, RadioProtocolCommand.Builder packet) {
		ButtonFunction function = button.getKey();
		Float buttonValue = button.getValue();
		
		//switch to the right function handeling
		switch(function){
		case KICK:
			if(isPressed(buttonValue)) {
				float kickStrength = layout.containsBinding(ButtonFunction.KICK_STRENGTH) ? layout.get(ButtonFunction.KICK_STRENGTH) : MAX_STRENGTH;
				packet.setFlatKick(kickStrength);
			}
			return true;
		
		case CHIP:
			if(isPressed(buttonValue)) {
				float chipStrength = layout.containsBinding(ButtonFunction.CHIP_STRENGTH) ? layout.get(ButtonFunction.CHIP_STRENGTH) : MAX_STRENGTH;
				packet.setChipKick(chipStrength);
			}
			return true;
		
		case DRIBBLE:
			packet.setDribblerSpin(buttonValue);
			return true;
			
		case DRIBBLE_TOGGLE:
			if(isPressed(buttonValue)){
				float dribbleSpeed = ((Robot)Models.get("robot A" + packet.getRobotId())).getDribbleSpeed() == 0f ? 1f : 0f;
				packet.setDribblerSpin(dribbleSpeed);
			}
			return true;

		case DRIBBLE_SPEED:
			float dribbleSpeed = layout.get(ButtonFunction.DRIBBLE_SPEED);
			packet.setDribblerSpin(dribbleSpeed);
			return true;

		case DIRECTION_POV:
			if(isPressed(buttonValue)){
				packet.setVelocityY((float) Math.sin(buttonValue * 2*Math.PI) *  MAX_SPEED);
				packet.setVelocityX((float) Math.cos(buttonValue * 2*Math.PI) * -MAX_SPEED);
			}
			return true;
			
		case DIRECTION_FORWARD:
			if(isPressed(buttonValue))
				packet.setVelocityX(MAX_SPEED);
			return true;

		case DIRECTION_BACKWARD:
			if(isPressed(buttonValue))
				packet.setVelocityX(-1*MAX_SPEED);
			return true;

		case DIRECTION_LEFT:
			if(isPressed(buttonValue))
				packet.setVelocityY(MAX_SPEED);
			return true;
		
		case DIRECTION_RIGHT:
			if(isPressed(buttonValue))
				packet.setVelocityY(-1*MAX_SPEED);
			return true;

		case DIRECTION_X:
			packet.setVelocityX(layout.get(ButtonFunction.DIRECTION_X));
			return true;
		case DIRECTION_Y:
			packet.setVelocityY(layout.get(ButtonFunction.DIRECTION_Y));
			return true;

		case ORIENTATION_Y:
		case ORIENTATION_X:
			//make sure both axis are bound 
			if(!(layout.containsBinding(ButtonFunction.ORIENTATION_X)
					&& layout.containsBinding(ButtonFunction.ORIENTATION_Y))){
				logger.warning("Both orientation directions (x and y) should be bound.");
				return false;
			}
			//calculate angle
			float rad = (float) Math.atan2(layout.get(ButtonFunction.ORIENTATION_X), layout.get(ButtonFunction.ORIENTATION_Y));
			float rps = rad / 10;			//TODO fix time divesion (read it from a config or something)
			packet.setVelocityR(rps);
			return true;

		case ORIENTATION_WEST:
			if(isPressed(buttonValue))
				packet.setVelocityR(10f);	//TODO fix rad/s (read it from a config or something)
			return true;

		case ORIENTATION_EAST:
			if(isPressed(buttonValue))
				packet.setVelocityR(-10f);	//TODO fix rad/s (read it from a config or something) 
			return true;
		
		case STOP_ALL_ROBOTS:
			if(isPressed(buttonValue)){
				//TODO implementation
				//discuss possibility for a panic mode
				logger.info("Stopped all robots");
			}
			return true;
			
			
		case SELECT_NEXT_ROBOT: 
		case SELECT_PREV_ROBOT:
			if(isPressed(buttonValue))
				((ControllerListener)Services.get("ControllerListener")).changeRobotId(this, function.equals(ButtonFunction.SELECT_NEXT_ROBOT));
			return true;
			
		default:
			logger.warning("No implementation for %s.\n", button.getKey());
			return false;
		}
	}
	
	/**
	 * @return Layout for this handler
	 */
	public ControllerLayout getLayout(){
		return layout;
	}

	/**
	 * Checks of a button is being pressed
	 * 
	 * @param buttonValue value of the button to check
	 * @return succes value
	 */
	private boolean isPressed(Float buttonValue) {
		return buttonValue > 0f;
	}

	/**
	 * calculate vector speed based on a reference speed and the max speed
	 * 
	 * @param maxSpeed
	 * @param referenceSpeed
	 * @return a suitable vector speed
	 */ //TODO abstract this puke
	private float calcVector(float maxSpeed, double referenceSpeed) {
		return (float) Math.sqrt(maxSpeed*maxSpeed - referenceSpeed*referenceSpeed);
	}

	/**
	 * Fill a {@link RadioProtocolCommand.Builder RadioProtocolCommand} with data as read from assigned {@link Controller} 
	 * 
	 * @param robotID	robot that should be controlled
	 * @return Filled in {@link RadioProtocolCommand.Builder RadioProtocolCommand}
	 */
	public RadioProtocolCommand.Builder process(int robotID){
		//Initialize protobuf packet with robotID
		RadioProtocolCommand.Builder packet = RadioProtocolCommand.newBuilder();
		packet.setRobotId(robotID);
		
		//update controller values
		layout.getController().poll();

		//Make a list with all buttons that changed values
		HashMap<ButtonFunction, Float> currentButtonState = (HashMap<ButtonFunction, Float>)
				layout.getBindings().entrySet().stream()
				//filter all buttons that don't describe a strength value
				.filter(entry -> !entry.getValue().toString().contains("_STRENGTH"))
				.filter(entry -> entry.getValue().isPersistant()
							 || entry.getKey().getPollData() != previousButtonState.get(entry.getValue()))
				//and collect these to a map
				.collect(Collectors.toMap(entry -> entry.getValue(),				// buttonfunction
										 entry -> entry.getKey().getPollData()));	// polldata from abstractbutton
		//currentButtonState.entrySet().forEach(entry -> System.out.println(entry.getKey() + " -> " + entry.getValue()));
		//create a stream with current buttons
		if(!currentButtonState.entrySet().stream()
				//process each button
				.map(entry -> processInput(entry, packet))
				//check for false succes values
				.reduce(true, (accumulator, succes) -> accumulator && succes))
			logger.warning("Not every button-press was processed succesfully (controller: %s).\n", layout.getFullName());

		for(Entry<ButtonFunction, Float> entry : currentButtonState.entrySet())
			previousButtonState.put(entry.getKey(), previousButtonState.get(entry.getKey()) == entry.getValue() ? previousButtonState.get(entry.getKey()) : entry.getValue());

		//make sure the speed vector doesn't exceed MAX_SPEED
//		Point2D vectors = new Point2D(packet.getVelocityX(), packet.getVelocityY());
//		packet.setVelocityX(calcVector(MAX_SPEED, vectors.getY()));
//		packet.setVelocityY(calcVector(MAX_SPEED, vectors.getX()));

		return packet;
	}
}