package robocup.controller.ai.highLevelBehavior.forcebehavior.forces;

import robocup.model.Point;
import robocup.model.Robot;

public class RobotForce extends Force {

	private Robot robot;

	public RobotForce(Robot robot, int power, int scope) {
		super(robot.getPosition(), power, scope);
		this.robot = robot;
	}

	public Robot getRobot() {
		return robot;
	}

	public boolean affectsPoint(Point position) {
		return super.affectsPoint(position);
	}

	@Override
	public int getDirection(Point position) {
		return power > 0 ? position.getAngle(robot.getPosition()) : robot.getPosition().getAngle(position);
	}
}
