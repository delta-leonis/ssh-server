package robocup.controller.handlers;

import robocup.model.Robot;
import robocup.model.World;

public class RobotInputHandler {

	private World world;

	public RobotInputHandler(World world) {
		this.world = world;
	}
	
	public void process(int robotID, int batteryStatus, long timestamp){
		Robot r = world.getAlly().getRobotByID(robotID);
		
		if(r != null){
			r.setBatteryStatus(batteryStatus,timestamp);
		}
		
	}
}
