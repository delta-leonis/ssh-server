package robocup.controller.ai.lowLevelBehavior;

import robocup.model.FieldObject;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.output.ComInterface;

public class GotoPositionWithBall extends LowLevelBehavior {

	private Point newPosition = null;

	public GotoPositionWithBall(Robot robot, ComInterface output, Point NewPosition) {
		super(robot, output);
		this.newPosition = NewPosition;
		// TODO Auto-generated constructor stub
	}

	public GotoPositionWithBall(Robot robot, ComInterface output, FieldObject target) {
		super(robot, output);
		this.newPosition = target.getPosition();
	}

	public Point getTarget() {
		return newPosition;
	}

	public void setTarget(Point p) {
		this.newPosition = p;
	}

	@Override
	public void calculate() {	
		if(timeOutCheck()){
				
		}else if(newPosition == null){
			robot.setOnSight(true);
			output.send(1,  robot.getRobotID(), 0, 0, 0, 0, 0, 0, false);
			return;
		}else{
			robot.setOnSight(true);
			int direction = 0;
			int travelDistance = 0;
			int newRotation = rotationToDest(newPosition);
			int speed = 0;
			int rotationSpeed = 0;
			
			if(Math.abs(newRotation) > 140){
				rotationSpeed = 350;
			}else if(Math.abs(newRotation) > 70) {
				rotationSpeed = 280;
			}else if(Math.abs(newRotation) > 30) {
				travelDistance = getDistance();
				speed = getSpeed(travelDistance, newRotation);
				if(speed == 0)
					rotationSpeed = 400;
				else
					rotationSpeed = 300;
			} else if(Math.abs(newRotation) > 10) {
				travelDistance = getDistance();
				speed = getSpeed(travelDistance, newRotation);
				if(speed == 0)
					rotationSpeed = 200;
				else
					rotationSpeed = 150;
			} else {
				travelDistance = getDistance();
				speed = getSpeed(travelDistance, newRotation);
			}
			
			if(newRotation < 0)
				rotationSpeed *= -1;
			if(travelDistance < 300 && Math.abs(newRotation) > 10)
				speed = 0;
//			output.send(1, robot.getRobotID(), 0, 0, 0, 0, 0, 0, true);

//			System.out.println(getDistance());
			if(getDistance() < 250)
				output.send(1, robot.getRobotID(), direction, speed, travelDistance, newRotation, rotationSpeed, 0, false);
			else
				output.send(1, robot.getRobotID(), direction, speed, travelDistance, newRotation, rotationSpeed, 0, false);
			}
		
	}
	public int getDistance(){
		double dx = (robot.getPosition().getX() - newPosition.getX());
		double dy = (robot.getPosition().getY() - newPosition.getY());
		return (int)Math.sqrt(dx*dx + dy*dy);
	}
	
	public int getSpeed(int distance, int rotationToDest){
		int speed = 7000;
		if( (int)(distance/1.6) < speed){
			speed = (int)(distance/1.0);
		}
		if( Math.abs(rotationToDest) > 10 ){
			speed = 900;
				if( (int)(distance/2) < speed)
					speed = (int)(distance/2);
		}
//		if(Math.abs(rotationToDest) >= 1 ){
//			speed -= (int)((float)speed / ((float)90 / (float)Math.abs(rotationToDest)));
//		}
		if(speed < 400)
			speed = 400;
//		if(Math.abs(rotationToDest) > 30){
//			speed = 0;
//		}
//		if(Math.abs(rotationToDest) > 10 && distance < 300)
//			speed = 0;

//		
//		if( Math.abs(rotationToDest) > 10 ){
////			speed = 400;
//			speed /= (Math.abs(rotationToDest)/10);
//			if( (int)(distance/2) < speed)
//				speed = (int)(distance/2);
//		}

		return speed;
	}

}
