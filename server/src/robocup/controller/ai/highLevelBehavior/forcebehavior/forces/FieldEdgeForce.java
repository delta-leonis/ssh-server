package robocup.controller.ai.highLevelBehavior.forcebehavior.forces;

import robocup.model.Point;

public class FieldEdgeForce extends Force {

	protected FieldEdgeForce(int power, int scope) {
		super(null, power, scope);
	}

	public boolean affectsPoint(Point position) {
		// TODO calculate range to edge of field
		return false;
	}
}
