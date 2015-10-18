package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.java.games.input.Controller;
import output.Communicator;
import protobuf.Radio.RadioProtocolCommand;
import services.Service;
import util.BiMap;
import util.Logger;

/**
 * A {@link Service} that manages all {@link ControllerHandler controllers} for every robotId
 * 
 * @author Jeroen
 *
 */
public class ControllerListener extends Service {
	/**
	 * List of handlers<br/>
	 *  - Integer describes robotId<br/>
	 *  - ControllerHandler is the associated handler for that id, null if not bound<br/>
	 */
	private BiMap<Integer, ControllerHandler> handlers = new BiMap<Integer, ControllerHandler>();
	/**
	 * List to temporarily store any changes in the handlers list. Is processed every {@link #processControllers()} call. 
	 * Whenever handlers should be updated, tmpHandlers != null. 
	 */
	private BiMap<Integer, ControllerHandler> tmpHandlers;
	//respective logger
	private Logger logger = Logger.getLogger();
	
	/**
	 * Create a controllerlistener
	 * 
	 * @param noRobots	total number of robots		TODO remove this puke
	 */
	public ControllerListener(int noRobots) {
		super("ControllerListener");
		//TODO remove noRobots, read it from Models or something
		IntStream.range(0, noRobots).forEach(index -> handlers.put(index, null));
	}
	
	/**
	 * Register a specific {@link ControllerLayout} to a robotId
	 * 
	 * @param robotId		robotid to bound to the given {@link ControllerLayout}
	 * @param controller 	Controllerlayout to bind
	 * @return succes value
	 */
	public boolean register(int robotId, ControllerLayout controller){
		//check if the robotId is allready bound
		if(isAssigned(robotId)){
			logger.warning("Handler for %d is not empty, and will be overwriten.\n", robotId);
			unregister(robotId);
		}
		
		//check if all essential buttons are bound for this controller
		if(!controller.isComplete()){
			logger.warning("Could not register controller, essential buttons are not bound.");
			return false;
		}
	
		handlers.put(robotId, new ControllerHandler(controller));
		return true;
	}

	/**
	 * unregister the handler for a specific robotId
	 * @param robotId robotId to unregister a handler for
	 * @return succes value
	 */
	public boolean unregister(int robotId) {
		//check if it is assigned at all
		if(!isAssigned(robotId)){
			logger.warning("Could not unregister controllerHandler for robot %d.\n", robotId);
			return false;
		}

		//replace the handler with null, let the garbage collector pick up te pieces
		handlers.put(robotId, null);
		return true;
	}

	/**
	 * @param robotId robotId to check
	 * @return whether given id has a handler (thus a controller) assigned
	 */
	private boolean isAssigned(int robotId) {
		return handlers.get(robotId) != null;
	}
	
	/**
	 * @param controller controller to check
	 * @return whether given controller is in use
	 */
	public boolean containsController(Controller controller){
		return handlers.entrySet().stream()
				//Only check handlers that actually have a handler assigned to a id
				.filter(entry -> entry.getValue() != null)
				//filter controllers that equal to a assigned controller
				.filter(entry -> entry.getValue().getLayout().getController().equals(controller))
				//found any? Then it is in use
				.count() > 0;
	}

	/**
	 * give the next or previous available robotid
	 * 
	 * @param currentIndex
	 * @param forward	give next available id on true, give previous on false
	 */
	public void changeRobotId(ControllerHandler handler, boolean forward){
		int currentIndex = handlers.getbyValue(handler);
		tmpHandlers = handlers.clone();
		
		//free the id
		tmpHandlers.put(currentIndex, null);

		List<Integer> list = tmpHandlers.entrySet().stream()
				.filter(entry -> entry.getValue() == null)
				.map(entry -> entry.getKey())
				.collect(Collectors.toList());

		ListIterator<Integer> it = list.listIterator();
		while(it.next() != currentIndex);

		int robotId = 0;
		if(forward)
			robotId = it.hasNext() ? it.next() : list.get(0); 
		else{
			//since it currently between the current, and the next possible id,
			//it should go to the previous value: between the previous possible id, and the current id
			it.previous();
			robotId = it.hasPrevious() ? it.previous() : list.get(list.size()-1);
		}
			
		if(tmpHandlers.get(robotId) != null){
			logger.info("Could not find a available robotid.");
			return;
		}
		tmpHandlers.put(robotId, handler);
	}

	/**
	 * Process every controller and its input
	 * 
	 * TODO don't give it to the Communicator straight away
	 * 
	 * @return succes value
	 */
	public boolean processControllers(){
		//check if the list should change
		handlers = tmpHandlers == null ? handlers : tmpHandlers;
		tmpHandlers = null;

		return Communicator.send((ArrayList<RadioProtocolCommand.Builder>) 
								//get all handlers
								handlers.entrySet().stream()
									// only proces handlers with a ControllerHandler assigned
									.filter(entry -> isAssigned(entry.getKey()))
									// process every ControlelrHandler
									.map(entry -> entry.getValue().process(entry.getKey()))
									//collect the packets to a list
									.collect(Collectors.toList()));
	}
	
}