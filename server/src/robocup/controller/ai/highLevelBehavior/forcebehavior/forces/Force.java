package robocup.controller.ai.highLevelBehavior.forcebehavior.forces;

import robocup.model.Point;

public abstract class Force {
	private Point origin;
	private int power;
	private int scope;

	protected Force(Point origin, int power, int scope) {
		this.origin = origin;
		this.power = power;
		this.scope = scope;
	}

	public Point getOrigin() {
		return origin;
	}

	public int getPower() {
		return power;
	}

	public int getScope() {
		return scope;
	}

	/**
	 * Calculate if a point is affected by this force
	 * @param position the point
	 * @return true when the point is affected by this force
	 */
	public boolean affectsPoint(Point position) {
		return origin.getDeltaDistance(position) < scope;
	}
}
