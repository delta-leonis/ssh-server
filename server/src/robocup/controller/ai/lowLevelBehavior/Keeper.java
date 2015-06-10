package robocup.controller.ai.lowLevelBehavior;

import robocup.controller.ai.movement.GotoPosition;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.World;
import robocup.model.enums.RobotMode;

public class Keeper extends LowLevelBehavior {

	protected int distanceToObject;
	protected boolean goToKick;
	protected FieldPoint ballPosition;
	protected FieldPoint pointToDefend;
	protected FieldPoint centerGoalPosition;
	
	protected double fieldWidth = 6000;
	protected double fieldLength = 9000;

	/**
	 * Create a keeper
	 * @param robot the keeper {@link Robot} in the model.
	 * @param centerGoalPosition center of the goal on the correct side of the playing field
	 */
	public Keeper(Robot robot, FieldPoint centerGoalPosition) {
		super(robot);
		distanceToObject = 0;
		goToKick = false;
		ballPosition = null;
		this.pointToDefend = centerGoalPosition;
		this.centerGoalPosition = centerGoalPosition;

		this.role = RobotMode.KEEPER;
		go = new GotoPosition(robot, centerGoalPosition, ballPosition);
		go.setStartupSpeedVelocity(200);
		go.setDistanceToSlowDown(150);
	}

	/**
	 * Update the values for the keeper
	 * @param distanceToGoal The distance this {@link Keeper} needs to stay away from the pointToDefend.
	 * @param goToKick True if the {@link Keeper} has to go forth and get the ball, false otherwise.
	 * @param ballPosition The {@link FieldPoint position} of the ball. (duh)
	 * @param pointToDefend The point this keeper is going to defend.
	 * @see {@link #update(int, boolean, FieldPoint)}
	 */
	public void update(int distanceToGoal, boolean goToKick, FieldPoint ballPosition, FieldPoint pointToDefend, double fieldWidth, double fieldLength) {
		this.distanceToObject = distanceToGoal;
		this.goToKick = goToKick;
		this.ballPosition = ballPosition;
		this.pointToDefend = pointToDefend;

		this.fieldWidth = fieldWidth;
		this.fieldLength = fieldLength;
	}
	
	/**
	 * Update the values for the keeper
	 * @param distanceToGoal The distance this {@link Keeper} needs to stay away from the middle of the goal.
	 * @param goToKick True if the {@link Keeper} has to go forth and get the ball, false otherwise.
	 * @param ballPosition The {@link FieldPoint position} of the ball. (duh)
	 */
	public void update(int distanceToGoal, boolean goToKick, FieldPoint ballPosition, double fieldWidth, double fieldLength) {
		this.distanceToObject = distanceToGoal;
		this.goToKick = goToKick;
		this.ballPosition = ballPosition;
		pointToDefend = centerGoalPosition;

		this.fieldWidth = fieldWidth;
		this.fieldLength = fieldLength;
	}

	@Override
	public void calculate() {
		FieldPoint newDestination = getNewKeeperDestination(pointToDefend, ballPosition, distanceToObject);
		// Change direction based on goToKick.
		// Move forward and kick if ball gets too close
		// Else, go to proper direction
		changeDestination(newDestination, ballPosition);
	}

	protected void changeDestination(FieldPoint destination, FieldPoint target) {
		go.setTarget(target);

		if (goToKick)
			go.setDestination(ballPosition);
		else if (destination != null)
			go.setDestination(destination);

		go.calculate(false, false);
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
		return getNewKeeperDestination(objectPosition, subjectPosition, distance, 0);
	}
	
	protected FieldPoint cropFieldPosition(FieldPoint position){
		if (ballPosition != null) {
			// make sure the x coordinate of the ball is within the x axis of the field
			double ballX = Math.max(-fieldLength/2, Math.min(fieldLength/2, ballPosition.getX()));

			// make sure the x coordinate of the ball is within the x axis of the field
			double ballY = Math.max(-fieldWidth/2, Math.min(fieldWidth/2, ballPosition.getY()));
			// place the new x coordinate in a new fieldpoint
			return new FieldPoint(ballX, ballY);
		} else {
			return null;
		}
	}
	
	/**
	 * Calculate a new Keeper destination.
	 * The destination will be a point between the object and the subject position with a specified distance to the object position.
	 * @param objectPosition the position of the point this keeper is defending.
	 * @param subjectPosition the point which needs to be blocked
	 * @param distance the distance to the object position
	 * @param offset the offset for this keeper in degrees
	 * @return the new keeper destination
	 */
	protected FieldPoint getNewKeeperDestination(FieldPoint objectPosition, FieldPoint subjectPosition, int distance, int offset) {
		FieldPoint newDestination = null;

		if (objectPosition != null && subjectPosition != null) {
			double angle = objectPosition.getAngle(subjectPosition) + offset;
			double dx = Math.cos(Math.toRadians(angle)) * distance;
			double dy = Math.sin(Math.toRadians(angle)) * distance;

			double destX = objectPosition.getX() + dx;
			double destY = objectPosition.getY() + dy;
			if(World.getInstance().getReferee().getAlly().equals(World.getInstance().getReferee().getEastTeam()) && destX > World.getInstance().getField().getLength()/2 - Robot.DIAMETER/2){
				destX = World.getInstance().getField().getLength()/2 - Robot.DIAMETER/2;
			}
			else if(World.getInstance().getReferee().getAlly().equals(World.getInstance().getReferee().getWestTeam()) && destX < -World.getInstance().getField().getLength()/2 + Robot.DIAMETER/2){
				destX = -World.getInstance().getField().getLength()/2 + Robot.DIAMETER/2;
			}
			newDestination = new FieldPoint(destX, destY);
		}

		return newDestination;
	}
}
