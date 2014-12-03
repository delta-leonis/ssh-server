package robocup.controller.ai.highLevelBehavior.forcebehavior.forces;

import robocup.model.Point;
import robocup.model.Robot;

public class RobotForce extends Force {

	public RobotForce(Robot robot, int power, int scope) {
		super(robot.getPosition(), power, scope);
	}

	public boolean affectsPoint(Point position) {
		return super.affectsPoint(position);
	}
}
