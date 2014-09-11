package robocup.controller.ai.lowLevelBehavior;

import java.util.ArrayList;

import robocup.model.Ball;
import robocup.model.Goal;
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
			robot.setOnSight(true);
			ballDestination = getNewKeepingDestination();
			if(ballDestination != null) {
				int direction = robot.getPosition().getX() < ballDestination.getX() ? 90 : 270;
				
				output.send(0, robot.getRobotID(), direction, getSpeed(), 0, 0, 0, 0, false);
			}
		}
	}
	
	/**
	 * Function to determine the preferred position of the keeper based on the 
	 * position of the ball and the position of the goal.
	 * @return destination
	 */
	public Point getNewKeepingDestination() {
		Point dest = null;

		if(ball.getSpeed() < 1) { // ball is slow, determine position based on enemies able to shoot
			// TODO: adjust ball speed
			// TODO: determine where the enemy will shoot
		} else if(isBallRollingToGoal()) { // ball is rolling towards the goal, intercept
			dest = getBallDestination();
			System.out.println("DANGER, enemies will attack at:\n" + dest);
		} else { // ball isn't a danger, prepare for enemies
			// TODO: calculate destination based on enemy forces
		}

		return dest;
	}
	
	/**
	 * Calculate the position where the ball will cross the edge of the field
	 * @return
	 */
	private Point getBallDestination() {
		Point currentPosition = ball.getPosition();
		int direction = Math.abs((int) ball.getDirection());
		
		// get the angle used in the calculation
		int angle = direction > 90 ? 180 - direction : direction;
		
		// get the y value of the edge of the field
		int maxY = world.getField().getLength() / 2;
		
		int x = (int) currentPosition.getX();
		int y = (int) currentPosition.getY();
		
		int dy = maxY - Math.abs(y);
		// tan(90) is inf, we can assume dx is 0 in this case
		int dx = angle == 90 ? 0 : (int) (dy / Math.tan(angle));
		
		int destX = x + (direction > 90 ? dx : -dx);
		int destY = y > 0 ? maxY : -maxY;
		
		
		return new Point(destX, destY);
	}
	
	private boolean isPointInGoal(Point p) {
		ArrayList<Goal> goals = world.getField().getGoal();
		
		Goal goal = (goals.get(0).getFrontLeft().getY() > 0 && robot.getPosition().getY() > 0)
						? goals.get(0) : goals.get(1);
		
		return Math.abs(goal.getFrontLeft().getX()) > Math.abs(robot.getPosition().getX())
				&& Math.abs(goal.getFrontRight().getX()) < Math.abs(robot.getPosition().getX()); 
	}

	private boolean isBallRollingToGoal() {
		return isPointInGoal(ballDestination);
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
