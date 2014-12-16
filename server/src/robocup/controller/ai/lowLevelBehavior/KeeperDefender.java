package robocup.controller.ai.lowLevelBehavior;

import robocup.controller.ai.highLevelBehavior.forcebehavior.Mode;
import robocup.controller.ai.movement.GotoPosition;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.output.ComInterface;

public class KeeperDefender extends Keeper {

	private Point offset;

	public KeeperDefender(Robot robot, ComInterface output, int distanceToGoal, boolean goToKick, Point ballPosition,
			Point keeperPosition, Point centerGoalPosition, Point offset, int yMax) {
		super(robot, output, distanceToGoal, goToKick, ballPosition, keeperPosition, centerGoalPosition, yMax);
		this.offset = offset;
		this.role = Mode.roles.DEFENDER;
		go = new GotoPosition(robot, output, centerGoalPosition, ballPosition, 2000);
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
		Point newDestination = super.getNewKeeperDestination();

		if (newDestination != null) {
			newDestination.setX(newDestination.getX() + offset.getX());
			newDestination.setY(newDestination.getY() + offset.getY());
		}

		return newDestination;
	}
}
