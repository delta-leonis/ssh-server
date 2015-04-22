package robocup.controller.ai.movement;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import robocup.Main;
import robocup.model.FieldObject;
import robocup.model.FieldPoint;
import robocup.model.Robot;
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

	// TODO find a better solution
	private static final double DISTANCE_ROTATIONSPEED_COEFFICIENT = 3;
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
	
	// calculate total circumference of robot
	private static final double circumference = (Robot.DIAMETER * Math.PI);
	
	private static final int MAX_VELOCITY =2000;

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
	public void calculate() {
		robot.setOnSight(true);
		if (destination == null || target == null) {
			output.send(1, robot.getRobotId(), 0, 0, 0, 0, false);
			return;

			// Calculate parameters
		} else {
			// TODO enable me to be able to dribble
			// dribble = Math.abs(robot.getOrientation() - robot.getPosition().getAngle(World.getInstance().getBall().getPosition())) < 20 && robot.getPosition().getDeltaDistance(World.getInstance().getBall().getPosition()) < Robot.DIAMETER / 2 + 200;
			route = dplanner.getRoute(robot.getPosition(), destination, robot.getRobotId(), false);
			if(route == null){
				LOGGER.severe("Robot #" + robot.getRobotId() + " can't reach destination.");
				output.send(1, robot.getRobotId(), 0, 0, 0, 0, false);
				return;
			}
				
			if (route.size() > 0 && route.get(0) != null) {
				destination = route.get(0);
			} else {
				output.send(1, robot.getRobotId(), 0, 0, 0, 0, false);
				return;
			}

			double rotationToTarget = rotationToDest(target);
			double rotationToGoal = rotationToDest(destination);
			double speed;
			if(route.size() > 1)							//If we're not at our destination
				speed = getSpeed(getDistance()+50, 100);	//Don't slow down as much
			else
				speed = getSpeed(getDistance(), 100);
			
			double rotationSpeed = getRotationSpeed(rotationToTarget);

			// Overrule speed
			if (forcedSpeed > 0) {
				speed = forcedSpeed;
			}

			// Send commands to robot
			// direction and rotationAngle do nothing, set to 0
			// rotationSpeed inverted because the motors spin in opposite
			// direction
			output.send(1, robot.getRobotId(), (int)rotationToGoal, (int)speed, (int)rotationSpeed, chipKick, dribble);
			LOGGER.log(Level.INFO, robot.getRobotId() + "," + (int)rotationToGoal + "," + (int)speed + "," + (int)rotationSpeed + "," + chipKick + "," + dribble);
			
			// Set kick back to 0 to prevent kicking twice in a row
			chipKick = 0;
		}
	}

//	/**
//	 * Get rotationSpeed, calculates the speed at which to rotate based on degrees left to rotate
//	 * Precondition: -180 <= rotation <= 180
//	 * @param rotation
//	 * @return
//	 */
//	private double getRotationSpeed(double rotation) {
//
//
//		// must be between 0 and 50 percent, if it's higher than 50% rotating to
//		// the other direction is faster
//		double rotationPercent = rotation / 360;
//
//		// distance needed to rotate in mm
//		double rotationDistance = circumference * rotationPercent;
//
//		return (rotationDistance * DISTANCE_ROTATIONSPEED_COEFFICIENT);
//	}
	
	/**
	 * Get rotationSpeed, calculates the speed at which to rotate based on degrees left to rotate
	 * Precondition: -180 <= rotation <= 180
	 * @param rotation
	 * @return
	 */
	private double getRotationSpeed(double rotation) {
		return rotation/3.60;
//
//		// must be between 0 and 50 percent, if it's higher than 50% rotating to
//		// the other direction is faster
//		double rotationPercent = rotation / 360;
//
//		// distance needed to rotate in mm
//		double rotationDistance = circumference * rotationPercent;
//
//		return (rotationDistance * DISTANCE_ROTATIONSPEED_COEFFICIENT);
	}

	/**
	 * @returns the distance in millimeters between the {@link Robot} and the first {@link FieldPoint} calculated in the route.
	 * @see {@link #calculate()}
	 */
	private double getDistance() {
		double distance = 0;

		distance = robot.getPosition().getDeltaDistance(route.get(0));
//		if (route != null && !route.isEmpty()) {
//			distance += robot.getPosition().getDeltaDistance(route.get(0));
//
//			for (int i = 0; i < route.size() - 1; i++)
//				distance += route.get(i).getDeltaDistance(route.get(i + 1));
//		} else
//			distance = (int) robot.getPosition().getDeltaDistance(destination);

		return distance;
	}
	
	/**
	 * Returns the speed the robot should drive at.
	 * When a robot nears its destination, it should slow down.
	 * @param d The distance to travel in mm
	 * @param distanceToSlowDown If the robot has less distance to travel than the distance to slow down, the robot should slow down.
	 * @return The speed in mm/s
	 */
	private double getSpeed(double d, int distanceToSlowDown) {
		if(d > distanceToSlowDown)
			return MAX_VELOCITY;
		return ((d / distanceToSlowDown) * MAX_VELOCITY);
	}
	
	/**
	 * Calculate the needed rotation to destination
	 * @param newPoint The {@link FieldPoint} we wish to face.
	 * @return The rotation we need to make to face the given {@link FieldPoint}
	 */
	private double rotationToDest(FieldPoint newPoint) {
		// angle vector between old and new
		double newangle = robot.getPosition().getAngle(newPoint);
		double rot = (newangle - robot.getOrientation());

		if (rot > 180) {
			rot -= 360;
		}

		if (rot < -180) {
			rot += 360;
		}
		return rot;
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
}
