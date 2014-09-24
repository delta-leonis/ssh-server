package robocup.controller.ai.lowLevelBehavior;

import robocup.model.FieldObject;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.output.ComInterface;

public class GotoPosition extends LowLevelBehavior {

	private Point newPosition;

	public GotoPosition(Robot robot, ComInterface output, Point newPosition) {
		super(robot, output);
		// System.out.println("goto 1: " + newPosition);
		this.newPosition = newPosition;
	}

	public GotoPosition(Robot robot, ComInterface output, FieldObject target) {
		super(robot, output);
		// System.out.println("goto 2: " + target.getPosition());
		newPosition = target.getPosition();
	}

	public Point getTarget() {
		return newPosition;
	}

	public void setTarget(Point p) {
		// System.out.println(" settarget: " + p);
		newPosition = p;
	}

	@Override
	public void calculate() {
		if (timeOutCheck()) {

		} else if (newPosition == null) {
			robot.setOnSight(true);
			output.send(1, robot.getRobotID(), 0, 0, 0, 0, 0, 0, false);
			return;
		} else {
			robot.setOnSight(true);
			int direction = 0;
			int travelDistance = 0;// getDistance();
			// System.out.println(" Newpost: " + newPosition);
			int newRotation = rotationToDest(newPosition);
//			System.out.println("NewPos: " + newPosition);
			int speed = 0;// getSpeed(travelDistance, newRotation); //max 1200
			// System.out.println("S:" + getSpeed(travelDistance, newRotation) +
			// " RS: " + getRotationSpeed(speed, newRotation, travelDistance) +
			// " TD: " + travelDistance + " NR: " + newRotation);
			int rotationSpeed = 0;

			speed = getSpeed2(getDistance(), newRotation);
			float rotationSpeedFloat = getRotationSpeed(newRotation, speed);
//			System.out.println("rotationSpeed " + rotationSpeedFloat + " newro: " + newRotation);

			float tSpeed = ((45.553093f / 360) * rotationSpeedFloat);
//			System.out.println("speedRaw: " + tSpeed);
			rotationSpeed = (int) tSpeed;

			/*
			 * if(rotationSpeed > 360) { rotationSpeed = 360; }
			 */

			output.send(1, robot.getRobotID(), newRotation, speed, 200, newRotation, rotationSpeed, 0, false);
		}
	}

	// private int getRotationSpeed(int speed, int rotation, int distance){
	// int rotationSpeed = /*max*/400;
	// rotationSpeed = rotation*3*(speed/500);

	// rotationSpeed = (rotation * 1000) / distance;
	// System.out.println("1  " + rotationSpeed);
	// if(Math.abs(rotationSpeed) < 150){
	// if(rotationSpeed < 0)
	// rotationSpeed = -150;
	// else
	// rotationSpeed = 150;
	// }
	// System.out.println("2  " + rotation);
	//
	// if(Math.abs(rotation) < 5)
	// rotationSpeed = 0;
	// if(speed > 0){
	// rotationSpeed = 400;//400/((speed/100)+2)+200;
	// if(rotation < 0)
	// rotationSpeed *= -1;
	// }
	// (float)speed/200;
	// System.out.println("3  " + rotationSpeed);
	// return rotationSpeed;
	// }

	public float getRotationSpeed(int rotation, int speed) {
		//used natural logarithmic function to determine rotationSpeed;\
		double rotationLog = Math.abs(rotation);
		rotationLog = Math.log(rotationLog) / Math.log(1.1);
		rotationLog = rotationLog * 4;
	
		float rotationSpeed =  (float)rotationLog;

		if(rotationSpeed  < 0) { 
			rotationSpeed = 0;
		}
		if (rotation < 0) {
			rotationSpeed *= -1;
		}
		

		return rotationSpeed;
	}

	public int getDistance() {
		double dx = (robot.getPosition().getX() - newPosition.getX());
		double dy = (robot.getPosition().getY() - newPosition.getY());
		return (int) Math.sqrt(dx * dx + dy * dy);
	}

	public int getSpeed2(int distance, int rotation) {
		int speed = 0;
		int thresholdValue = 800;
		if(distance < thresholdValue)
		{
			speed = (int)(Math.log(distance) / Math.log(1.1)) * 8; //- robotDiameter
		} else if(Math.abs(rotation) > 10) {
			speed = (180-Math.abs(rotation)) * 8;
		} else {
			speed = 1200;
		}
		//System.out.println("speed" + speed);
		
//		int speed = 1500;
//		if (distance < 150)
//			speed = 200;
//		else if (distance < 400)
//			speed = 400;
//		else if (distance < 800)
//			speed = 800;
//		else if (distance < 1200)
//			speed = 1200;
		// if(distance < 1200) speed = 1200;

		// if(distance < 1200 && distance > 500) speed = distance;
		// else if(distance < 150) speed = 0;
		// else speed = 400;
		// System.out.println("distance: " + distance + " speed: " + speed);
		return speed;
	}

	// public int getSpeed(int distance, int rotationToDest){
	// int speed = 7000;
	// if( (int)(distance/1.6) < speed){
	// speed = (int)(distance/1.0);
	// }
	// if( Math.abs(rotationToDest) > 10 ){
	// speed = 900;
	// if( (int)(distance/2) < speed)
	// speed = (int)(distance/2);
	// }
	// // if(Math.abs(rotationToDest) >= 1 ){
	// // speed -= (int)((float)speed / ((float)90 /
	// (float)Math.abs(rotationToDest)));
	// // }
	// if(speed < 400)
	// speed = 400;
	// // if(Math.abs(rotationToDest) > 30){
	// // speed = 0;
	// // }
	// // if(Math.abs(rotationToDest) > 10 && distance < 300)
	// // speed = 0;
	//
	// //
	// // if( Math.abs(rotationToDest) > 10 ){
	// //// speed = 400;
	// // speed /= (Math.abs(rotationToDest)/10);
	// // if( (int)(distance/2) < speed)
	// // speed = (int)(distance/2);
	// // }
	//
	// return speed;
	// }
}
