package controller.ai.lowLevelBehavior;

import java.util.Calendar;
import java.util.Date;

import model.FieldObject;
import model.Point;
import model.Robot;
import output.ComInterface;

public class GotoPosition extends LowLevelBehavior {
	
	private Point newPosition;
	private FieldObject target;

	public GotoPosition(Robot robot, ComInterface output, Point newPosition){
		super(robot, output);
		this.newPosition = newPosition;
		target = null;
	}
	
	public GotoPosition(Robot robot, ComInterface output, FieldObject target){
		super(robot, output);
		newPosition = target.getPosition();
		this.target = target;
	}

	@Override
	public void calculate() {
		if(robot.getLastUpdateTime() + 1.10 < Calendar.getInstance().getTimeInMillis()/1000){
			//send stop
//			System.out.println(robot.getLastUpdateTime() - Calendar.getInstance().getTimeInMillis()/1000);
			
			if(robot.getRobotID()==11 && robot.isOnSight()){
				System.out.println("ROBOT LOST ROBOT LOST ROBOT LOST ROBOT LOST ");
				System.out.println("ROBOT LOST ROBOT LOST ROBOT LOST ROBOT LOST ");
				System.out.println("ROBOT LOST ROBOT LOST ROBOT LOST ROBOT LOST ");
				System.out.println("ROBOT LOST ROBOT LOST ROBOT LOST ROBOT LOST ");
				System.out.println("ROBOT LOST ROBOT LOST ROBOT LOST ROBOT LOST ");
				System.out.println("ROBOT LOST ROBOT LOST ROBOT LOST ROBOT LOST ");
				robot.setOnSight(false);
			}
			output.send(1, robot.getRobotID(), 0, 0, 0, 0, 0, 0, false);
		} else {
			robot.setOnSight(true);
			int direction = 0;
			int rotationSpeed = 0;
			int travelDistance = 0;
			int speed = 0;
			int newRotation = rotationToDest(newPosition);

			if(Math.abs(newRotation) > 140){
				rotationSpeed = 350;
			} else if(Math.abs(newRotation) > 100){
				rotationSpeed = 280;
			} else if( Math.abs(newRotation) > 30){
				travelDistance = getDistance();
				speed = getSpeed(travelDistance, newRotation);
				if(speed == 0)
					rotationSpeed = 200;
				else
					rotationSpeed = 120;
			} else if( Math.abs(newRotation) > 10){
				travelDistance = getDistance();
				speed = getSpeed(travelDistance, newRotation);
				if(speed == 0)
					rotationSpeed = 200;
				else
					rotationSpeed = 30;
			} else {
				travelDistance = getDistance();
				speed = getSpeed(travelDistance, newRotation);
			}
			if(newRotation > 0)
				rotationSpeed *= -1;
			output.send(1, robot.getRobotID(), direction, speed, travelDistance, newRotation, rotationSpeed, 0, true);
		}
	}
	
	public int getDistance(){
		double dx = (robot.getPosition().getX() - newPosition.getX());
		double dy = (robot.getPosition().getY() - newPosition.getY());
		return (int)Math.sqrt(dx*dx + dy*dy);
	}
	
	public int getSpeed(int distance, int rotationToDest){
		int speed = 5000;
		if( (int)(distance/1.6) < speed){
			speed = (int)(distance/1.0);
		}
		if( Math.abs(rotationToDest) > 10 ){
			speed = 400;
			if( (int)(distance/2) < speed)
				speed = (int)(distance/2);
		}
		if(speed < 200)
			speed = 200;
//		if(distance < 200){
//			speed = 0;
//		}
//		System.out.println("rotationToDest" + rotationToDest);
//		System.out.println("distance" + distance);
//		System.out.println("speed" + speed);
		return speed;
	}
}
