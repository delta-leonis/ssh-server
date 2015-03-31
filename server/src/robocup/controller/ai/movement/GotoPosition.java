package robocup.controller.ai.movement;

import java.util.LinkedList;

import robocup.model.FieldObject;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.output.ComInterface;

public class GotoPosition {

	// TODO find a better solution
	private static final double DISTANCE_ROTATIONSPEED_COEFFICIENT = 3;
	private FieldPoint destination;
	private FieldPoint target;
	private Robot robot;
	private ComInterface output;
	private int forcedSpeed = 0;
	private int chipKick = 0;
	private boolean dribble = false;
	private DijkstraPathPlanner dplanner = new DijkstraPathPlanner();
	private LinkedList<FieldPoint> route;
	
	private static final int MAX_VELOCITY =5000;

	/**
	 * Go to target position
	 * @param robot RobotObject
	 * @param output Output Connection
	 * @param destination Destination Position
	 */
	public GotoPosition(Robot robot, ComInterface output, FieldPoint destination) {
		this.robot = robot;
		this.output = output;
		this.destination = destination;
		this.target = destination;
	}

	/**
	 * Go to target object
	 * @param robot RobotObject
	 * @param output Output Connection
	 * @param target Target Object (position)
	 */
	public GotoPosition(Robot robot, ComInterface output, FieldObject target) {
		this.robot = robot;
		this.output = output;
		this.destination = target.getPosition();
		this.target = this.destination;
	}

	/**
	 * Go to goalPosition and `look` towards the destination
	 * @param robot RobotObject
	 * @param output Output Connection
	 * @param destination Position to drive to
	 * @param target Position to look at
	 */
	public GotoPosition(Robot robot, ComInterface output, FieldPoint destination, FieldPoint target) {
		this.robot = robot;
		this.output = output;
		this.destination = destination;
		this.target = target;
	}

	/**
	 * Go to goalPosition with a `forced` speed and `look` towards the destination
	 * @param robot RobotObject
	 * @param output Output Connection
	 * @param destination Position to drive to
	 * @param target Position to look at
	 * @param forcedSpeed Speed to drive with
	 */
	public GotoPosition(Robot robot, ComInterface output, FieldPoint destination, FieldPoint target, int forcedSpeed) {
		this.robot = robot;
		this.output = output;
		this.destination = destination;
		this.target = target;
		this.forcedSpeed = forcedSpeed;
	}

	/**
	 * Get Target
	 * @return TargetPoint
	 */
	public FieldPoint getTarget() {
		return target;
	}

	/**
	 * Set Target
	 * @param TargetPoint
	 */
	public void setTarget(FieldPoint p) {
		target = p;
	}

	/**
	 * Set Destination
	 * @param destination
	 */
	public void setDestination(FieldPoint destination) {
		this.destination = destination;
	}

	/**
	 * Get Goal position
	 * @return GoalPoint
	 * @deprecated replaced by getDestination
	 */
	public FieldPoint getGoal() {
		return destination;
	}

	/**
	 * Get Destination
	 * @return
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
	 * Calulate
	 */
	public void calculate() {
		if (destination == null || target == null) {
			robot.setOnSight(true);
			output.send(1, robot.getRobotId(), 0, 0, 0, 0, 0, 0, false);
			return;

			// Calculate parameters
		} else {
			robot.setOnSight(true);

			route = dplanner.getRoute(robot.getPosition(), destination, robot.getRobotId(), false);
			
			if (route.size() > 0 && route.get(0) != null) {
				destination = route.get(0);
			} else {
					output.send(1, robot.getRobotId(), 0, 0, 0, 0, 0, 0, false);
					return;
			}

			// TODO make robot stop when distance is reached, should be handled
			// in robot code
			double travelDistance = getDistance();
			double rotationToTarget = rotationToDest(target);
			double rotationToGoal = rotationToDest(destination);

			double speed = getSpeed(getDistance(), 100);
			double rotationSpeed = getRotationSpeed(rotationToTarget);

			// Overrule speed
			if (forcedSpeed > 0) {
				speed = forcedSpeed;
			}

			// Send commands to robot
			// direction and rotationAngle do nothing, set to 0
			// rotationSpeed inverted because the motors spin in opposite
			// direction
			output.send(1, robot.getRobotId(), (int)rotationToGoal, (int)speed, (int)travelDistance, 0, (int)-rotationSpeed, chipKick, dribble);
			

			// Set kick back to 0 to prevent kicking twice in a row
			chipKick = 0;
		}
	}

	/**
	 * Get rotationSpeed, calculates the speed at which to rotate based on degrees left to rotate
	 * Precondition: -180 <= rotation <= 180
	 * @param rotation
	 * @return
	 */
	private double getRotationSpeed(double rotation) {
		// calculate total circumference of robot
		double circumference = (Robot.DIAMETER * Math.PI);

		// must be between 0 and 50 percent, if it's higher than 50% rotating to
		// the other direction is faster
		double rotationPercent = rotation / 360;

		// distance needed to rotate in mm
		double rotationDistance = circumference * rotationPercent;

		return (rotationDistance * DISTANCE_ROTATIONSPEED_COEFFICIENT);
	}

	/**
	 * Get travel distance
	 * @return
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
	 * Get speed based on travel distance and rotation
	 * @param distance
	 * @param rotation
	 * @return
	 */
//	private int getSpeed(int distance, int rotation) {
//		// Defaults
//		int speed = 0;
//		int thresholdValue = 800;
//
//		// If distance to travel is less then the `threshold`, use a logarithmic
//		// formula for speed
//		if (distance < thresholdValue) {
//			speed = (int) (Math.log(distance) / Math.log(1.1)) * 8; // -
//																	// robotDiameter
//		} else if (Math.abs(rotation) > 10) {
//			speed = (180 - Math.abs(rotation)) * 16;
//		} else {
//			speed = 2400;
//		}
//
//		return speed;
//	}
	
	/**
	 * Returns the speed the robot should drive at.
	 * When a robot nears its destination, it should slow down.
	 * @param d The distance to travel in mm
	 * @param distanceToSlowDown If the robot has less distance to travel than the distance to slow down, the robot should slow down.
	 * @return The speed in degrees/s
	 */
	public double getSpeed(double d, int distanceToSlowDown) {
		if(d > distanceToSlowDown)
			return MAX_VELOCITY;
		return ((d / distanceToSlowDown) * MAX_VELOCITY);
	}
	
	/**
	 * Calculate the needed rotation to destination
	 * @param newPoint
	 * @return
	 */
	private double rotationToDest(FieldPoint newPoint) {
		// angle vector between old and new
		double newangle = robot.getPosition().getAngle(newPoint);
		double rot = (newangle - robot.getOrientation());

		if (rot > 180) {
			rot -= 360;
		}

		if (rot <= -180) {
			rot += 360;
		}
		return rot;
	}

	/**
	 * @return the dribble
	 */
	public boolean getDribble() {
		return dribble;
	}

	/**
	 * @param dribble the dribble to set
	 */
	public void setDribble(boolean dribble) {
		this.dribble = dribble;
	}
}
