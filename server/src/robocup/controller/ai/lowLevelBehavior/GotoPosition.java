package robocup.controller.ai.lowLevelBehavior;

import java.util.Calendar;
import java.util.logging.Logger;

import robocup.Main;
import robocup.model.FieldObject;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.model.World;
import robocup.output.ComInterface;

public class GotoPosition {

	private Point goalPosition;
	private Point targetPosition;
	private Robot robot;
	private ComInterface output;
	private int forcedSpeed = 0;
	private static Logger LOGGER = Logger.getLogger(Main.class.getName());

	public GotoPosition(Robot robot, ComInterface output, Point newPosition) {
		this.robot = robot;
		this.output = output;
		this.goalPosition = newPosition;
		this.targetPosition = newPosition;
	}

	public GotoPosition(Robot robot, ComInterface output, FieldObject target) {
		this.robot = robot;
		this.output = output;
		goalPosition = target.getPosition();
		this.targetPosition = goalPosition;
	}

	public GotoPosition(Robot robot, ComInterface output, Point goalPosition, Point targetPosition) {
		this.robot = robot;
		this.output = output;
		this.goalPosition = goalPosition;
		this.targetPosition = targetPosition;
	}
	
	public GotoPosition(Robot robot, ComInterface output, Point goalPosition, Point targetPosition, int forcedSpeed) {
		this.robot = robot;
		this.output = output;
		this.goalPosition = goalPosition;
		this.targetPosition = targetPosition;
		this.forcedSpeed  = forcedSpeed;
	}

	public Point getTarget() {
		return targetPosition;
	}

	public void setTarget(Point p) {
		targetPosition = p;
	}

	public void setGoal(Point p) {
		goalPosition = p;
	}
	
	public Point getGoal(){
		return goalPosition;
	}

	public void calculate() {
		if (timeOutCheck()) {

		} else if (goalPosition == null || targetPosition == null) {
			robot.setOnSight(true);
			output.send(1, robot.getRobotID(), 0, 0, 0, 0, 0, 0, false);
			return;
		} else {
			robot.setOnSight(true);
			int targetDirection = rotationToDest(this.targetPosition);
			int travelDistance = getDistance();
			int rotationToGoal = rotationToDest(goalPosition);
			int speed = 0;
			int rotationSpeed = 0;

			speed = getSpeed(getDistance(), rotationToGoal);
			float rotationSpeedFloat = getRotationSpeed(targetDirection);
			//float tSpeed = ((45.553093f / 360) * rotationSpeedFloat);
			rotationSpeed = (int)rotationSpeedFloat;
			//rotationSpeed = (int) tSpeed;

			output.send(1, robot.getRobotID(), rotationToGoal, forcedSpeed > 0 ? forcedSpeed : speed, travelDistance, targetDirection, rotationSpeed,
					0, false);
		}
	}

	public float getRotationSpeed(int rotation) {
		// 0.0006 * x^3
		
		// used natural logarithmic function to determine rotationSpeed;
		double rotationCalc = Math.abs(rotation);
		
		//rotationLog = Math.log(rotationLog) / Math.log(1.1);
		//rotationLog = rotationLog * 4; // * 4
		rotationCalc = rotationCalc * 0.06;
		
		float rotationSpeed = (float) rotationCalc;
		
		if (rotationSpeed < 0) {
			rotationSpeed = 0;
		}
		if (rotation > 0) {
			rotationSpeed *= -1;
		}
		System.out.println(" rot speed" + rotationSpeed);
		return rotationSpeed;
	}

	public int getDistance() {
		double dx = (robot.getPosition().getX() - goalPosition.getX());
		double dy = (robot.getPosition().getY() - goalPosition.getY());
		return (int) Math.sqrt(dx * dx + dy * dy);
	}

	public int getSpeed(int distance, int rotation) {
		int speed = 0;
		int thresholdValue = 800;
		if (distance < thresholdValue) {
			speed = (int) (Math.log(distance) / Math.log(1.1)) * 8; // -
																	// robotDiameter
		} else if (Math.abs(rotation) > 10) {
			speed = (180 - Math.abs(rotation)) * 8;
		} else {
			speed = 1200;
		}
		return speed;
	}
	
	/**
	 * Calculate the needed rotation to destination
	 * @param newPoint
	 * @return
	 */
	public int rotationToDest(Point newPoint){
		//angle vector between old and new
		double dy = newPoint.getY() - robot.getPosition().getY();
		double dx = newPoint.getX() - robot.getPosition().getX();
		double newRad = Math.atan2(dy, dx);
		int rot = (int)(Math.toDegrees(newRad) - robot.getOrientation());
		if( rot > 180 )
			rot -= 360;
		if (rot <= -180 )
			rot += 360;
		return  rot;
	}
	
	/**
	 * Check if the robot timed out, should be used at the start of calculate in every low level behavior
	 * @return true if the robot timed out
	 */
	public boolean timeOutCheck() {
		boolean failed = robot.getLastUpdateTime() + 0.20 < Calendar.getInstance().getTimeInMillis()/1000 ||
				!World.getInstance().getReferee().isStart();
		
		if(failed) {
			LOGGER.warning("Robot " + robot.getRobotID() + " is not on sight");
			LOGGER.warning("Time: " + (Calendar.getInstance().getTimeInMillis()/1000));
			LOGGER.warning("Robot: " + (robot.getLastUpdateTime()));

			robot.setOnSight(false);
			output.send(1, robot.getRobotID(), 0, 0, 0, 0, 0, 0, false);  // stop moving if the robot timed out
		}
		return failed;
	}
}
