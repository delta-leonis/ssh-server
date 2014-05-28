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
		if(robot.getRobotID()==11){
			System.out.println(robot.getPosition().getX() + ":" + robot.getPosition().getY());
		}
		if(robot.getLastUpdateTime() + 0.05 < Calendar.getInstance().getTimeInMillis()/1000){
			//send stop
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
					rotationSpeed = 80;
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
			output.send(1, robot.getRobotID(), direction, speed, travelDistance, newRotation, rotationSpeed, 0, false);
		}
	}
	
	public int getDistance(){
		double dx = (robot.getPosition().getX() - newPosition.getX());
		double dy = (robot.getPosition().getY() - newPosition.getY());
		return (int)Math.sqrt(dx*dx + dy*dy);
	}
	
	public int getSpeed(int distance, int rotationToDest){
		int speed = 1000;
		if((distance*10) < speed){
		speed = (distance*10);
		}
		if( rotationToDest > 20 )
			speed /= 3;
		if(speed < 250)
			speed = 250;
		if(distance < 200){
			speed = 0;
		}
		
		return speed;
	}
}
