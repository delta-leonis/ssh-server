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
