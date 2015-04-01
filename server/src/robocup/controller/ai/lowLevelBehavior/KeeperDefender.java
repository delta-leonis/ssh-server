package robocup.controller.ai.lowLevelBehavior;

import robocup.model.enums.RobotMode;
import robocup.controller.ai.movement.GotoPosition;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.output.ComInterface;

/**
 * Blocked and BlockerVooruit in documentation.
 *
 */
public class KeeperDefender extends Keeper {

	private FieldPoint offset;

	public KeeperDefender(Robot robot, ComInterface output, int distanceToGoal, boolean goToKick, FieldPoint ballPosition,
			FieldPoint keeperPosition, FieldPoint centerGoalPosition, FieldPoint offset, int yMax) {
		super(robot, output, distanceToGoal, goToKick, ballPosition, keeperPosition, centerGoalPosition, yMax);
		this.offset = offset;
		this.role = RobotMode.KEEPERDEFENDER;
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
	public void update(int distanceToGoal, boolean goToKick, FieldPoint ballPosition, FieldPoint keeperPosition, FieldPoint offset) {
		super.update(distanceToGoal, goToKick, ballPosition, keeperPosition);
		this.offset = offset;
	}

	@Override
	protected FieldPoint getNewKeeperDestination() {
		FieldPoint newDestination = super.getNewKeeperDestination();

		if (newDestination != null) {
			newDestination.setX(newDestination.getX() + offset.getX());
			newDestination.setY(newDestination.getY() + offset.getY());
		}

		return newDestination;
	}
}
