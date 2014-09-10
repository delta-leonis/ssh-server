package robocup.controller.ai.lowLevelBehavior;

import robocup.model.Ball;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.model.World;
import robocup.output.ComInterface;


public class FollowBall extends LowLevelBehavior {
	
	private Ball ball;

	public FollowBall(Robot robot, ComInterface output) {
		super(robot, output);
		ball = World.getInstance().getBall();
	}

	@Override
	public void calculate() {
		if(!timeOutCheck()) {
			robot.setOnSight(true);
			int direction = 0;
			int rotationSpeed = 0;
			int travelDistance = 0;
			int speed = 0;
			Point point = new Point(ball.getPosition().getX(), ball.getPosition().getY());
			System.out.println("X:"+ball.getPosition().getX() + " Y:" + ball.getPosition().getY());
			int newRotation = rotationToDest(point);
			
			if(Math.abs(newRotation) > 140) {
				rotationSpeed = 350;
			} else if(Math.abs(newRotation) > 100) {
				rotationSpeed = 280;
			} else if(Math.abs(newRotation) > 30) {
				travelDistance = getDistance();
				speed = getSpeed(travelDistance, newRotation);
				if(speed ==0 ) rotationSpeed = 200;
				else rotationSpeed = 30;
			} else {
				travelDistance = getDistance();
				speed = getSpeed(travelDistance, newRotation);
			}
			if(newRotation > 0) rotationSpeed *= -1;
			output.send(1,robot.getRobotID(), direction, speed, travelDistance, newRotation, rotationSpeed, 0, false);
		}
	}
	
	public int getDistance(){
		double dx = (robot.getPosition().getX() - ball.getPosition().getX());
		double dy = (robot.getPosition().getY() - ball.getPosition().getY());
		return (int)Math.sqrt(dx*dx + dy*dy);
	}
	
	public int getSpeed(int distance, int rotationToDest){
		int speed = 400;
		if((distance*20)<speed)
			speed = (distance*20);
		if(rotationToDest > 20)
			speed /= 3;
		if(speed < 200)
			speed = 200;
		
		return speed;
	}

}
