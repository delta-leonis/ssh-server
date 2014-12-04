package robocup.controller.ai.highLevelBehavior.forcebehavior.forces;

import robocup.model.Point;
import robocup.model.World;

public abstract class Force {
	private Point origin;
	protected int power;
	protected int scope;
	protected World world;

	protected Force(Point origin, int power, int scope) {
		this.origin = origin;
		this.power = power;
		this.scope = scope;
		world = World.getInstance();
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

	public abstract int getDirection(Point position);
}
