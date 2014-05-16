package controller.handlers;

import model.Robot;
import model.World;

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
