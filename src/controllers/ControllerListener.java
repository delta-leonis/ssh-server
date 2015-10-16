package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import model.enums.ButtonFunction;
import output.Communicator;
import protobuf.Radio.RadioProtocolCommand;
import services.Service;
import util.BiMap;
import util.Logger;

public class ControllerListener extends Service {
	private BiMap<Integer, ControllerHandler> handlers = new BiMap<Integer, ControllerHandler>();
	private Logger logger = Logger.getLogger();
	
	public ControllerListener(int noRobots) {
		super("ControllerListener");
		//TODO remove noRobots, read it from Models or something
		IntStream.range(0, noRobots).forEach(index -> handlers.put(index, null));
	}
	
	public boolean register(int robotId, ControllerLayout controller){
		if(isAssigned(robotId)){
			logger.warning("Handler for %d is not empty, and will be overwriten.\n", robotId);
			unregister(robotId);
		}
		
		if(!controller.isComplete()){
			logger.warning("Could not register controller, essential buttons are not bound.");
			return false;
		}
	
		handlers.put(robotId, new ControllerHandler(controller));
		return true;
	}

	private boolean unregister(int robotId) {
		if(!isAssigned(robotId)){
			logger.warning("Could not unregister controllerHandler for robot %d.\n", robotId);
			return false;
		}

		handlers.put(robotId, null);
		return true;
	}

	private boolean isAssigned(int robotId) {
		return handlers.get(robotId) != null;
	}

	/**
	 * give the next or previous available robotid
	 * 
	 * @param currentIndex
	 * @param forward	give next available id on true, give previous on false
	 */
	public void changeRobotId(ControllerHandler handler, boolean forward){
		int currentIndex = handlers.getbyValue(handler);
		
		//free the id
		handlers.put(currentIndex, null);

		Stream<Entry<Integer, ControllerHandler>> availableHandlers = handlers.entrySet().stream().filter(entry -> entry.getValue() != null);
		handlers.put(forward ? availableHandlers
								.filter(entry -> entry.getKey() <= currentIndex)
								.findFirst().get().getKey()
							: availableHandlers
								.filter(entry -> entry.getKey() >= currentIndex)
								.findFirst().get().getKey()
						, handler);
	}

	public boolean processControllers(){
		return Communicator.send((ArrayList<RadioProtocolCommand.Builder>) 
								handlers.entrySet().stream()
									.filter(entry -> isAssigned(entry.getKey()))
									.map(entry -> entry.getValue().process(entry.getKey()))
									.collect(Collectors.toList()));
	}
	
}