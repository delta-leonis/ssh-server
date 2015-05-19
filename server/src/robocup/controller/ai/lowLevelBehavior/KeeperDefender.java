package robocup.controller.ai.lowLevelBehavior;

import robocup.controller.ai.movement.GotoPosition;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.enums.RobotMode;

public class KeeperDefender extends Keeper {

	private int offset;

	public KeeperDefender(Robot robot, FieldPoint centerGoalPosition) {
		super(robot, centerGoalPosition);
		offset = 0;

		this.role = RobotMode.KEEPERDEFENDER;
		go = new GotoPosition(robot, centerGoalPosition, ballPosition, 2000);
	}

	/**
	 * Update the values for the keeper
	 * @param distanceToGoal
	 * @param goToKick
	 * @param ballPosition
	 * @param offset the offset for this keeper defender in degrees
	 */
	public void update(int distanceToGoal, boolean goToKick, FieldPoint ballPosition, int offset) {
		super.update(distanceToGoal, goToKick, ballPosition);
		this.offset = offset;
	}

	@Override
	protected FieldPoint getNewKeeperDestination(FieldPoint objectPosition, FieldPoint subjectPosition, int distance) {
		return super.getNewKeeperDestination(objectPosition, subjectPosition, distance, offset);
	}
}
