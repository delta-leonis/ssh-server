package robocup.controller.ai.highLevelBehavior.forcebehavior.forces;

import robocup.model.FieldPoint;
import robocup.model.Robot;

public class RobotForce extends Force {

	private Robot robot;

	/**
	 * Create a RobotForce
	 * @param robot the robot
	 * @param power the power
	 * @param scope the scope
	 */
	public RobotForce(Robot robot, int power, int scope) {
		super(robot.getPosition(), power, scope);
		this.robot = robot;
	}

	/**
	 * Get the robot
	 * @return the robot
	 */
	public Robot getRobot() {
		return robot;
	}

	/**
	 * Calculate if a point is affected by this force
	 * 	true when the distance of the robot to the point is within the scope
	 */
	public boolean affectsPoint(FieldPoint position) {
		return super.affectsPoint(position);
	}

	/**
	 * If the power is positive use the angle from the position to the robot (pulling towards the robot)
	 * If the power is negative use the angle from the robot to the position (pushing away from the robot)
	 */
	public int getDirection(FieldPoint position) {
		return (int) (power > 0 ? position.getAngle(robot.getPosition()) : robot.getPosition().getAngle(position));
	}
}
