package robocup.controller.ai.highLevelBehavior.forcebehavior.forces;

import robocup.model.FieldPoint;

public class GoalForce extends Force {

	/**
	 * Create a GoalForce
	 * @param power the power
	 * @param scope the scope (size of the goal area)
	 */
	public GoalForce(int power, int scope) {
		super(null, power, scope);
	}

	/**
	 * Calculate if a point is affected by this force
	 * A point is affected by a GoalForce when the distance to one of the goal posts is less than the scope
	 */
	public boolean affectsPoint(FieldPoint position) {
		int dist1 = (int) position.getDeltaDistance(world.getField().getEastGoal().getFrontSouth());
		int dist2 = (int) position.getDeltaDistance(world.getField().getEastGoal().getFrontNorth());
		int dist3 = (int) position.getDeltaDistance(world.getField().getWestGoal().getFrontSouth());
		int dist4 = (int) position.getDeltaDistance(world.getField().getWestGoal().getFrontNorth());
		return dist1 < scope && dist2 < scope || dist3 < scope && dist4 < scope;
	}

	/**
	 * Get the direction, 180 when on the right side of the field, 0 when on the left side
	 * (pushing away from the goal)
	 */
	public int getDirection(FieldPoint position) {
		return position.getX() > 0 ? 180 : 0;
	}
}
