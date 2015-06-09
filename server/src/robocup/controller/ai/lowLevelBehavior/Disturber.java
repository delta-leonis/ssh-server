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
	public void update(int distanceToObject, boolean goToKick, FieldPoint objectPosition, int offset) {
		super.update(distanceToObject, goToKick, objectPosition);
		this.offset = offset;
	}

	@Override
	public void calculate() {
		// take half of the field width
		double halfFieldWidth = World.getInstance().getField().getWidth()/2;
		// make sure the x coordinate of the ball is within the x axis of the field
		double ballX = Math.max(-halfFieldWidth, Math.min(halfFieldWidth, ballPosition.getX()));

		// take half of the field width
		double halfFieldHeight = World.getInstance().getField().getLength()/2;
		// make sure the x coordinate of the ball is within the x axis of the field
		double ballY = Math.max(-halfFieldHeight, Math.min(halfFieldHeight, ballPosition.getY()));
		// place the new x coordinate in a new fieldpoint
		FieldPoint inFieldBallPosition = new FieldPoint(ballX,	ballY);

		// calculate position
		FieldPoint newDestination = getNewKeeperDestination(inFieldBallPosition, centerGoalPosition, distanceToObject, offset);
		changeDestination(newDestination, ballPosition);
	}
}
