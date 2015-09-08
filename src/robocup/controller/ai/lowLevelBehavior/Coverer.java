package robocup.controller.ai.lowLevelBehavior;

import robocup.controller.ai.movement.GotoPosition;
import robocup.model.Ball;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.enums.RobotMode;

/**
 * Describes the low-level behavior for a Coverer robot.
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
	 */
	public Coverer(Robot robot) {
		super(robot);
		distanceToSubject = 0;
		objectPosition = null;
		subjectPosition = null;
		subjectId = 0;

		this.role = RobotMode.COVERER;
		go = new GotoPosition(robot, robot.getPosition(), objectPosition);
	}

	/**
	 * Update
	 * @param distanceToSubject The distance the defender keeps from the enemy (center) in millimeters
	 * @param objectPosition current position of the ball. See {@link FieldPoint}
	 * @param subjectPosition center position of the opponent / enemy. See {@link FieldPoint}
	 * @param subjectId The Id of the opponent this Robot is trying to interrupt.
	 */
	public void update(int distanceToSubject, FieldPoint objectPosition, FieldPoint subjectPosition, int subjectId) {
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

		if (robot.getPosition() != null)
			go.calculate(0,true);
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

			double dx = Math.cos(Math.toRadians(angle)) * distanceToSubject;
			double dy = Math.sin(Math.toRadians(angle)) * distanceToSubject;

			newDestination = new FieldPoint(subjectPosition.getX() + dx, subjectPosition.getY() + dy);
		}

		return newDestination;
	}
}
