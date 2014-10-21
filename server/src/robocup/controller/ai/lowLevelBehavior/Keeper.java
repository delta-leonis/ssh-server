package robocup.controller.ai.lowLevelBehavior;

import robocup.model.FieldObject;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.output.ComInterface;

public class Keeper extends LowLevelBehavior {

	protected int distanceToGoal;
	protected boolean goToKick;
	protected Point ballPosition;
	protected Point keeperPosition;
	protected Point centerGoalPosition;
	private int yMax;
	
	/**
	 * Create a keeper
	 * @param robot
	 * @param output
	 * @param distanceToGoal defense radius size, 500 ideal in most situations
	 * @param goToKick if true, move to ball and kick it away
	 * @param ballPosition current position of the ball
	 * @param keeperPosition current position of the keeper
	 * @param centerGoalPosition center of the goal on the correct side of the playing field
	 */
	public Keeper(Robot robot, ComInterface output, int distanceToGoal, boolean goToKick, Point ballPosition,
			Point keeperPosition, Point centerGoalPosition, int yMax) {
		super(robot, output);
		this.distanceToGoal = distanceToGoal;
		this.goToKick = goToKick;
		this.ballPosition = ballPosition;
		this.keeperPosition = keeperPosition;
		this.centerGoalPosition = centerGoalPosition;
		this.yMax = yMax;
		go = new GotoPosition(robot, output, centerGoalPosition, ballPosition, 2000);
	}
	
	/**
	 * Update the values for the keeper
	 * @param distanceToGoal
	 * @param goToKick
	 * @param ballPosition
	 * @param keeperPosition
	 */
	public void update(int distanceToGoal, boolean goToKick, Point ballPosition, Point keeperPosition) {
		this.distanceToGoal = distanceToGoal;
		this.goToKick = goToKick;
		this.ballPosition = ballPosition;
		this.keeperPosition = keeperPosition;
	}

	@Override
	public void calculate() {
		if(timeOutCheck()) {
			
		} else {
			Point newDestination = getNewKeeperDestination();
			
			if(centerGoalPosition.getX() > 0 && centerGoalPosition.getX() < newDestination.getX())
				newDestination.setX(centerGoalPosition.getX() > 0 ? centerGoalPosition.getX() - 100 : centerGoalPosition.getX() + 100);
			
			if(newDestination.getY() > 0 && newDestination.getY() > yMax - 100)
				newDestination.setY(yMax - 100);
			else if(newDestination.getY() < 0 && newDestination.getY() < -yMax + 100)
				newDestination.setY(-yMax + 100);
			
			if(newDestination != null) {
				if(goToKick)
					go.setGoal(ballPosition);//GotoPosition(keeperPosition, ballPosition, ballPosition)
				else if(isWithinRange(robot, newDestination, 15))
					go.setGoal(null);
				else
					go.setGoal(newDestination);//GotoPosition(keeperPosition, newDestination, ballPosition)

				go.setTarget(ballPosition);
				go.calculate();
			}
		}
	}
	
	protected Point getNewKeeperDestination() {
		Point newDestination = null;
		
		if(ballPosition != null) {
			int angle = Math.abs(centerGoalPosition.getAngle(ballPosition));
			int realAngle = angle > 90 ? 180 - angle : angle;
			
			double dx = Math.sin(Math.toRadians(realAngle)) * distanceToGoal;
			double dy = Math.sqrt(distanceToGoal * distanceToGoal - dx * dx);
			
			int centerGoalX = (int) centerGoalPosition.getX();
			int destX = (int) (centerGoalX > 0 ? centerGoalX - dx : centerGoalX + dx);
			
			int centerGoalY = (int) centerGoalPosition.getY();
			int destY = (int) (ballPosition.getY() > 0 ? centerGoalY + dy : centerGoalY - dy);
		
			newDestination = new Point(destX, destY);
		}

		return newDestination;
	}
}