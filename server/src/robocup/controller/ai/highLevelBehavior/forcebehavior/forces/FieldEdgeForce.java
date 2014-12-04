package robocup.controller.ai.highLevelBehavior.forcebehavior.forces;

import robocup.model.Point;

public class FieldEdgeForce extends Force {

	/**
	 * Create a FieldEdgeForce, pushing away from the edge of the field
	 * @param power the power
	 * @param scope the scope
	 */
	public FieldEdgeForce(int power, int scope) {
		super(null, power, scope);
	}

	/**
	 * Calculate if a point is affected by this force
	 * A point is affected when the distance to the edge of the field is less than the scope
	 */
	public boolean affectsPoint(Point position) {
		int maxY = world.getField().getWidth() / 2;
		int maxX = world.getField().getLength() / 2;
		return maxX - Math.abs(position.getX()) < scope || maxY - Math.abs(position.getY()) < scope;
	}

	/**
	 * Get the direction, facing away from the edge of the field
	 */
	public int getDirection(Point position) {
		boolean maxXReached = world.getField().getLength() / 2 - Math.abs(position.getX()) < scope;
		boolean maxYReached = world.getField().getWidth() / 2 - Math.abs(position.getY()) < scope;

		int direction = 0;

		if (maxXReached && maxYReached)
			if (position.getX() > 0) {
				if (position.getY() > 0)
					direction = -135;
				else
					direction = -45;
			} else {
				if (position.getY() > 0)
					direction = 135;
				else
					direction = 45;
			}
		else if (maxXReached)
			direction = position.getX() > 0 ? 180 : 0;
		else if (maxYReached)
			direction = position.getY() > 0 ? -90 : 90;

		return direction;
	}
}
