package robocup.controller.ai.lowLevelBehavior;

import java.util.Calendar;
import java.util.logging.Logger;

import robocup.Main;
import robocup.model.FieldObject;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.output.ComInterface;

public class GotoPosition extends LowLevelBehavior {
	
	private Point newPosition;
	private static Logger LOGGER = Logger.getLogger(Main.class.getName());

	public GotoPosition(Robot robot, ComInterface output, Point newPosition){
		super(robot, output);
		this.newPosition = newPosition;
	}
	
	public GotoPosition(Robot robot, ComInterface output, FieldObject target){
		super(robot, output);
		newPosition = target.getPosition();
	}

	@Override
	public void calculate() {
		if(robot.getLastUpdateTime() + 1.10 < Calendar.getInstance().getTimeInMillis()/1000){
			robot.setOnSight(false);
			LOGGER.warning("Robot " + robot.getRobotID() + " is not on sight");
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
			output.send(1, robot.getRobotID(), direction, speed, travelDistance, newRotation, rotationSpeed, 0, false);
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
			speed = (int)(distance/1.6);
		}
		if( Math.abs(rotationToDest) > 10 ){
			speed = 400;
			if( (int)(distance/2) < speed)
				speed = (int)(distance/2);
		}
		if(speed < 200)
			speed = 200;
		return speed;
	}
}
