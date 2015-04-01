package robocup.controller.ai.lowLevelBehavior;

import robocup.controller.ai.movement.GotoPosition;
import robocup.model.Ball;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.enums.RobotMode;
import robocup.output.ComInterface;
import robocup.output.RobotCom;

/**
 * Stoorder
 * Bezet lijn tussen bal en midden van eigen goal met een offset ten opzichte van de bal
 * 
 * Describes the low-level behaviour for a Blocker Robot.
 * These Robots attempt to interrupt the enemy by getting in between the enemy {@link Robot} and the {@link Ball}
 * TODO: remove defenderPosition. Variable can be acquired through robot.getPosition(). This variable isn't even in use.
 * TODO: English-fy
 */
public class Coverer extends LowLevelBehavior {

	protected FieldPoint ballPosition;
	protected FieldPoint opponentPosition;
	protected FieldPoint defenderPosition;
	protected int distanceToOpponent;
	protected int opponentId;

	/**
	 * Create a defender (stands between "target" enemy and the ball)
	 * @param robot The {@link Robot} that describes the blocker.
	 * @param output The {@link RobotCom} that sends the commands to the physical Robot.
	 * @param distanceToOpponent The distance the defender keeps from the enemy (center) in millimeters
	 * @param ballPosition current position of the ball. See {@link FieldPoint}
	 * @param defenderPosition current position of the defender (this robot). See {@link FieldPoint}
	 * @param opponentPosition center position of the opponent / enemy. See {@link FieldPoint}
	 * @param opponentId The Id of the opponent this Robot is trying to interrupt.
	 */
	public Coverer(Robot robot, ComInterface output, int distanceToOpponent, FieldPoint ballPosition,
			FieldPoint defenderPosition, FieldPoint opponentPosition, int opponentId) {
		super(robot, output);

		this.opponentPosition = opponentPosition;
		this.ballPosition = ballPosition;
		this.distanceToOpponent = distanceToOpponent;
		this.defenderPosition = defenderPosition;
		this.role = RobotMode.COVERER;
		this.opponentId = opponentId;
		go = new GotoPosition(robot, output, defenderPosition, opponentPosition, 400);
	}

	/**
	 * Update
	 * @param distanceToOpponent The distance the defender keeps from the enemy (center) in millimeters
	 * @param ballPosition current position of the ball. See {@link FieldPoint}
	 * @param defenderPosition current position of the defender (this robot). See {@link FieldPoint}
	 * @param opponentPosition center position of the opponent / enemy. See {@link FieldPoint}
	 * @param opponentId The Id of the opponent this Robot is trying to interrupt.
	 */
	public void update(int distanceToOpponent, FieldPoint ballPosition, FieldPoint defenderPosition, FieldPoint opponentPosition,
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
			FieldPoint newDestination = getNewDestination();
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
	private FieldPoint getNewDestination() {
		FieldPoint newDestination = null;

		// Ball has to be on the field
		if (ballPosition != null) {

			double angle = opponentPosition.getAngle(ballPosition);

			double dx = (Math.sin(angle) * distanceToOpponent);
			double dy = (Math.cos(angle) * distanceToOpponent);

			newDestination = new FieldPoint(opponentPosition.getX() + dx, opponentPosition.getY() + dy);
		}

		return newDestination;
	}

}
