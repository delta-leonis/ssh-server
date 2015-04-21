package robocup.controller.ai.lowLevelBehavior;

import robocup.model.FieldPoint;
import robocup.model.Robot;

public class Disturber extends Keeper {

	public Disturber(Robot robot, FieldPoint centerGoalPosition) {
		super(robot, centerGoalPosition);
	}

	/**
	 * Update the values for the disturber
	 * @param distanceToObject
	 * @param goToKick
	 * @param objectPosition
	 */
	public void update(int distanceToObject, boolean goToKick, FieldPoint objectPosition) {
		super.update(distanceToObject, goToKick, objectPosition);
	}

	@Override
	public void calculate() {
		FieldPoint newDestination = getNewKeeperDestination(ballPosition, centerGoalPosition, distanceToObject);
		changeDestination(newDestination, ballPosition);
	}
}
