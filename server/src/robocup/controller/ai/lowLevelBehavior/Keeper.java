package robocup.controller.ai.lowLevelBehavior;

import robocup.controller.ai.movement.GotoPosition;
import robocup.model.Ball;
import robocup.model.Enemy;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.World;
import robocup.model.enums.RobotMode;

public class Keeper extends LowLevelBehavior {

	protected int distanceToObject;
	protected boolean goToKick;
	protected FieldPoint ballPosition;
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
		this.centerGoalPosition = centerGoalPosition;

		this.role = RobotMode.KEEPER;
		go = new GotoPosition(robot, centerGoalPosition, ballPosition,3000);
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
		// Calculate goToKick
		
//		FieldPoint newDestination = getNewKeeperDestination(centerGoalPosition, ballPosition, distanceToObject);
		FieldPoint newDestination = calculateGoToKick();
		// Change direction based on goToKick.
		// Move forward and kick if ball gets too close
		// Else, go to proper direction
		changeDestination(newDestination, ballPosition);
	}
	
	/**
	 * Calculates the goToKick
	 */
	private FieldPoint calculateGoToKick(){
		if(this instanceof Keeper){
			// Look for "Dangerous robots"
			// 		Get closest enemy robot to ball
			double ballDirection;
			Robot robot = World.getInstance().getClosestRobotToBall();
			// This part might just ruin the entire game for us.
			boolean dangerous = false;
			if(robot instanceof Enemy){
				//		If enemy in danger zone
				if(World.getInstance().getReferee().getAlly().equals(World.getInstance().getReferee().getEastTeam())
						&& robot.getPosition().getX() > 0){
					dangerous = true;
				}
				else if(World.getInstance().getReferee().getAlly().equals(World.getInstance().getReferee().getWestTeam())
						&& robot.getPosition().getX() < 0){
					dangerous = true;
				}
				//		Then we're dealing with a dangerous robot.
			}
			// If no robot has the ball -> Go to where the ball will be.
			Ball ball = World.getInstance().getBall();
			FieldPoint ballPos = ball.getPosition();
			if(robot.getPosition().getDeltaDistance(ballPos) > 250 && ball.getSpeed() > 0.1){
				ballDirection = ball.getDirection();
			}
			else if(dangerous){
				ballDirection = robot.getPosition().getAngle(ball.getPosition());
			}
			else{
				return getNewKeeperDestination(centerGoalPosition, ballPosition, distanceToObject);		
			}
			
			double y;	// Where the ball will go to.
			double x;
			if(World.getInstance().getReferee().getAlly().equals(World.getInstance().getReferee().getEastTeam())){
				if(Math.cos(Math.toRadians(ballDirection)) < 0){		// Ball moves towards the west
					goToKick = false;
					return getNewKeeperDestination(centerGoalPosition, ballPosition, distanceToObject);
				}
				x = World.getInstance().getField().getLength()/2;
			}
			else{
				if(Math.cos(Math.toRadians(ballDirection)) > 0){		// Ball moves towards the east
					goToKick = false;
					return getNewKeeperDestination(centerGoalPosition, ballPosition, distanceToObject);
				}
				x = -World.getInstance().getField().getLength()/2;
			}
			y = Math.tan(Math.toRadians(ballDirection)) * (ballPos.getX() + x) + ballPos.getY();	// <-- Where the ball will hit
			double goalWidth = World.getInstance().getField().getEastGoal().getWidth();
			if(y < goalWidth/2 && y > -goalWidth/2){
				// if the ball is going towards the goal
				double keeperX = x - Math.cos(Math.toRadians(- ballDirection)) * 500;
				double keeperY = y + Math.sin(Math.toRadians(- ballDirection)) * 500;
				FieldPoint goalDest =  new FieldPoint(keeperX,keeperY);
				//TODO: Test the commented code.
	//				if(goalDest.getDeltaDistance(ballPos) < 1000){
	//					// go to ball and kick
	////					goToKick = true;
	//					return new FieldPoint(	x - Math.cos(Math.toRadians(- ball.getDirection())) * 800,
	//											y + Math.sin(Math.toRadians(- ball.getDirection())) * 800);
	//				}
	//				else{
					goToKick = false;
					return goalDest;
	//				}
			}
			goToKick = false;
		}
		// Else: Ally has ball. Do the regular stuff.
		return getNewKeeperDestination(centerGoalPosition, ballPosition, distanceToObject);		
		
		
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
