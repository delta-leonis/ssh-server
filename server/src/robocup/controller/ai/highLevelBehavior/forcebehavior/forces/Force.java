package robocup.controller.ai.highLevelBehavior.forcebehavior.forces;

import robocup.model.Point;
import robocup.model.World;

public abstract class Force {
	private Point origin;
	protected int power;
	protected int scope;
	protected World world;

	/**
	 * Create a Force object
	 * @param origin position of the origin of this force
	 * @param power the power of this force
	 * @param scope the range where this force affects other objects
	 */
	protected Force(Point origin, int power, int scope) {
		this.origin = origin;
		this.power = power;
		this.scope = scope;
		world = World.getInstance();
	}

	/**
	 * Get the origin
	 * @return the origin
	 */
	public Point getOrigin() {
		return origin;
	}

	/**
	 * Get the power
	 * @return the power
	 */
	public int getPower() {
		return power;
	}

	/**
	 * Get the scope
	 * @return the scope
	 */
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

	/**
	 * Calculate in what direction this force works in relation to the position of the other object
	 * @param position the object on which the force works
	 * @return the direction where the force is going
	 */
	public abstract int getDirection(Point position);
}
