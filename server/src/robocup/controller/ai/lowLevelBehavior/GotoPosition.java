package robocup.controller.ai.lowLevelBehavior;

import robocup.model.FieldObject;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.output.ComInterface;

public class GotoPosition extends LowLevelBehavior {
	
	private Point newPosition;
	

	public GotoPosition(Robot robot, ComInterface output, Point newPosition){
		super(robot, output);
//		System.out.println("goto 1: " + newPosition);
		this.newPosition = newPosition;
	}
	
	public GotoPosition(Robot robot, ComInterface output, FieldObject target){
		super(robot, output);
//		System.out.println("goto 2: " + target.getPosition());
		newPosition = target.getPosition();
	}

	public Point getTarget(){
		return newPosition;
	}
	
	public void setTarget(Point p){
//		System.out.println(" settarget: " + p);
		newPosition = p;
	}
	
	@Override
	public void calculate() {
		if(timeOutCheck()) {

		} else if(newPosition == null){
			robot.setOnSight(true);
			output.send(1, robot.getRobotID(), 0, 0, 0, 0, 0, 0, false); 
			return;
		} else {
			robot.setOnSight(true);
			int direction = 0;
			int travelDistance = 0;//getDistance();
//			System.out.println(" Newpost: " + newPosition);
			int newRotation = rotationToDest(newPosition);
			int speed = 0;//getSpeed(travelDistance, newRotation); //max 1200
//			System.out.println("S:" + getSpeed(travelDistance, newRotation) + " RS: " + getRotationSpeed(speed, newRotation, travelDistance) + " TD: " + travelDistance + " NR: " + newRotation);
			int rotationSpeed = 0;//getRotationSpeed(speed, newRotation, travelDistance);

//			if(Math.abs(newRotation) > 140){
//				rotationSpeed = 350;
//			} else if(Math.abs(newRotation) > 70){
//				rotationSpeed = 280;
//			} else if( Math.abs(newRotation) > 30){
//				travelDistance = getDistance();
//				speed = getSpeed(travelDistance, newRotation);
//				if(speed == 0)
//					rotationSpeed = 400;
//				else
//					rotationSpeed = 300;
////				rotationSpeed = 350;
//////				if(speed>1000)
//////					rotationSpeed = 300;
//			} else if( Math.abs(newRotation) > 10){
//				travelDistance = getDistance();
//				speed = getSpeed(travelDistance, newRotation);
//				if(speed == 0)
//					rotationSpeed = 200;
//				else
//					rotationSpeed = 150;
////				rotationSpeed = 200;
//////				if(travelDistance > 300)
//////					speed = 0;
//			}
//			else {
//				travelDistance = getDistance();
//				speed = getSpeed(travelDistance, newRotation);
//			}
//			if(travelDistance < 300 && Math.abs(newRotation) > 10)
//				speed = 0;
			
//			if(Math.abs(newRotation) > 140) rotationSpeed = 720;
//			else if(Math.abs(newRotation) > 70) rotationSpeed = 360;
//			else if(Math.abs(newRotation) > 10) rotationSpeed = 180;
//			else rotationSpeed = 0;
			
			
			if(newRotation < 0)
				rotationSpeed *= -1;
			
			
			
			
//			System.out.println(newRotation);
//			if(Math.abs(newRotation) > 10 && getDistance() > 1200){
//				speed = 600;
//				rotationSpeed = (speed * newRotation*10) / getDistance();
//			}
//			else {
//				speed = getSpeed2(getDistance());
//				rotationSpeed = (speed * newRotation) / getDistance();
//			}
			rotationSpeed = (speed * newRotation) / getDistance();
			System.out.println(rotationSpeed);
			speed = getSpeed2(getDistance());
			 //rotationSpeed = rotatie / afstand / snelheid; oftewel (rotatie * speed) / afstand        //add multiplier to increase slightly e.g. 1.1
//			System.out.println(rotationSpeed);
//			if(travelDistance < 300)
//				speed = 0;
			//System.out.println("RID: " + robot.getRobotID() + " S:" + speed + " RS: " + rotationSpeed + " TD: " + travelDistance + " NR: " + newRotation);
			//output.send(1, robot.getRobotID(), newRotation, 800, 0, 0, 0, 0, false);
			//System.out.println(newRotation);
			output.send(1, robot.getRobotID(), newRotation, speed, 500, newRotation, 0, 0, false);
		}
	}
	
//	private int getRotationSpeed(int speed, int rotation, int distance){
//		int rotationSpeed = /*max*/400;
//		rotationSpeed = rotation*3*(speed/500);
		
//		rotationSpeed = (rotation * 1000) / distance;
//		System.out.println("1  " + rotationSpeed);
//		if(Math.abs(rotationSpeed) < 150){
//			if(rotationSpeed < 0)
//				rotationSpeed = -150;
//			else
//				rotationSpeed = 150;
//		}
//		System.out.println("2  " + rotation);
//
//		if(Math.abs(rotation) < 5)
//			rotationSpeed = 0;
//		if(speed > 0){
//			rotationSpeed = 400;//400/((speed/100)+2)+200; 
//			if(rotation < 0)
//				rotationSpeed *= -1;
//		}
				//(float)speed/200;
//		System.out.println("3  " + rotationSpeed);
//		return rotationSpeed;
//	}
	
	public int getDistance(){
		double dx = (robot.getPosition().getX() - newPosition.getX());
		double dy = (robot.getPosition().getY() - newPosition.getY());
		return (int)Math.sqrt(dx*dx + dy*dy);
	}
	
	public int getSpeed2(int distance)
	{
		int speed = 1500;
		if(distance < 1200) speed = distance;
		if(distance < 300) speed = 300;
//		System.out.println("distance: " + distance + " speed: " + speed);
		return speed;
	}
	
//	public int getSpeed(int distance, int rotationToDest){
//		int speed = 7000;
//		if( (int)(distance/1.6) < speed){
//			speed = (int)(distance/1.0);
//		}
//		if( Math.abs(rotationToDest) > 10 ){
//			speed = 900;
//				if( (int)(distance/2) < speed)
//					speed = (int)(distance/2);
//		}
////		if(Math.abs(rotationToDest) >= 1 ){
////			speed -= (int)((float)speed / ((float)90 / (float)Math.abs(rotationToDest)));
////		}
//		if(speed < 400)
//			speed = 400;
////		if(Math.abs(rotationToDest) > 30){
////			speed = 0;
////		}
////		if(Math.abs(rotationToDest) > 10 && distance < 300)
////			speed = 0;
//
////		
////		if( Math.abs(rotationToDest) > 10 ){
//////			speed = 400;
////			speed /= (Math.abs(rotationToDest)/10);
////			if( (int)(distance/2) < speed)
////				speed = (int)(distance/2);
////		}
//
//		return speed;
//	}
}
