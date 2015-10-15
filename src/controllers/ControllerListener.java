package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import model.enums.ButtonFunction;
import output.Communicator;
import protobuf.Radio.RadioProtocolCommand;
import services.Service;
import util.Logger;

public class ControllerListener extends Service {
	private HashMap<Integer, ControllerHandler> handlers = new HashMap<Integer, ControllerHandler>();
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
	
	
	public boolean processControllers(){
		return Communicator.send((ArrayList<RadioProtocolCommand.Builder>) 
								handlers.entrySet().stream()
									.filter(entry -> isAssigned(entry.getKey()))
									.map(entry -> entry.getValue().process(entry.getKey()))
									.collect(Collectors.toList()));
	}
	
}