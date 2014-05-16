package controller.ai.lowLevelBehavior;

import model.Point;
import model.Robot;
import output.ComInterface;

public class GotoPosition extends LowLevelBehavior {
	
	private Point newPosition;

	public GotoPosition(Robot robot, ComInterface output, Point newPosition){
		super(robot, output);
		this.newPosition = newPosition;
	}

	@Override
	public void calculate() {
		//TODO: drive while rotate with correct direction
		//TODO: drive to position while orientation is on other point
		int direction = 0;
		int newRotation = rotationToDest(newPosition);
		int rotationSpeed = 100;
		int travelDistance = 0;
		if(Math.abs(newRotation) < 3)
			travelDistance = getDistance();
		output.send(1, robot.getRobotID(), direction, this.getSpeed(travelDistance), travelDistance, newRotation, rotationSpeed, 0, false);
	}
	
	public int getDistance(){
		double dx = (robot.getPosition().getX() - newPosition.getX());
		double dy = (robot.getPosition().getY() - newPosition.getY());
		return (int)Math.sqrt(dx*dx + dy*dy);
	}
	
	public int getSpeed(int distance){
		int speed = 1500;
		if((distance*3) < speed){
			speed = (distance*3);
		}
		return speed;
	}
}
