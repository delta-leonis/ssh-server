package robocup.controller.ai.movement;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.logging.Logger;

import robocup.Main;
import robocup.model.FieldObject;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.model.World;
import robocup.output.ComInterface;

public class GotoPosition {

	private Point destination;
	private Point target;
	private Robot robot;
	private ComInterface output;
	private int forcedSpeed = 0;
	private int chipKick = 0;
	private boolean dribble = false;
	private static Logger LOGGER = Logger.getLogger(Main.class.getName());
	private DijkstraPathPlanner dplanner = new DijkstraPathPlanner();
	private LinkedList<Point> route;

	/**
	 * Go to target position
	 * 
	 * @param robot				RobotObject
	 * @param output			Output Connection
	 * @param destination 		Destination Position
	 */
	public GotoPosition(Robot robot, ComInterface output, Point destination) {
		this.robot = robot;
		this.output = output;
		this.destination = destination;
		this.target = destination;
	}

	/**
	 * Go to target object
	 * 
	 * @param robot				RobotObject
	 * @param output			Output Connection
	 * @param target 			Target Object (position)
	 */
	public GotoPosition(Robot robot, ComInterface output, FieldObject target) {
		this.robot = robot;
		this.output = output;
		this.destination = target.getPosition();
		this.target = this.destination;
	}

	/**
	 * Go to goalPosition and `look` towards the destination
	 * 
	 * @param robot				RobotObject
	 * @param output			Output Connection
	 * @param destination		Position to drive to
	 * @param target			Position to look at
	 */
	public GotoPosition(Robot robot, ComInterface output, Point destination, Point target) {
		this.robot = robot;
		this.output = output;
		this.destination = destination;
		this.target = target;
	}

	/**
	 * Go to goalPosition with a `forced` speed and `look` towards the destination
	 * 
	 * @param robot				RobotObject
	 * @param output			Output Connection
	 * @param destination		Position to drive to
	 * @param target			Position to look at
	 * @param forcedSpeed		Speed to drive with
	 */
	public GotoPosition(Robot robot, ComInterface output, Point destination, Point target, int forcedSpeed) {
		this.robot = robot;
		this.output = output;
		this.destination = destination;
		this.target = target;
		this.forcedSpeed = forcedSpeed;
	}

	/**
	 * Get Target
	 * @return	TargetPoint
	 */
	public Point getTarget() {
		return target;
	}

	/**
	 * Set Target
	 * @param TargetPoint
	 */
	public void setTarget(Point p) {
		target = p;
	}

	/**
	 * Set Goal
	 * @param GoalPoint
	 * @deprecated replaced by setDestination
	 */
	public void setGoal(Point p) {
		destination = p;
	}

	/**
	 * Set Destination
	 * @param destination
	 */
	public void setDestination(Point destination) {
		this.destination = destination;
	}

	/**
	 * Get Goal position
	 * @return GoalPoint
	 * @deprecated replaced by getDestination
	 */
	public Point getGoal() {
		return destination;
	}

	/**
	 * Get Destination
	 * @return
	 */
	public Point getDestination() {
		return destination;
	}

	/**
	 * Set the kicking or chipping power for the next message, resets to 0 after using it
	 * @param kick ranges 1-100 3for kicking, -1 to -100 for chipping power in percentages
	 */
	public void setKick(int chipKick) {
		this.chipKick = chipKick;
	}

	/**
	 * Calulate 
	 */
	public void calculate() {
		// Check for timeout
		if (timeOutCheck()) {

			/* Todo */

			// Both destination and target are required, if not set, default
			// position to idle
		} else if (destination == null || target == null) {
			robot.setOnSight(true);
			output.send(1, robot.getRobotID(), 0, 0, 0, 0, 0, 0, false);
			return;

			// Calculate parameters
		} else {
			robot.setOnSight(true);

			route = dplanner.getRoute(robot.getPosition(), destination, robot.getRobotID());

			if (route.size()  > 0 && route.get(0) != null)
				destination = route.get(0);

			int targetDirection = rotationToDest(this.target);
			int travelDistance = getDistance();
			int rotationToGoal = rotationToDest(destination);
			targetDirection += 180;
			if(targetDirection > 180) targetDirection-= 360;
			
			
			
			int speed = getSpeed(getDistance(), rotationToGoal);
			float rotationSpeedFloat = getRotationSpeed(targetDirection);
			int rotationSpeed = (int) rotationSpeedFloat;

			// Overrule speed
			if (forcedSpeed > 0) {
				speed = forcedSpeed;
			}

//			System.out.println(robot.getPosition().getDeltaDistance(World.getInstance().getBall().getPosition()));
			// Send commands to robot
			output.send(1, robot.getRobotID(), rotationToGoal, speed, travelDistance, targetDirection, rotationSpeed,
					chipKick, dribble);

			// Set kick back to 0 to prevent kicking twice in a row
			chipKick = 0;
		}
	}

	/**
	 * Get rotationSpeed, calculates the speed at which to rotate based on degrees left to rotate
	 * 
	 * @param rotation
	 * @return
	 */
	public float getRotationSpeed(int rotation) {
		// used natural logarithmic function to determine rotationSpeed;
		// double rotationCalc = Math.abs(rotation);

		float rotationSpeed = (float) Math.toRadians(rotation);
		rotationSpeed = rotationSpeed * 45;
		/*
		if(rotation < 10) {
			rotationSpeed *= 0;
		}
		else if(rotation < 40){
			rotationSpeed *= 20;
		}
		else{
			rotationSpeed = (float) Math.toRadians(rotation);
			rotationSpeed = rotationSpeed * 40;
		}
		// Return calculated speed
//		System.out.println("rotationSpeed: "+ rotationSpeed * 10);
		System.out.println("rotation: " + rotation);
		*/
		return rotationSpeed;
	}

	/**
	 * Get travel distance
	 * @return
	 */
	public int getDistance() {
		int distance = 0;

		if (route != null) {
			for (int i = -1; i < route.size() - 1; i++) {
				if (i == -1) {
					distance += robot.getPosition().getDeltaDistance(route.get(0));
				} else {
					distance += route.get(i).getDeltaDistance(route.get(i + 1));
				}
			}
		} else {
			distance = (int) robot.getPosition().getDeltaDistance(destination);
		}

		return distance;
	}

	/**
	 * Get speed based on travel distance and rotation
	 * @param distance
	 * @param rotation
	 * @return
	 */
	public int getSpeed(int distance, int rotation) {
		// Defaults
		int speed = 0;
		int thresholdValue = 800;

		// If distance to travel is less then the `threshold`, use a logarithmic
		// formula for speed
		if (distance < thresholdValue) {
			speed = (int) (Math.log(distance) / Math.log(1.1)) * 8; // -
																	// robotDiameter
		} else if (Math.abs(rotation) > 10) {
			speed = (180 - Math.abs(rotation)) * 16;
		} else {
			speed = 2400;
		}

		return 800;
	}

	/**
	 * Calculate the needed rotation to destination
	 * @param newPoint
	 * @return
	 */
	public int rotationToDest(Point newPoint) {
		// angle vector between old and new
		double dy = newPoint.getY() - robot.getPosition().getY();
		double dx = newPoint.getX() - robot.getPosition().getX();
		double newRad = Math.atan2(dy, dx);
		int rot = (int) (Math.toDegrees(newRad) - robot.getOrientation());

		if (rot > 180) {
			rot -= 360;
		}

		if (rot <= -180) {
			rot += 360;
		}
		return rot;
	}

	/**
	 * Check if the robot timed out, should be used at the start of calculate in every low level behavior
	 * @return true if the robot timed out
	 */
	public boolean timeOutCheck() {
		boolean failed = false;

		if ((robot.getLastUpdateTime() + 0.20) < (Calendar.getInstance().getTimeInMillis() / 1000)) {
			failed = true;
		}
		if (!World.getInstance().getReferee().isStart()) {
			failed = true;
		}

		if (failed) {
			LOGGER.warning("Robot " + robot.getRobotID() + " is not on sight");
			LOGGER.warning("Time: " + (Calendar.getInstance().getTimeInMillis() / 1000));
			LOGGER.warning("Robot: " + (robot.getLastUpdateTime()));

			robot.setOnSight(false);
			output.send(1, robot.getRobotID(), 0, 0, 0, 0, 0, 0, false); // stop
																			// moving
																			// if
																			// the
																			// robot
																			// timed
																			// out
		}
		return failed;
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
