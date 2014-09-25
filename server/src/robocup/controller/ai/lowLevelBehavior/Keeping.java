package robocup.controller.ai.lowLevelBehavior;

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
//		ball = world.getBall();
	}

	@Override
	public void calculate() {
		if(!timeOutCheck()) {
//			Point dest = getBallDestination();
//			int x = (int) dest.getX();
			
//			if(x < 600 && x > -600)
//				System.out.println("Ball is rolling towards defenceLine, keeper is intercepting");
			
			
			
//			if(x < 350 && x > -350)
//				System.out.println("Ball is rolling towards goal at pos: " + dest);
			/**robot.setOnSight(true);
			ballDestination = getNewKeepingDestination();
			if(ballDestination != null) {
				int direction = robot.getPosition().getX() < ballDestination.getX() ? 90 : 270;
				
				output.send(0, robot.getRobotID(), direction, getSpeed(), 0, 0, 0, 0, false);
			}*/
		}
	}
	
	/**
	 * Function to determine the preferred position of the keeper based on the 
	 * position of the ball and the position of the goal.
	 * @return destination
	 */
	public Point getNewKeepingDestination() {
		Point dest = null;

//		if(isBallOnSameSide()) {
//			dest = getBallDestination();
//			
//			if(isPointInGoal(dest))
//				System.out.println("Ball is rolling towards the goal");
//			else
//				System.out.println("Ball is not rolling towards the goal");
//		}
		
//		if(ball.getSpeed() < 0) { // ball is slow, determine position based on enemies able to shoot
//			// TODO: adjust ball speed
//			// TODO: determine where the enemy will shoot
//		} else if(isBallRollingToGoal()) { // ball is rolling towards the goal, intercept
//			dest = getBallDestination();
//			System.out.println("DANGER, enemies will attack at:\n" + dest);
//		} else { // ball isn't a danger, prepare for enemies
//			// TODO: calculate destination based on enemy forces
//		}

		return dest;
	}
	
	private boolean isBallOnSameSide() {
		return (ball.getPosition().getY() > 0 && robot.getPosition().getY() > 0) ||
				(ball.getPosition().getY() < 0 && robot.getPosition().getY() < 0);
	}

//	/**
//	 * Calculate the position where the ball will cross the edge of the field
//	 * @return
//	 */
//	private Point getBallDestination() {
//		Point currentPosition = ball.getPosition();
//		int direction = (int) ball.getDirection();
//		
//		// get the y value of the edge of the field
//		int defenceLine = world.getField().getLength() / 2 - 200;
//		
//		if(currentPosition.getY() < 0)
//			defenceLine = -defenceLine;
//		
//		int dy = defenceLine - (int) currentPosition.getY();
//		// tan(90) or tan(-90) is inf, we can assume dx is 0 in this case
//		int dx = direction == 90 || direction == -90 ? 0 : (int) (dy / Math.tan(direction));
//		
//		int destX = (int) currentPosition.getX() + dx;
//		int destY = defenceLine;
//		
//		return new Point(destX, destY);
//	}
//	
//	private boolean isPointInGoal(Point p) {
//		Goal goal = world.getField().getGoal().get(0);
//		
//		int width = (int) (Math.abs(goal.getFrontLeft().getX()) + Math.abs(goal.getFrontRight().getX()));		
//		
//		return Math.abs(p.getX()) < width / 2; 
//	}

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
