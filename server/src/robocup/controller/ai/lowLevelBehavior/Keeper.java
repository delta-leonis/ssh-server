package robocup.controller.ai.lowLevelBehavior;

import robocup.controller.ai.movement.GotoPosition;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.enums.RobotMode;
import robocup.output.ComInterface;

public class Keeper extends LowLevelBehavior {

	protected int distanceToGoal;
	protected boolean goToKick;
	protected FieldPoint ballPosition;
	protected FieldPoint keeperPosition;
	protected FieldPoint centerGoalPosition;
	private int yMax;

	/**
	 * Create a keeper
	 * @param robot
	 * @param output
	 * @param distanceToGoal defense radius size, 500 ideal in most situations
	 * @param goToKick if true, move to ball and kick it away
	 * @param ballPosition current position of the ball
	 * @param keeperPosition current position of the keeper
	 * @param centerGoalPosition center of the goal on the correct side of the playing field
	 * @param yMax maximum y position on the field
	 */
	public Keeper(Robot robot, ComInterface output, int distanceToGoal, boolean goToKick, FieldPoint ballPosition,
			FieldPoint keeperPosition, FieldPoint centerGoalPosition, int yMax) {
		super(robot, output);
		this.distanceToGoal = distanceToGoal;
		this.goToKick = goToKick;
		this.ballPosition = ballPosition;
		this.keeperPosition = keeperPosition;
		this.centerGoalPosition = centerGoalPosition;
		this.yMax = yMax;
		this.role = RobotMode.KEEPER;
		go = new GotoPosition(robot, output, centerGoalPosition, ballPosition, 3000);
	}

	/**
	 * Update the values for the keeper
	 * @param distanceToGoal
	 * @param goToKick
	 * @param ballPosition
	 * @param keeperPosition
	 */
	public void update(int distanceToGoal, boolean goToKick, FieldPoint ballPosition, FieldPoint keeperPosition) {
		this.distanceToGoal = distanceToGoal;
		this.goToKick = goToKick;
		this.ballPosition = ballPosition;
		this.keeperPosition = keeperPosition;
		;
	}

	@Override
	public void calculate() {
		if (timeOutCheck()) {

		} else {
			FieldPoint newDestination = getNewKeeperDestination();

			if (centerGoalPosition.getX() > 0 && centerGoalPosition.getX() < newDestination.getX())
				newDestination.setX(centerGoalPosition.getX() > 0 ? centerGoalPosition.getX() - 100
						: centerGoalPosition.getX() + 100);

			// TODO move to gotoposition,
			// this is the implementation for a no-go zone where robots shouldn't drive (to prevent going off-camera)
			if (newDestination.getY() > 0 && newDestination.getY() > yMax - 100)
				newDestination.setY(yMax - 100);
			else if (newDestination.getY() < 0 && newDestination.getY() < -yMax + 100)
				newDestination.setY(-yMax + 100);

			if (newDestination != null) {
				if (goToKick)
					go.setDestination(ballPosition);// GotoPosition(keeperPosition,
													// ballPosition,
													// ballPosition)
				else if (isWithinRange(robot, newDestination, 15))
					go.setDestination(null);
				else
					go.setDestination(newDestination);// GotoPosition(keeperPosition,
														// newDestination,
														// ballPosition)

				go.setTarget(ballPosition);
				go.calculate();
			}
		}
	}

	protected FieldPoint getNewKeeperDestination() {
		FieldPoint newDestination = null;

		if (ballPosition != null) {
			double angle = centerGoalPosition.getAngle(ballPosition);
			double dx = Math.cos(Math.toRadians(angle)) * distanceToGoal;
			double dy = Math.sin(Math.toRadians(angle)) * distanceToGoal;

			double destX = (centerGoalPosition.getX() + dx);
			double destY = (centerGoalPosition.getY() + dy);
			newDestination = new FieldPoint(destX, destY);
		}

		return newDestination;
	}
}
