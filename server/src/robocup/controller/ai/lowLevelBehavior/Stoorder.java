package robocup.controller.ai.lowLevelBehavior;

import robocup.controller.ai.movement.GotoPosition;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.output.ComInterface;
import robocup.model.enums.RobotMode;

/**
 * Stoorder
 * Bezet lijn tussen bal en midden van eigen goal met een offset ten opzichte van de bal
 * 
 * Describes the low-level behaviour for a Blocker Robot.
 * These Robots attempt to interrupt the enemy by getting in between the enemy {@link Robot} and the {@link Ball}
 * TODO: remove defenderPosition. Variable can be acquired through robot.getPosition(). This variable isn't even in use.
 * TODO: English-fy
 */
public class Stoorder extends LowLevelBehavior {

	protected Point ballPosition;
	protected Point opponentPosition;
	protected Point defenderPosition;
	protected int distanceToOpponent;
	protected int opponentId;

	/**
	 * Create a defender (stands between "target" enemy and the ball)
	 * @param robot The {@link Robot} that describes the blocker.
	 * @param output The {@link RobotCom} that sends the commands to the physical Robot.
	 * @param distanceToOpponent The distance the defender keeps from the enemy (center) in millimeters
	 * @param ballPosition current position of the ball. See {@link Point}
	 * @param defenderPosition current position of the defender (this robot). See {@link Point}
	 * @param opponentPosition center position of the opponent / enemy. See {@link Point}
	 * @param opponentId The Id of the opponent this Robot is trying to interrupt.
	 */
	public Stoorder(Robot robot, ComInterface output, int distanceToOpponent, Point ballPosition,
			Point defenderPosition, Point opponentPosition, int opponentId) {
		super(robot, output);

		this.opponentPosition = opponentPosition;
		this.ballPosition = ballPosition;
		this.distanceToOpponent = distanceToOpponent;
		this.defenderPosition = defenderPosition;
		this.role = RobotMode.BLOCKER;
		this.opponentId = opponentId;
		go = new GotoPosition(robot, output, defenderPosition, opponentPosition, 400);
	}

	/**
	 * Update
	 * @param distanceToOpponent The distance the defender keeps from the enemy (center) in millimeters
	 * @param ballPosition current position of the ball. See {@link Point}
	 * @param defenderPosition current position of the defender (this robot). See {@link Point}
	 * @param opponentPosition center position of the opponent / enemy. See {@link Point}
	 * @param opponentId The Id of the opponent this Robot is trying to interrupt.
	 */
	public void update(int distanceToOpponent, Point ballPosition, Point defenderPosition, Point opponentPosition,
			int opponentId) {
		this.distanceToOpponent = distanceToOpponent;
		this.ballPosition = ballPosition;
		this.defenderPosition = defenderPosition;
		this.opponentPosition = opponentPosition;
		this.opponentId = opponentId;
	}

	/**
	 * Calculates the new position to go to and attempts to make the Robot move in that direction.
	 */
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
	 * @returns the id of the Opponent this Robot is trying to block.
	 */
	public int getOpponentId() {
		return opponentId;
	}

	/**
	 * @returns the new destination we want this robot to move to. 
	 * 			The function attempts to pick a point in between the opponent we are blocking and the ball.
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
