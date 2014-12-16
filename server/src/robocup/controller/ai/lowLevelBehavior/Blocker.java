package robocup.controller.ai.lowLevelBehavior;

import robocup.controller.ai.highLevelBehavior.forcebehavior.Mode;
import robocup.controller.ai.movement.GotoPosition;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.output.ComInterface;

public class Blocker extends LowLevelBehavior {

	protected Point ballPosition;
	protected Point opponentPosition;
	protected Point defenderPosition;
	protected int distanceToOpponent;
	protected int opponentId;

	/**
	 * Create a defender (stands between "target" enemy and the ball)
	 * @param robot
	 * @param output
	 * @param distanceToOpponent The distance the defender keeps from the enemy (center)
	 * @param ballPosition current position of the ball
	 * @param defenderPosition current position of the defender (this robot)
	 * @param opponentPosition center position of the opponent / enemy
	 */
	public Blocker(Robot robot, ComInterface output, int distanceToOpponent, Point ballPosition,
			Point defenderPosition, Point opponentPosition, int opponentId) {
		super(robot, output);

		this.opponentPosition = opponentPosition;
		this.ballPosition = ballPosition;
		this.distanceToOpponent = distanceToOpponent;
		this.defenderPosition = defenderPosition;
		this.role = Mode.roles.BLOCKER;
		this.opponentId = opponentId;
		go = new GotoPosition(robot, output, defenderPosition, opponentPosition, 400);
	}

	/**
	 * Update
	 * @param distanceToOpponent
	 * @param ballPosition
	 * @param defenderPosition
	 * @param opponentPosition
	 */
	public void update(int distanceToOpponent, Point ballPosition, Point defenderPosition, Point opponentPosition,
			int opponentId) {
		this.distanceToOpponent = distanceToOpponent;
		this.ballPosition = ballPosition;
		this.defenderPosition = defenderPosition;
		this.opponentPosition = opponentPosition;
		this.opponentId = opponentId;
	}

	@Override
	public void calculate() {
		// Only run if the robot isn't timed out
		if (!timeOutCheck()) {
			Point newDestination = getNewDestination();
			// If available, set the new destination
			if (newDestination != null) {
				go.setDestination(newDestination);
				go.setTarget(ballPosition);
				go.calculate();
			}
		}
	}

	/**
	 * Returns the opponent id
	 * @return
	 */
	public int getOpponentId() {
		return opponentId;
	}

	/**
	 * Get the destination
	 */
	private Point getNewDestination() {
		Point newDestination = null;

		// Ball has to be on the field
		if (ballPosition != null) {

			int angle = opponentPosition.getAngle(ballPosition);

			int dx = (int) (Math.sin(angle) * distanceToOpponent);
			int dy = (int) (Math.cos(angle) * distanceToOpponent);

			newDestination = new Point(opponentPosition.getX() + dx, opponentPosition.getY() + dy);
		}

		return newDestination;
	}

}
