package robocup.controller.ai.lowLevelBehavior;

import robocup.model.FieldObject;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.output.ComInterface;

public class KeeperDefender extends Keeper {

	private Point offset;

	public KeeperDefender(Robot robot, ComInterface output, int distanceToGoal,
			boolean goToKick, Point ballPosition, Point keeperPosition,
			Point centerGoalPosition, Point offset) {
		super(robot, output, distanceToGoal, goToKick, ballPosition, keeperPosition,
				centerGoalPosition);
		this.offset = offset;
		go = new GotoPosition(robot, output, centerGoalPosition, ballPosition, 400);
	}
	
	/**
	 * Update the values for the keeper
	 * @param distanceToGoal
	 * @param goToKick
	 * @param ballPosition
	 * @param keeperPosition
	 * @param offset the offset Position
	 */
	public void update(int distanceToGoal, boolean goToKick, Point ballPosition, Point keeperPosition, Point offset) {
		super.update(distanceToGoal, goToKick, ballPosition, keeperPosition);
		this.offset = offset;
	}

	@Override
	protected Point getNewKeeperDestination() {
		Point newDestination = null;
		
		if(ballPosition != null) {
			int angle = Math.abs(centerGoalPosition.getAngle(ballPosition));
			int realAngle = angle > 90 ? 180 - angle : angle;
			
			double dx = Math.sin(Math.toRadians(realAngle)) * distanceToGoal;
			double dy = Math.sqrt(distanceToGoal * distanceToGoal - dx * dx);
			
			int centerGoalX = (int) centerGoalPosition.getX();
			int destX = (int) ((centerGoalX > 0 ? centerGoalX - dx : centerGoalX + dx) + offset.getX());
			
			int centerGoalY = (int) centerGoalPosition.getY();
			int destY = (int) ((ballPosition.getY() > 0 ? centerGoalY + dy : centerGoalY - dy) + offset.getY());
		
			newDestination = new Point(destX, destY);
		}

		return newDestination;
	}
}
