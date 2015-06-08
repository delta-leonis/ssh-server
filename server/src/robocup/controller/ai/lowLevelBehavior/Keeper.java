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
		go = new GotoPosition(robot, centerGoalPosition, ballPosition,3000);
	}

	/**
	 * Update the values for the keeper
	 * @param distanceToGoal The distance this {@link Keeper} needs to stay away from the pointToDefend.
	 * @param goToKick True if the {@link Keeper} has to go forth and get the ball, false otherwise.
	 * @param ballPosition The {@link FieldPoint position} of the ball. (duh)
	 * @param pointToDefend The point this keeper is going to defend.
	 * @see {@link #update(int, boolean, FieldPoint)}
	 */
	public void update(int distanceToGoal, boolean goToKick, FieldPoint ballPosition, FieldPoint pointToDefend) {
		this.distanceToObject = distanceToGoal;
		this.goToKick = goToKick;
		this.ballPosition = ballPosition;
		this.pointToDefend = pointToDefend;
	}
	
	/**
	 * Update the values for the keeper
	 * @param distanceToGoal The distance this {@link Keeper} needs to stay away from the middle of the goal.
	 * @param goToKick True if the {@link Keeper} has to go forth and get the ball, false otherwise.
	 * @param ballPosition The {@link FieldPoint position} of the ball. (duh)
	 */
	public void update(int distanceToGoal, boolean goToKick, FieldPoint ballPosition) {
		this.distanceToObject = distanceToGoal;
		this.goToKick = goToKick;
		this.ballPosition = ballPosition;
		pointToDefend = centerGoalPosition;
	}

	@Override
	public void calculate() {
		// Calculate goToKick
		
		FieldPoint newDestination = getNewKeeperDestination(pointToDefend, ballPosition, distanceToObject);
		// Change direction based on goToKick.
		// Move forward and kick if ball gets too close
		// Else, go to proper direction
		changeDestination(newDestination, ballPosition);
	}

	protected void changeDestination(FieldPoint destination, FieldPoint target) {
		go.setTarget(ballPosition);

		if (goToKick)
			go.setDestination(ballPosition);
		else if (destination != null)
			go.setDestination(destination);

		go.calculate(false);
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
			if(World.getInstance().getReferee().getAlly().equals(World.getInstance().getReferee().getEastTeam()) && destX > World.getInstance().getField().getLength()/2){
				destX = World.getInstance().getField().getLength()/2;
			}
			else if(World.getInstance().getReferee().getAlly().equals(World.getInstance().getReferee().getWestTeam()) && destX < -World.getInstance().getField().getLength()/2){
				destX = -World.getInstance().getField().getLength()/2;
			}
			newDestination = new FieldPoint(destX, destY);
		}

		return newDestination;
	}
}
