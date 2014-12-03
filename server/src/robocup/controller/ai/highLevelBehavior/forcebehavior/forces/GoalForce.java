package robocup.controller.ai.highLevelBehavior.forcebehavior.forces;

import robocup.model.Point;

public class GoalForce extends Force {
	
	public GoalForce(Point origin, int power, int scope) {
		super(origin, power, scope);
	}

	public boolean affectsPoint(Point position) {
		// TODO calculate if point is inside goal area
		return false;
	}
}
