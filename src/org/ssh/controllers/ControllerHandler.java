package org.ssh.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.ssh.Models;
import org.ssh.Services;
import org.ssh.models.Model;
import org.ssh.models.Robot;
import org.ssh.models.enums.ButtonFunction;
import org.ssh.models.enums.ProducerType;
import org.ssh.services.Producer;
import org.ssh.util.Logger;

import net.java.games.input.Controller;
import protobuf.Radio.RadioProtocolCommand;

/**
 * This class is used to process a {@link ControllerLayout Controller} {@link Model} and generate a
 * protobuf message accordingly
 *
 * @TODO read max_speed and solandoid_speed from a config file
 * @TODO calc speed vectors
 * @TODO stop_all_robots
 * @TODO calc rad/s
 *       
 * @author Jeroen de Jong
 *         
 */
public class ControllerHandler extends Producer {
    
    // TODO should be read from a config of some sorts
    float                                    MAX_SPEED           = 4f,
                                                     MAX_STRENGTH = 1f;
                                                     
    /**
     * Map with previous buttonstates and respective values
     */
    private final Map<ButtonFunction, Float> previousButtonState = new HashMap<ButtonFunction, Float>();
    /**
     * Controllerlayout that should be used for inputprocessing
     */
    private final ControllerLayout           layout;
    // respective logger
    private final static Logger              LOG                 = Logger.getLogger();
                                                                 
    /**
     * Instantiates a handler assigned to a specific layout
     * 
     * @param layout
     */
    public ControllerHandler(final ControllerLayout layout) {
        super("ControllerHandler " + layout.getController().getName(), ProducerType.SCHEDULED);
        // fill previousButtonState array
        resetPreviousState();
        
        this.layout = layout;
    }
    
    /**
     * calculate vector speed based on a reference speed and the max speed
     * 
     * @param maxSpeed
     * @param referenceSpeed
     * @return a suitable vector speed
     */ // TODO abstract this puke
    private float calcVector(final float maxSpeed, final double referenceSpeed) {
        return (float) Math.sqrt((maxSpeed * maxSpeed) - (referenceSpeed * referenceSpeed));
    }
    
    /**
     * @return Layout for this handler
     */
    public ControllerLayout getLayout() {
        return this.layout;
    }
    
    /**
     * Sets all possible binding values to zero.
     */
    private void resetPreviousState() {
        layout.getBindings().entrySet().forEach(entry -> this.previousButtonState.put(entry.getValue(), 0f));
    }
    
    /**
     * Checks of a button is being pressed
     * 
     * @param buttonValue
     *            value of the button to check
     * @return succes value
     */
    private boolean isPressed(final Float buttonValue) {
        return buttonValue > 0f;
    }
    
    /**
     * Fill a {@link RadioProtocolCommand.Builder RadioProtocolCommand} with data as read from
     * assigned {@link Controller}
     * 
     * @param robotID
     *            robot that should be controlled
     * @return Filled in {@link RadioProtocolCommand.Builder RadioProtocolCommand}
     */
    public RadioProtocolCommand.Builder process(final int robotID) {
        // Initialize protobuf packet with robotID
        final RadioProtocolCommand.Builder packet = RadioProtocolCommand.newBuilder();
        packet.setRobotId(robotID);
        
        // update controller values
        this.layout.getController().poll();
        
        // Make a list with all buttons that changed values
        final Map<ButtonFunction, Float> currentButtonState = (HashMap<ButtonFunction, Float>) this.layout.getBindings()
                .entrySet().stream()
                // filter all buttons that don't describe a strength value
                .filter(entry -> !entry.getValue().toString().contains("_STRENGTH"))
                .filter(entry -> entry.getValue().isPersistant()
                        || (entry.getKey().getPollData() != this.previousButtonState.get(entry.getValue())))
                // and collect these to a map
                .collect(Collectors.toMap(entry -> entry.getValue(), // buttonfunction
                        entry -> entry.getKey().getPollData())); // polldata from abstractbutton

        // create a stream with current buttons
        if (!currentButtonState.entrySet().stream()
                // process each button
                .map(entry -> this.processInput(entry, packet))
                // check for false succes values
                .reduce(true, (accumulator, succes) -> accumulator && succes))
            ControllerHandler.LOG.warning("Not every button-press was processed succesfully (controller: %s).\n",
                    this.layout.getFullName());
                    
        for (final Entry<ButtonFunction, Float> entry : currentButtonState.entrySet())
            this.previousButtonState.put(entry.getKey(),
                    this.previousButtonState.get(entry.getKey()).compareTo(entry.getValue()) == 0
                            ? this.previousButtonState.get(entry.getKey()) : entry.getValue());
                            
        // make sure the speed vector doesn't exceed MAX_SPEED
        // Point2D vectors = new Point2D(packet.getVelocityX(), packet.getVelocityY());
        // packet.setVelocityX(calcVector(MAX_SPEED, vectors.getY()));
        // packet.setVelocityY(calcVector(MAX_SPEED, vectors.getX()));
        
        return packet;
    }
    
    /**
     * Mutate the given {@link RadioProtocolCommand.Builder packet} with modifier given as
     * 
     * @param button
     * @param packet
     *            {@link RadioProtocolCommand.Builder packet} to mutate
     * @return
     */
    private boolean processInput(final Entry<ButtonFunction, Float> button, final RadioProtocolCommand.Builder packet) {
        final ButtonFunction function = button.getKey();
        final Float buttonValue = button.getValue();
        
        // switch to the right function handling
        switch (function) {
            case KICK:
                if (this.isPressed(buttonValue)) {
                    final float kickStrength = this.layout.containsBinding(ButtonFunction.KICK_STRENGTH)
                            ? this.layout.get(ButtonFunction.KICK_STRENGTH) : this.MAX_STRENGTH;
                    packet.setFlatKick(kickStrength);
                }
                return true;
                
            case CHIP:
                if (this.isPressed(buttonValue)) {
                    final float chipStrength = this.layout.containsBinding(ButtonFunction.CHIP_STRENGTH)
                            ? this.layout.get(ButtonFunction.CHIP_STRENGTH) : this.MAX_STRENGTH;
                    packet.setChipKick(chipStrength);
                }
                return true;
                
            case DRIBBLE:
                packet.setDribblerSpin(buttonValue);
                return true;
                
            case DRIBBLE_TOGGLE:
                if (this.isPressed(buttonValue)) {
                    // TODO Get robot of the right team
                    final Optional<Model> oRobot = Models.get("robot B" + packet.getRobotId());
                    if (!oRobot.isPresent())
                        ControllerHandler.LOG.info("Could not find robot %d", packet.getRobotId());
                    else {
                        final float dribbleSpeed = ((Robot) oRobot.get()).getDribbleSpeed() == 0f ? 1f : 0f;
                        packet.setDribblerSpin(dribbleSpeed);
                    }
                }
                return true;
                
            case DRIBBLE_SPEED:
                final float dribbleSpeed = this.layout.get(ButtonFunction.DRIBBLE_SPEED);
                packet.setDribblerSpin(dribbleSpeed);
                return true;
                
            case DIRECTION_POV:
                if (this.isPressed(buttonValue)) {
                    packet.setVelocityY((float) Math.sin(buttonValue * 2 * Math.PI) * this.MAX_SPEED);
                    packet.setVelocityX((float) Math.cos(buttonValue * 2 * Math.PI) * -this.MAX_SPEED);
                }
                return true;
                
            case DIRECTION_FORWARD:
                if (this.isPressed(buttonValue)) packet.setVelocityX(this.MAX_SPEED);
                return true;
                
            case DIRECTION_BACKWARD:
                if (this.isPressed(buttonValue)) packet.setVelocityX(-1 * this.MAX_SPEED);
                return true;
                
            case DIRECTION_LEFT:
                if (this.isPressed(buttonValue)) packet.setVelocityY(this.MAX_SPEED);
                return true;
                
            case DIRECTION_RIGHT:
                if (this.isPressed(buttonValue)) packet.setVelocityY(-1 * this.MAX_SPEED);
                return true;
                
            case DIRECTION_X:
                packet.setVelocityX(this.layout.get(ButtonFunction.DIRECTION_X));
                return true;
            case DIRECTION_Y:
                packet.setVelocityY(this.layout.get(ButtonFunction.DIRECTION_Y));
                return true;
                
            case ORIENTATION_Y:
            case ORIENTATION_X:
                // make sure both axis are bound
                if (!(this.layout.containsBinding(ButtonFunction.ORIENTATION_X)
                        && this.layout.containsBinding(ButtonFunction.ORIENTATION_Y))) {
                    ControllerHandler.LOG.warning("Both orientation directions (x and y) should be bound.");
                    return false;
                }
                // calculate angle
                final float rad = (float) Math.atan2(this.layout.get(ButtonFunction.ORIENTATION_X),
                        this.layout.get(ButtonFunction.ORIENTATION_Y));
                // TODO fix time divesion (read it from a config or something)
                final float rps = rad / 10;
                packet.setVelocityR(rps);
                return true;
                
            case ORIENTATION_WEST:
                if (this.isPressed(buttonValue)) packet.setVelocityR(10f); // TODO fix rad/s (read
                                                                           // it from a config or
                                                                           // something)
                return true;
                
            case ORIENTATION_EAST:
                if (this.isPressed(buttonValue)) packet.setVelocityR(-10f); // TODO fix rad/s (read
                                                                            // it from a config or
                                                                            // something)
                return true;
                
            case STOP_ALL_ROBOTS:
                if (this.isPressed(buttonValue)) {
                    // TODO implementation
                    // discuss possibility for a panic mode
                    ControllerHandler.LOG.info("Stopped all robots");
                }
                return true;
                
            case SELECT_NEXT_ROBOT:
            case SELECT_PREV_ROBOT:
                if (this.isPressed(buttonValue)) ((ControllerListener) Services.get("ControllerListener").get())
                        .changeRobotId(this, function.equals(ButtonFunction.SELECT_NEXT_ROBOT));
                return true;
                
            default:
                ControllerHandler.LOG.info("No implementation for %s.\n", button.getKey());
                return false;
        }
    }
}