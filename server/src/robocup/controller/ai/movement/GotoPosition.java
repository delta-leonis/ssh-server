package robocup.controller.ai.movement;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import robocup.Main;
import robocup.model.FieldObject;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.World;
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

	private static final int DISTANCE_TO_SLOW_DOWN = 200;	// Distance at which we start slowing down in millimeters.
	public static final int MAX_VELOCITY = 3000;
	
	private double DISTANCE_ROTATIONSPEED_COEFFICIENT = 12;
	private int MAX_ROTATION_SPEED = 1000;	//in mm/s
	private int START_UP_SPEED = 100; // Speed added to rotation. Robot only starts rotating if it receives a value higher than 200 
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
	
	// calculate total circumference of robot
	private static final double circumference = (Robot.DIAMETER * Math.PI);
	

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
		dplanner = new DijkstraPathPlanner();
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
		dplanner = new DijkstraPathPlanner();
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
		dplanner = new DijkstraPathPlanner();
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
		dplanner = new DijkstraPathPlanner();
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
	 */
	public void calculate(boolean avoidBall) {
		// Handle nulls
		if (destination == null) {
			if(target == null){
				output.send(1, robot.getRobotId(), 0, 0, 0, chipKick, dribble);
			}
			else{
				output.send(1, robot.getRobotId(), 0, 0, (int)getRotationSpeed(rotationToDest(target),0), chipKick, dribble);
			}
		} 
		else {
			// Dribble when the ball is close by
			dribble = Math.abs(
					robot.getOrientation() - robot.getPosition().getAngle(World.getInstance().getBall().getPosition())) < 20
					&& robot.getPosition().getDeltaDistance(World.getInstance().getBall().getPosition()) < Robot.DIAMETER / 2 + 200;
			// Calculate the route using the DijkstraPathPlanner
			route = dplanner.getRoute(robot.getPosition(), destination, robot.getRobotId(), avoidBall);
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

			double rotationToTarget = rotationToDest(target);
			double rotationToGoal = rotationToDest(destination);
			double speed;
			// Base speed on route.
			if(route.size() > 1){
				double angle0 = robot.getPosition().getAngle(route.get(0));
				double angle1 = route.get(0).getAngle(route.get(1));
				double routeAngle = Math.abs(angle0 - angle1);
				// Slow down based on the angle of the turn
				speed = getSpeed(getDistance() + DISTANCE_TO_SLOW_DOWN * (1 - routeAngle/360), DISTANCE_TO_SLOW_DOWN, MAX_VELOCITY);
			}
			else{
				speed = getSpeed(getDistance(), DISTANCE_TO_SLOW_DOWN, MAX_VELOCITY);
			}
			// Get the rotation speed, based on the speed we're turning
			double rotationSpeed = getRotationSpeed(rotationToTarget, speed);

			// Overrule speed
			if (forcedSpeed > 0) {
				speed = getSpeed(getDistance(), DISTANCE_TO_SLOW_DOWN/3, forcedSpeed);
			}
			
			currentSpeed = speed;
			if(dribble){
				// Send the command
				output.send(1, robot.getRobotId(), (int)rotationToGoal, (int)speed, (int)rotationSpeed, chipKick, dribble);
				LOGGER.log(Level.INFO, robot.getRobotId() + "," + (int)rotationToGoal + "," + (int)speed + "," + (int)rotationSpeed + "," + chipKick + "," + dribble);
			}
			// Don't kick or chip if we aren't nearby.
			else{
				// Send the command
				output.send(1, robot.getRobotId(), (int)rotationToGoal, (int)speed, (int)rotationSpeed, 0, dribble);
				LOGGER.log(Level.INFO, robot.getRobotId() + "," + (int)rotationToGoal + "," + (int)speed + "," + (int)rotationSpeed + ",0 ," + dribble);
			}
			
			
			// Set kick back to 0 to prevent kicking twice in a row
			chipKick = 0;
		}
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
				output.send(1, robot.getRobotId(), 0, 0, (int)getRotationSpeed(rotationToDest(target),0), chipKick, dribble);
			}
		} 
		else {
			double rotationToTarget = rotationToDest(target);
			double rotationToGoal = rotationToDest(destination);
			
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
	 * Get rotationSpeed, calculates the speed at which to rotate based on degrees left to rotate
	 * Precondition: -180 <= rotation <= 180
	 * @param rotation The rotation we want to make
	 * @param speed The speed of the {@link Robot}
	 * @return the speed at which the {@link Robot} should turn.
	 */
	private double getRotationSpeed(double rotation, double speed) {
		if(speed > MAX_VELOCITY / 2){
			return 0;
		}
		// must be between 0 and 50 percent, if it's higher than 50% rotating to
		// the other direction is faster
		double rotationPercent = rotation / 360;

		// distance needed to rotate in mm
		double rotationDistance = circumference * rotationPercent;
		rotationDistance *= DISTANCE_ROTATIONSPEED_COEFFICIENT;
		rotationDistance *= 1 - Math.abs(speed)/(MAX_VELOCITY + 500);
		
		if(Math.abs(rotationDistance) > MAX_ROTATION_SPEED){
			if(rotationDistance < 0){
				rotationDistance = -MAX_ROTATION_SPEED;
			}
			else{
				rotationDistance = MAX_ROTATION_SPEED;
			}
		}
		if(rotationDistance < 0){
			return rotationDistance - START_UP_SPEED;
		}
		else{
			return rotationDistance + START_UP_SPEED;
		}
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
			return speed;
		}
		return (d / distanceToSlowDown) * speed;
	}
	
	/**
	 * Calculate the needed rotation to destination
	 * @param newPoint The {@link FieldPoint} we wish to face.
	 * @return The rotation we need to make to face the given {@link FieldPoint}
	 */
	private double rotationToDest(FieldPoint newPoint) {
		if(newPoint != null){
			// angle vector between old and new
			double newangle = robot.getPosition().getAngle(newPoint);
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
	 * @param dribble True if you want to active the dribbler. False if you want to deactivate it.
	 */
	public void setDribble(boolean dribble) {
		this.dribble = dribble;
	}
	
	public DijkstraPathPlanner getPathPlanner(){
		return dplanner;
	}
}
