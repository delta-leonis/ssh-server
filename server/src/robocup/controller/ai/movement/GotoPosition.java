package robocup.controller.ai.movement;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import robocup.Main;
import robocup.gamepad.GamepadModel;
import robocup.model.FieldObject;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.World;
import robocup.model.enums.GameState;
import robocup.output.ComInterface;

/**
 * Class used by {@link robocup.controller.ai.lowLevelBehavior.LowLevelBehavior
 * LowLevelBehaviors} to move to a certain point on the field. The Class will
 * direct a robot to a certain position using the given
 * {@link robocup.model.Robot Robot} and a
 * {@link robocup.controller.ai.movement.DijkstraPathPlanner Path_Planner}.
 * 
 * @see {@link robocup.output.ComInterface ComInterface}
 */
public class GotoPosition {
	// Movement Speed Variables
	/** 450 */
	private int DISTANCE_TO_SLOW_DOWN = 450;
	/** 3000 */
	public static int MAX_VELOCITY = 3000;
	/** 100 */
	private int START_UP_MOVEMENT_SPEED = 200;
	/** 450 */
	private int DISTANCE_TO_SLOW_DOWN_FORCED = 90;
	// Rotation Speed Variables
	/** 5 */
	@SuppressWarnings("unused")
	private double DISTANCE_ROTATIONSPEED_COEFFICIENT = 5;
	/** 1000 */
	private int MAX_ROTATION_SPEED = 1000;
	/** 100 */
	private int START_UP_ROTATION_SPEED = 200;
	// Circle Around Ball Move Variables
	private int CIRCLE_SPEED = 2300;
	
	private long lastKickTime;
	
	private boolean avoidEastGoalArea = false;
	private boolean avoidWestGoalArea = false;
	
	private static Logger LOGGER = Logger.getLogger(Main.class.getName());

	private FieldPoint destination;
	private FieldPoint target;
	private Robot robot;
	private ComInterface output;
	private int forcedSpeed = 0;
	private int chipKick = 0;
	private boolean dribble = false;
	private DijkstraPathPlanner dplanner;
	private LinkedList<FieldPoint> route;
	private double currentSpeed;
	private World world;
	
	// calculate total circumference of robot
//	private static final double circumference = (Robot.DIAMETER * Math.PI);
	

	/**
	 * Setup this object to function for the given {@link Robot} and {@link FieldPoint destination}
	 * @param robot The {@link Robot} we wish to move.
	 * @param destination The {@link FieldPoint destination} we wish to move to.
	 */
	public GotoPosition(Robot robot, FieldPoint destination) {
		this.robot = robot;
		output = ComInterface.getInstance();
		this.destination = destination;
		this.target = destination;
		lastKickTime = System.currentTimeMillis();
		dplanner = new DijkstraPathPlanner();
		world = World.getInstance();
	}

	/**
	 * Setup this object to function for the given {@link Robot} and {@link FieldPoint target}
	 * Makes the given {@link Robot} turn towards the given {@link FieldObject target}
	 * @param robot The {@link Robot} we wish to move.
	 * @param target Target to look at (usually the ball)
	 */
	public GotoPosition(Robot robot, FieldObject target) {
		this.robot = robot;
		output = ComInterface.getInstance();
		this.destination = target.getPosition();
		this.target = this.destination;
		lastKickTime = System.currentTimeMillis();
		dplanner = new DijkstraPathPlanner();
		world = World.getInstance();
	}

	/**
	 * Go to the given {@link FieldPoint destination} and face the given {@link FieldPoint target}
	 * @param robot The {@link Robot} we wish to control.
	 * @param destination The {@link FieldPoint destination} we wish to move to.
	 * @param target The {@link FieldPoint target} we wish to face. (usually the ball)
	 */
	public GotoPosition(Robot robot, FieldPoint destination, FieldPoint target) {
		this.robot = robot;
		output = ComInterface.getInstance();
		this.destination = destination;
		this.target = target;
		lastKickTime = System.currentTimeMillis();
		dplanner = new DijkstraPathPlanner();
		world = World.getInstance();
	}

	/**
	 * Move towards {@link FieldPoint destination} with a forced speed facing the {@link FieldPoint destination}
	 * @param robot The {@link Robot} we wish to move.
	 * @param destination The {@link FieldPoint destination} we wish to go to.
	 * @param target The {@link FieldPoint target} we wish to face.
	 * @param forcedSpeed The speed in mm/s we wish to drive at. (Overrules the speed calculate by {@link #calculate()})
	 */
	public GotoPosition(Robot robot, FieldPoint destination, FieldPoint target, int forcedSpeed) {
		this.robot = robot;
		output = ComInterface.getInstance();
		this.destination = destination;
		this.target = target;
		this.forcedSpeed = forcedSpeed;
		lastKickTime = System.currentTimeMillis();
		dplanner = new DijkstraPathPlanner();
		world = World.getInstance();
	}

	/**
	 * @return TargetPoint the {@link FieldPoint target} we wish our {@link Robot} to look at
	 */
	public FieldPoint getTarget() {
		return target;
	}

	/**
	 * Sets the {@link FieldPoint target} we wish our robot to look at
	 * @param TargetPoint The target we wish out {@link Robot} to look at
	 */
	public void setTarget(FieldPoint p) {
		target = p;
	}

	/**
	 * Sets the {@link FieldPoint destination} we want our robot to drive to
	 * @param destination The destination we want our {@link Robot} to drive to.
	 */
	public void setDestination(FieldPoint destination) {
		this.destination = destination;
	}

	/**
	 * @returns The {@link FieldPoint destination} our {@link Robot} will attempt to move towards.
	 */
	public FieldPoint getDestination() {
		return destination;
	}

	/**
	 * Set the kicking or chipping power for the next message, resets to 0 after
	 * using it
	 * @param kick ranges 1-100 for kicking, -1 to -100 for chipping power in percentages
	 */
	public void setKick(int chipKick) {
		this.chipKick = chipKick;
	}

	/**
	 * Calculates what message we need to send to the robot, based on the
	 * parameters given in the constructor.
	 * @param avoidBall True if you wish the robot to avoid the ball, false otherwise.
	 * @param alwaysFaceTarget True if you want the robot to constantly face the target, 
	 * 							false if you want the robot to face the direction it should face at its destination
	 */
	public void calculate(boolean avoidBall, boolean alwaysFaceTarget) {
		if (robot.isOnSight()) {
		if(prepareForTakeOff()) {
			// Dribble when the ball is close by
			dribble = Math.abs(
					Math.abs(robot.getOrientation()) - Math.abs(robot.getPosition().getAngle(world.getBall().getPosition()))) < 20
					&& robot.getPosition().getDeltaDistance(world.getBall().getPosition()) < Robot.DIAMETER / 2 + 200;
			// Calculate the route using the DijkstraPathPlanner
			route = dplanner.getRoute(robot.getPosition(), destination, robot.getRobotId(), avoidBall, avoidEastGoalArea, avoidWestGoalArea);
			// If robot is locked up, the route will be null
			if(route == null){
				LOGGER.info("Robot #" + robot.getRobotId() + " can't reach destination.");
				output.send(1, robot.getRobotId(), 0, 0, 0, 0, false);
				return;
			}

			// Get the first point in the route
			if (route.size() > 0 && route.get(0) != null) {
				destination = route.get(0);
			} else {
				output.send(1, robot.getRobotId(), 0, 0, 0, 0, false);
				return;
			}
			
			double rotationToTarget = alwaysFaceTarget ? rotationToDest(robot.getPosition(), target) : rotationToDest(destination,target);
			double rotationToGoal = rotationToDest(robot.getPosition(), destination);
			double speed;
			// Base speed on route.
			if(route.size() > 1){
				// Slow down based on the angle of the turn
				speed = getSpeed(	getDistance() + 
									DISTANCE_TO_SLOW_DOWN * (1 - Math.abs(robot.getPosition().getAngle(route.get(0)) -
									route.get(0).getAngle(route.get(1)))/360), 
									DISTANCE_TO_SLOW_DOWN, MAX_VELOCITY);
			}
			else{
				speed = getSpeed(getDistance(), DISTANCE_TO_SLOW_DOWN, MAX_VELOCITY);
			}
			// Get the rotation speed, based on the speed we're turning
			double rotationSpeed = getRotationSpeed(rotationToTarget, speed);

			// Overrule speed
			if (forcedSpeed > 0) {
				speed = getSpeed(getDistance(), DISTANCE_TO_SLOW_DOWN_FORCED, forcedSpeed);
			}

			currentSpeed = speed;
			if(dribble && robot.getPosition().getDeltaDistance(destination) < Robot.DIAMETER/2 + 15 && System.currentTimeMillis() > lastKickTime + 1000){
				// Send the command
				output.send(1, robot.getRobotId(), (int)rotationToGoal, (int)speed, (int)rotationSpeed, chipKick, dribble);
				LOGGER.log(Level.INFO, robot.getRobotId() + "," + (int)rotationToGoal + "," + (int)speed + "," + (int)rotationSpeed + "," + chipKick + "," + dribble);
				lastKickTime = System.currentTimeMillis();
			}
			// Don't kick or chip if we aren't nearby.
			else{
				// Send the command
				output.send(1, robot.getRobotId(), (int)rotationToGoal, (int)speed, (int)rotationSpeed, 0, dribble);
				LOGGER.log(Level.INFO, robot.getRobotId() + "," + (int)rotationToGoal + "," + (int)speed + "," + (int)rotationSpeed + ",0 ," + dribble);
			}
//			System.out.println("\t " + robot.getRobotId() + "," + (int)rotationToGoal + "," + (int)currentSpeed + "," + (int)rotationSpeed );

			// Set kick back to 0 to prevent kicking twice in a row
			chipKick = 0;
		}
		}
		// check if robot has a previous location if the robot is not onsight
		else if (robot.getPosition() != null && !robot.isOnSight()) {
			output.send(2, robot.getRobotId());
		}
	}
	
	/**
	 * Does all the necessary checks before actually moving.
	 * @return true, if we're allowed to move, false otherwise.
	 */
	public boolean prepareForTakeOff(){
//		if(World.getInstance().getGameState() == GameState.HALTED){
//			output.send(1, robot.getRobotId(), 0, 0, 0, 0, false);
//			return false;
//		}
//		if(World.getInstance().getGameState() == GameState.STOPPED){
//			FieldPoint ball = World.getInstance().getBall().getPosition();
//			double deltaDistance = ball.getDeltaDistance(robot.getPosition());
//			MAX_VELOCITY = 1500;
//			if(deltaDistance < 700){
//				double robotAngleBall = robot.getPosition().getAngle(ball);
//				destination = new FieldPoint(robot.getPosition().getX() - Math.cos(Math.toRadians(robotAngleBall)) * (750 - deltaDistance),
//														robot.getPosition().getY() - Math.sin(Math.toRadians(robotAngleBall)) * (750 - deltaDistance));
//				return true;
//			}
//		}
		if (destination == null) {
			if(target == null){
				output.send(1, robot.getRobotId(), 0, 0, 0, 0, dribble);
				return false;
			}
			else{
				output.send(1, robot.getRobotId(), 0, 0, (int)getRotationSpeed(rotationToDest(robot.getPosition(), target),0), 0, dribble);
				return false;
			}
		}
		MAX_VELOCITY = 3000;

		return true;
	}
	
	public double getCurrentSpeed(){
		return currentSpeed;
	}
	
	/**
	 * Function which moves your {@link Robot} to the given position
	 * without making use of the {@link DijkstraPathPlanner}
	 * @param speed The speed the robot will move in mm/s
	 */
	public void calculateWithoutPathPlanner(int speed, int chipKick, boolean dribble){
		// Handle nulls
		if (destination == null) {
			if(target == null){
				output.send(1, robot.getRobotId(), 0, 0, 0, chipKick, dribble);
			}
			else{
				output.send(1, robot.getRobotId(), 0, 0, (int)getRotationSpeed(rotationToDest(robot.getPosition(), target),0), chipKick, dribble);
			}
		} 
		else {
			double rotationToTarget = rotationToDest(robot.getPosition(), target);
			double rotationToGoal = rotationToDest(robot.getPosition(), destination);
			
			// Get the rotation speed, based on the speed we're turning
			double rotationSpeed;
			if(speed < -700){
				rotationSpeed = getRotationSpeed(rotationToTarget, speed + 700);
			}
			else if(speed > 700){
				rotationSpeed = getRotationSpeed(rotationToTarget, speed -700);
			}
			else{
				rotationSpeed = getRotationSpeed(rotationToTarget, speed);
			}
			
			currentSpeed = speed;
			// Send the command
			output.send(1, robot.getRobotId(), (int)rotationToGoal, speed, (int)rotationSpeed, chipKick, dribble);
			LOGGER.log(Level.INFO, robot.getRobotId() + "," + (int)rotationToGoal + "," + (int)speed + "," + (int)rotationSpeed + "," + chipKick + "," + dribble);
			
			// Set kick back to 0 to prevent kicking twice in a row
			chipKick = 0;
		}
	}
	
	/**
	 * Function that goes to the given position, whilst turning around the target.
	 * Calling this function continuously will make the robot end up at the given
	 * offset from the target at an angle between the target and the destination.
	 * @param offset The distance to stay away from the target.
	 */
	public void calculateTurnAroundTarget(int offset){
		// Angle between ball and robot
		double angleTargetAndRobot = target.getAngle(robot.getPosition());
		// Get total angle we need to rotate
		double totalAngle = target.getAngle(destination);
		// Increase angle
		double degreesToMove;
		if(robot.getPosition().getDeltaDistance(target) > (offset*1.1)){
			if(Math.abs(totalAngle - angleTargetAndRobot) > 90){
				double turnAmount = Math.abs(totalAngle - angleTargetAndRobot) - 90;
				degreesToMove = angleTargetAndRobot + ((totalAngle - angleTargetAndRobot) < 0 ? -turnAmount : turnAmount);
			}
			else if(Math.abs(totalAngle - angleTargetAndRobot) > 15){
				degreesToMove = angleTargetAndRobot + ((totalAngle - angleTargetAndRobot) < 0 ? -15 : 15);
			}
			else{
				double turnAmount = Math.abs(totalAngle - angleTargetAndRobot);
				degreesToMove = angleTargetAndRobot + ((totalAngle - angleTargetAndRobot) < 0 ? -turnAmount : turnAmount);
			}
			// Use new angle to get position on circle around target
			FieldPoint newDestination = new FieldPoint(	target.getX() + offset * Math.cos(Math.toRadians(degreesToMove)),
														target.getY() + offset * Math.sin(Math.toRadians(degreesToMove)));
			destination = newDestination;
			forcedSpeed = 0;
			calculate(false, true);
		}
		else{
			if(Math.abs(totalAngle - angleTargetAndRobot) > 15){
				degreesToMove = angleTargetAndRobot + ((totalAngle - angleTargetAndRobot) < 0 ? -15 : 15);
			}
			else{
				double turnAmount = Math.abs(totalAngle - angleTargetAndRobot);
				degreesToMove = angleTargetAndRobot + ((totalAngle - angleTargetAndRobot) < 0 ? -turnAmount : turnAmount);
			}
			// Use new angle to get position on circle around target
			FieldPoint newDestination = new FieldPoint(	target.getX() + offset * Math.cos(Math.toRadians(degreesToMove)),
														target.getY() + offset * Math.sin(Math.toRadians(degreesToMove)));
			destination = newDestination;
			forcedSpeed = CIRCLE_SPEED;
			calculate(false, true);
		}
	}
	
	public void goForwardUntilKick(int speed){
		if(prepareForTakeOff()){
			// Dribble when the ball is close by
			dribble = Math.abs(
					Math.abs(robot.getOrientation()) - Math.abs(robot.getPosition().getAngle(World.getInstance().getBall().getPosition()))) < 20
					&& robot.getPosition().getDeltaDistance(World.getInstance().getBall().getPosition()) < Robot.DIAMETER / 2 + 200;
			if(dribble && target.getDeltaDistance(robot.getPosition()) < Robot.DIAMETER/2 && System.currentTimeMillis() > lastKickTime + 500 && chipKick != 0){
				ComInterface.getInstance().send(1, robot.getRobotId(), 0, 0, 0, chipKick, true);
				lastKickTime = System.currentTimeMillis();
			}
			else{
				ComInterface.getInstance().send(1, robot.getRobotId(), 0, speed, 0, 0, true);
			}
		}
	}

	/**
	 * Get rotationSpeed, calculates the speed at which to rotate based on degrees left to rotate
	 * Precondition: -180 <= rotation <= 180
	 * @param rotation The rotation we want to make
	 * @param speed The speed of the {@link Robot}
	 * @return the speed at which the {@link Robot} should turn.
	 */
	private double getRotationSpeed(double rotation, double speed) {
//		if(speed > MAX_VELOCITY / 2){
//			return 0;
//		}
		// must be between 0 and 50 percent, if it's higher than 50% rotating to
		// the other direction is faster
		double rotationPercent = rotation / 180;	// TODO: Make 180.

		// distance needed to rotate in mm
//		double rotationDistance = circumference * rotationPercent;
//		rotationDistance *= DISTANCE_ROTATIONSPEED_COEFFICIENT;
		double rotationDistance = rotationPercent * MAX_ROTATION_SPEED;
		if(rotationDistance < 0){
			rotationDistance -= START_UP_ROTATION_SPEED;
		}
		else{
			rotationDistance += START_UP_ROTATION_SPEED;
		}
		rotationDistance *= 1 - Math.abs(speed)/(MAX_VELOCITY * 1.5);
		return rotationDistance;
//		if(Math.abs(rotationDistance) > MAX_ROTATION_SPEED){
//			if(rotationDistance < 0){
//				rotationDistance = -MAX_ROTATION_SPEED;
//			}
//			else{
//				rotationDistance = MAX_ROTATION_SPEED;
//			}
//		}
		
	}

	/**
	 * @returns the distance in millimeters between the {@link Robot} and the first {@link FieldPoint} calculated in the route.
	 * @see {@link #calculate()}
	 */
	private double getDistance() {
		return robot.getPosition().getDeltaDistance(route.get(0));
	}
	
	/**
	 * Returns the speed the robot should drive at.
	 * When a robot nears its destination, it should slow down.
	 * @param d The distance to travel in mm
	 * @param distanceToSlowDown If the robot has less distance to travel than the distance to slow down, the robot should slow down.
	 * @return The speed in mm/s
	 */
	private double getSpeed(double d, int distanceToSlowDown, int speed) {
		if(d > distanceToSlowDown){
			return speed + (speed > 0 ? START_UP_MOVEMENT_SPEED : -START_UP_MOVEMENT_SPEED);
		}
		return (d / distanceToSlowDown) * speed + (speed > 0 ? START_UP_MOVEMENT_SPEED : -START_UP_MOVEMENT_SPEED);
	}
	
	/**
	 * Calculate the needed rotation to destination
	 * @param newPoint The {@link FieldPoint} we wish to face.
	 * @return The rotation we need to make to face the given {@link FieldPoint}
	 */
	private double rotationToDest(FieldPoint destination, FieldPoint newPoint) {
		if(newPoint != null){
			// angle vector between old and new
			double newangle = destination.getAngle(newPoint);
			double rot = robot.getOrientation() - newangle;
	
			if (rot > 180) {
				rot -= 360;
			}
	
			if (rot < -180) {
				rot += 360;
			}
			return rot;
		}else{
			return 0;
		}
	}

	/**
	 * @return The value we wish our {@link Robot} to dribble at.
	 */
	public boolean getDribble() {
		return dribble;
	}

	/**
	 * Only used by the {@link GamepadModel} at the moment. 
	 * Won't work with {@link #calculate(boolean)}, use {@link #calculateWithoutPathPlanner(int, int, boolean)}
	 * @param dribble True if you want to active the dribbler. False if you want to deactivate it.
	 */
	public void setDribble(boolean dribble) {
		this.dribble = dribble;
	}
	
	public DijkstraPathPlanner getPathPlanner(){
		return dplanner;
	}
	
	//***			Functions to manipulate robot speed				***\\
	/**
	 * Sets the distance the {@link Robot} has to start slowing down at, in millimeters
	 * For example: If the distanceToSlowDown = 500, the robot will start slowing down 500mm before its destination.
	 * DISTANCE_TO_SLOW_DOWN is not used when the {@link GotoPosition} is given a forced speed.
	 * @param distanceToSlowDown The distance the {@link Robot} needs to start slowing down.
	 * By default set to {@link #DISTANCE_TO_SLOW_DOWN}
	 */
	public void setDistanceToSlowDown(int distanceToSlowDown){
		DISTANCE_TO_SLOW_DOWN = distanceToSlowDown;
	}
	
	/**
	 * Sets the maximum velocity in mm/s for the {@link Robot} to drive at.
	 * @param maxVelocity The maximum velocity for the {@link Robot}
	 * By default set to {@link #MAX_VELOCITY}
	 */
	public void setMaxVelocity(int maxVelocity){
		MAX_VELOCITY = maxVelocity;
	}
	
	/**
	 * Sets the speed the {@link Robot} is guarenteed to drive.
	 * A shame about our {@link Robot robots} is that they're heavy, 
	 * meaning that moving a mere centimeter when standing still often doesn't work.
	 * To counter this, this variable is added to the speed to {@link Robot} is supposed to move at.
	 * For example: If the GotoPosition were to tell the {@link Robot} to move to the right with a speed of 200 mm/s,
	 * 	it would send (200 + START_UP_MOVEMENT_SPEED) instead.
	 * 
	 * Warning: Increasing the startup speed also means the {@link Robot} may end up being shakey
	 * @param startup The speed to {@link Robot} will start up with
	 * By default set to {@link #START_UP_MOVEMENT_SPEED}
	 */
	public void setStartupSpeedVelocity(int startup){
		START_UP_MOVEMENT_SPEED = startup;
	}
	
	//***			Functions to manipulate rotation speed				***\\
	/**
	 * @deprecated  Using MAX_ROTATION_SPEED instead.  TODO: Remove.
	 * Sets the rotationspeed for the {@link Robot} to spin at.
	 * @param rotationSpeed A calibratable number used to make the {@link Robot} spin faster. 
	 * By default set to {@link #DISTANCE_ROTATIONSPEED_COEFFICIENT}
	 */
	public void setRotationSpeed(int rotationSpeed){
		DISTANCE_ROTATIONSPEED_COEFFICIENT = rotationSpeed;
	}
	
	/**
	 * Sets the maximum velocity in mm/s this {@link Robot} is allowed to turn at.
	 * @param maxRotationSpeed
	 */
	public void setMaxRotationSpeed(int maxRotationSpeed){
		MAX_ROTATION_SPEED = maxRotationSpeed;
	}
	
	/**
	 * Sets the speed the {@link Robot} is guarenteed to turn.
	 * A shame about our {@link Robot robots} is that they're heavy, 
	 * meaning that turning a mere centimeter when standing still often doesn't work.
	 * To counter this, this variable is added to the rotationspeed to {@link Robot} is supposed to move at.
	 * For example: If the GotoPosition were to tell the {@link Robot} to turn to the right with a speed of 200 mm/s,
	 * 	it would send (200 + START_UP_ROTATION_SPEED) instead.
	 * 
	 * Warning: Increasing the startup speed also means the {@link Robot} may end up being shakey
	 * @param startup The speed to {@link Robot} will start up with
	 * By default set to {@link #START_UP_ROTATION_SPEED}
	 */
	public void setStartupSpeedRotation(int startup){
		START_UP_ROTATION_SPEED = startup;
	}
	
	public void setForcedSpeed(int speed){
		forcedSpeed = speed;
	}
	
	public void setAvoidWestGoal(boolean avoid){
		avoidWestGoalArea = avoid;
	}
	
	public void setAvoidEastGoal(boolean avoid){
		avoidEastGoalArea = avoid;
	}
}
