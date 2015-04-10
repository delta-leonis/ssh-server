package robocup.controller.ai.lowLevelBehavior;

import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.output.ComInterface;

public class Disturber extends Keeper {

	public Disturber(Robot robot, ComInterface output, int distanceToBall, boolean goToKick, FieldPoint ballPosition,
			FieldPoint centerGoalPosition) {
		super(robot, output, distanceToBall, goToKick, centerGoalPosition, centerGoalPosition);
	}

	/**
	 * Update the values for the disturber
	 * @param distanceToBall
	 * @param goToKick
	 * @param ballPosition
	 */
	public void update(int distanceToBall, boolean goToKick, FieldPoint ballPosition) {
		super.update(distanceToBall, goToKick, ballPosition);
	}

	@Override
	public void calculate() {
		FieldPoint newDestination = getNewKeeperDestination(ballPosition, centerGoalPosition, distanceToObject);

		changeDestination(newDestination, ballPosition);
	}
}