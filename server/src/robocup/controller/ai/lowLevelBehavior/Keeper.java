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
	protected FieldPoint centerGoalPosition;

	/**
	 * Create a keeper
	 * @param robot
	 * @param output
	 * @param distanceToGoal defense radius size, 500 ideal in most situations
	 * @param goToChip if true, move to ball and chip it away
	 * @param ballPosition current position of the ball
	 * @param centerGoalPosition center of the goal on the correct side of the playing field
	 */
	public Keeper(Robot robot, ComInterface output, int distanceToGoal, boolean goToChip, FieldPoint ballPosition,
			FieldPoint centerGoalPosition) {
		super(robot, output);
		this.distanceToGoal = distanceToGoal;
		this.goToKick = goToChip;
		this.ballPosition = ballPosition;
		this.centerGoalPosition = centerGoalPosition;
		this.role = RobotMode.KEEPER;
		go = new GotoPosition(robot, output, centerGoalPosition, ballPosition, 3000);
	}

	/**
	 * Update the values for the keeper
	 * @param distanceToGoal
	 * @param goToKick
	 * @param ballPosition
	 */
	public void update(int distanceToGoal, boolean goToKick, FieldPoint ballPosition) {
		this.distanceToGoal = distanceToGoal;
		this.goToKick = goToKick;
		this.ballPosition = ballPosition;
	}

	@Override
	public void calculate() {
		FieldPoint newDestination = getNewKeeperDestination();
		go.setTarget(ballPosition);

		if (goToKick)
			go.setDestination(ballPosition);
		else if (newDestination != null)
			go.setDestination(newDestination);

		go.calculate();
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
