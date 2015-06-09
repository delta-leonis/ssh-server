package robocup.controller.ai.lowLevelBehavior;

import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.World;

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
	public void update(int distanceToObject, boolean goToKick, FieldPoint objectPosition, int offset, double fieldWidth, double fieldHeight) {
		super.update(distanceToObject, goToKick, objectPosition, fieldWidth, fieldHeight);
		
		this.offset = offset;
	}

	@Override
	public void calculate() {
		// calculate position
		FieldPoint newDestination = getNewKeeperDestination(cropFieldPosition(ballPosition), centerGoalPosition, distanceToObject, offset);
		changeDestination(newDestination, ballPosition);
	}
}
