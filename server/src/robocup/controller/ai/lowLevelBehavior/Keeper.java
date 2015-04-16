package robocup.controller.ai.lowLevelBehavior;

import robocup.controller.ai.movement.GotoPosition;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.enums.RobotMode;

public class Keeper extends LowLevelBehavior {

	protected int distanceToObject;
	protected boolean goToKick;
	protected FieldPoint ballPosition;
	protected FieldPoint centerGoalPosition;

	/**
	 * Create a keeper
	 * @param robot the keeper {@link Robot} in the model.
	 * @param distanceToGoal defense radius size, 500 ideal in most situations
	 * @param goToChip if true, move to ball and chip it away
	 * @param ballPosition current position of the ball
	 * @param centerGoalPosition center of the goal on the correct side of the playing field
	 */
	public Keeper(Robot robot, int distanceToGoal, boolean goToChip, FieldPoint ballPosition,
			FieldPoint centerGoalPosition) {
		super(robot);
		this.distanceToObject = distanceToGoal;
		this.goToKick = goToChip;
		this.ballPosition = ballPosition;
		this.centerGoalPosition = centerGoalPosition;
		this.role = RobotMode.KEEPER;
		go = new GotoPosition(robot, centerGoalPosition, ballPosition, 3000);
	}

	/**
	 * Update the values for the keeper
	 * @param distanceToGoal
	 * @param goToKick
	 * @param ballPosition
	 */
	public void update(int distanceToGoal, boolean goToKick, FieldPoint ballPosition) {
		this.distanceToObject = distanceToGoal;
		this.goToKick = goToKick;
		this.ballPosition = ballPosition;
	}

	@Override
	public void calculate() {
		FieldPoint newDestination = getNewKeeperDestination(centerGoalPosition, ballPosition, distanceToObject);

		changeDestination(newDestination, ballPosition);
	}

	protected void changeDestination(FieldPoint destination, FieldPoint target) {
		go.setTarget(ballPosition);

		if (goToKick)
			go.setDestination(ballPosition);
		else if (destination != null)
			go.setDestination(destination);

		go.calculate();
	}

	/**
	 * Calculate a new Keeper destination.
	 * The destination will be a point between the object and the subject position with a specified distance to the object position.
	 * @param objectPosition the position of the point this keeper is defending.
	 * @param subjectPosition the point which needs to be blocked
	 * @param distance the distance to the object position
	 * @return the new keeper destination
	 */
	protected FieldPoint getNewKeeperDestination(FieldPoint objectPosition, FieldPoint subjectPosition, int distance) {
		FieldPoint newDestination = null;

		if (objectPosition != null && subjectPosition != null) {
			double angle = objectPosition.getAngle(subjectPosition);
			double dx = Math.cos(Math.toRadians(angle)) * distance;
			double dy = Math.sin(Math.toRadians(angle)) * distance;

			double destX = objectPosition.getX() + dx;
			double destY = objectPosition.getY() + dy;
			newDestination = new FieldPoint(destX, destY);
		}

		return newDestination;
	}
}
