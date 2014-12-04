package robocup.controller.ai.highLevelBehavior.forcebehavior.forces;

import robocup.model.Point;

public class GoalForce extends Force {

	public GoalForce(int power, int scope) {
		super(null, power, scope);
	}

	public boolean affectsPoint(Point position) {
		int dist1 = (int) position.getDeltaDistance(world.getField().getGoal().get(0).getFrontLeft());
		int dist2 = (int) position.getDeltaDistance(world.getField().getGoal().get(0).getFrontRight());
		int dist3 = (int) position.getDeltaDistance(world.getField().getGoal().get(1).getFrontLeft());
		int dist4 = (int) position.getDeltaDistance(world.getField().getGoal().get(1).getFrontRight());
		return dist1 < scope && dist2 < scope || dist3 < scope && dist4 < scope;
	}

	public int getDirection(Point position) {
		return position.getX() > 0 ? 180 : 0;
	}
}
