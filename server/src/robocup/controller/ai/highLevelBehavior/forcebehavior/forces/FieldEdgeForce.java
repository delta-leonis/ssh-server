package robocup.controller.ai.highLevelBehavior.forcebehavior.forces;

import robocup.model.Point;

public class FieldEdgeForce extends Force {

	public FieldEdgeForce(int power, int scope) {
		super(null, power, scope);
	}

	public boolean affectsPoint(Point position) {
		int maxY = world.getField().getWidth() / 2;
		int maxX = world.getField().getLength() / 2;
		return maxX - Math.abs(position.getX()) < scope || maxY - Math.abs(position.getY()) < scope;
	}
}
