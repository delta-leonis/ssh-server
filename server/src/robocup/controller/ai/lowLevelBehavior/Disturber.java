package robocup.controller.ai.lowLevelBehavior;

import robocup.model.FieldPoint;
import robocup.model.Robot;

public class Disturber extends Keeper {

	private int offset;

	public Disturber(Robot robot, FieldPoint centerGoalPosition) {
		super(robot, centerGoalPosition);
		offset = 0;
	}

	/**
	 * Update the values for the disturber
	 * @param distanceToObject
	 * @param goToKick
	 * @param objectPosition
	 * @param offset
	 */
	public void update(int distanceToObject, boolean goToKick, FieldPoint objectPosition, int offset) {
		super.update(distanceToObject, goToKick, objectPosition);
		this.offset = offset;
	}

	@Override
	public void calculate() {
		FieldPoint newDestination = getNewKeeperDestination(ballPosition, centerGoalPosition, distanceToObject, offset);
		changeDestination(newDestination, ballPosition);
	}
}
