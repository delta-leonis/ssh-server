package robocup.controller.ai.lowLevelBehavior;

import robocup.controller.ai.movement.GotoPosition;
import robocup.model.Ball;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.enums.RobotMode;
import robocup.output.ComInterface;
import robocup.output.RobotCom;

/**
 * Describes the low-level behaviour for a Coverer robot.
 * These Robots attempt to interrupt the enemy by getting in between the enemy {@link Robot} and the {@link Ball}
 */
public class Coverer extends LowLevelBehavior {

	protected FieldPoint objectPosition;
	protected FieldPoint subjectPosition;
	protected int distanceToSubject;
	protected int subjectId;

	/**
	 * Create a defender (stands between "target" enemy and the ball)
	 * @param robot The {@link Robot} that describes the blocker.
	 * @param output The {@link RobotCom} that sends the commands to the physical Robot.
	 * @param distanceToSubject The distance the coverer keeps from the subject in millimeters
	 * @param objectPosition current position of the object the coverer needs to cover. See {@link FieldPoint}
	 * @param subjectPosition current position of the subject. See {@link FieldPoint}
	 * @param subjectId The Id of the subject this Robot is trying to interrupt.
	 */
	public Coverer(Robot robot, ComInterface output, int distanceToSubject, FieldPoint objectPosition,
			FieldPoint subjectPosition, int subjectId) {
		super(robot);
		this.subjectPosition = subjectPosition;
		this.objectPosition = objectPosition;
		this.distanceToSubject = distanceToSubject;
		this.role = RobotMode.COVERER;
		this.subjectId = subjectId;
		go = new GotoPosition(robot, output, robot.getPosition(), objectPosition, 400);
	}

	/**
	 * Update
	 * @param distanceToSubject The distance the defender keeps from the enemy (center) in millimeters
	 * @param objectPosition current position of the ball. See {@link FieldPoint}
	 * @param subjectPosition center position of the opponent / enemy. See {@link FieldPoint}
	 * @param subjectId The Id of the opponent this Robot is trying to interrupt.
	 */
	public void update(int distanceToSubject, FieldPoint objectPosition, FieldPoint defenderPosition,
			FieldPoint subjectPosition, int subjectId) {
		this.distanceToSubject = distanceToSubject;
		this.objectPosition = objectPosition;
		this.subjectPosition = subjectPosition;
		this.subjectId = subjectId;
	}

	/**
	 * Calculates the new position to go to and attempts to make the Robot move in that direction.
	 */
	@Override
	public void calculate() {
		FieldPoint newDestination = getNewDestination();

		// If available, set the new destination
		if (newDestination != null)
			go.setDestination(newDestination);

		go.setTarget(objectPosition);
		go.calculate();
	}

	/**
	 * @returns the id of the Opponent this Robot is trying to block.
	 */
	public int getOpponentId() {
		return subjectId;
	}

	/**
	 * @returns the new destination for the robot.
	 * The function attempts to pick a point at a specified distance from the subject, towards the object.
	 */
	private FieldPoint getNewDestination() {
		FieldPoint newDestination = null;

		// object and subject has to be on the field
		if (objectPosition != null && subjectPosition != null) {

			double angle = subjectPosition.getAngle(objectPosition);

			double dx = (Math.sin(angle) * distanceToSubject);
			double dy = (Math.cos(angle) * distanceToSubject);

			newDestination = new FieldPoint(subjectPosition.getX() + dx, subjectPosition.getY() + dy);
		}

		return newDestination;
	}

}
