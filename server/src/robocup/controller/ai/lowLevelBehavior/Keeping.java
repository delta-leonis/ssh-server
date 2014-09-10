package robocup.controller.ai.lowLevelBehavior;

import robocup.model.Ball;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.output.ComInterface;

public class Keeping extends LowLevelBehavior {

	private Ball ball;
	private Point ballDestination;
	
	public Keeping(Robot robot, ComInterface output) {
		super(robot, output);
		ball = world.getBall();
	}

	@Override
	public void calculate() {
		
		if(!timeOutCheck()) {
			ballDestination = getBallDestination();
			// TODO: move towards the destination
		}
	}
	
	/**
	 * Function to determine the preferred position of the keeper based on the 
	 * position of the ball and the position of the goal.
	 * @return destination
	 */
	public Point getBallDestination() {
		Point dest = null;
		
		if(ball.getSpeed() < 100) { // ball is slow, determine position based on enemies able to shoot
			// TODO: determine where the enemy will shoot
		} else if(isBallRollingToGoal()) { // ball is rolling towards the goal, intercept
			
		} else { // ball isn't a danger, prepare for enemies
			// TODO: calculate destination based on enemy forces
		}
		
		
		return dest;
	}

	private boolean isBallRollingToGoal() {
		// TODO Auto-generated method stub
		return true;
	}

	public int getDistance() {
		double dx = (robot.getPosition().getX() - ballDestination.getX());
		double dy = (robot.getPosition().getY() - ballDestination.getY());
		return (int)Math.sqrt(dx*dx + dy*dy);
	}
	
	public int getSpeed() {
		int speed = 800;
		// TODO: find a stable speed
		return speed;
	}
}
