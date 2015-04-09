package robocup.controller.ai.lowLevelBehavior;

import robocup.controller.ai.movement.GotoPosition;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.enums.RobotMode;
import robocup.output.ComInterface;

public class KeeperDefender extends Keeper {

	private FieldPoint offset;

	public KeeperDefender(Robot robot, ComInterface output, int distanceToGoal, boolean goToKick,
			FieldPoint ballPosition, FieldPoint centerGoalPosition, FieldPoint offset) {
		super(robot, output, distanceToGoal, goToKick, ballPosition, centerGoalPosition);
		this.offset = offset;
		this.role = RobotMode.KEEPERDEFENDER;
		go = new GotoPosition(robot, output, centerGoalPosition, ballPosition, 2000);
	}

	/**
	 * Update the values for the keeper
	 * @param distanceToGoal
	 * @param goToKick
	 * @param ballPosition
	 * @param offset the offset Position
	 */
	public void update(int distanceToGoal, boolean goToKick, FieldPoint ballPosition, FieldPoint offset) {
		super.update(distanceToGoal, goToKick, ballPosition);
		this.offset = offset;
	}

	@Override
	protected FieldPoint getNewKeeperDestination(FieldPoint objectPosition, FieldPoint subjectPosition, int distance) {
		FieldPoint newDestination = super.getNewKeeperDestination(objectPosition, subjectPosition, distance);

		if (newDestination != null) {
			newDestination.setX(newDestination.getX() + offset.getX());
			newDestination.setY(newDestination.getY() + offset.getY());
		}

		return newDestination;
	}
}
