package robocup.controller.ai.movement;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.model.World;

public class PathPlanner {
	ArrayList<Robot> objects;

	/**
	 * Create A Path planner
	 */
	public PathPlanner() {
		objects = new ArrayList<Robot>();
		objects.addAll(World.getInstance().getEnemy().getRobots());
		objects.addAll(World.getInstance().getAlly().getRobots());
	}

	public enum Direction {
		LEFT,
		RIGHT
	}
	
	public Point getNextRoutePoint(Point beginNode, Point endNode, int robotId) {
		Point collisionPoint = getCollision(beginNode, endNode, robotId);
		
		// no collision, all okay
		if(collisionPoint == null)
			return endNode;
		
		SubPoint left = getNextRouteSubPoint(beginNode, new SubPoint(0, getNewSubPoint(collisionPoint, beginNode, Direction.LEFT)), 
																		Direction.LEFT, robotId);
		SubPoint right = getNextRouteSubPoint(beginNode, new SubPoint(0, getNewSubPoint(collisionPoint, beginNode, Direction.RIGHT)), 
																		Direction.RIGHT, robotId);

		if(left != null && right != null)
			return left.iteration() <= right.iteration() ? left.subPoint() : right.subPoint();
		if(left != null)
			return left.subPoint();
		if(right != null)
			return right.subPoint();
		
		// left and right subpoints too far, lets wreck some enemies
//		if(left == null && right == null)
			return endNode;
	}
	
	private Point getCollision(Point beginNode, Point endNode, int robotId) {
		Line2D line = new Line2D.Float(beginNode.getX(), beginNode.getY(), endNode.getX(), endNode.getY());

		for (Robot r : objects) {
			Rectangle2D rect = new Rectangle2D.Float(r.getPosition().getX(), r.getPosition().getY(), 300, 300);
			
			if(line.intersects(rect) && r.getRobotID() != robotId)
				return r.getPosition();
		}

		return null;
	}

	private SubPoint getNextRouteSubPoint(Point beginNode, SubPoint subPoint, Direction direction, int robotId) {
		// base-case, return null when iteration is 4
		if(subPoint.iteration() == 4)
			return null;

		// calculate if there's collision with subpoint
		// return subpoint when no collision
		Point collisionPoint = getCollision(beginNode, subPoint.subPoint(), robotId);
		if(collisionPoint == null)
			return subPoint;

		// calculate new subpoint, call self with new subpoint and higher iteration, going either left or right
		// return new subpoint
		Point newPosition = getNewSubPoint(collisionPoint, beginNode, direction);
		return getNextRouteSubPoint(beginNode, new SubPoint(subPoint.iteration() + 1, newPosition), direction, robotId);
	}
	
	/**
	 * Helper class to return 2 different objects at once
	 * Pair with iteration as first, subPoint as second
	 */
	private class SubPoint {
		private int iteration;
		private Point subPoint;

		public SubPoint(int iteration, Point subPoint) {
			this.iteration = iteration;
			this.subPoint = subPoint;
		}

		public int iteration() { return iteration; }
		public Point subPoint() { return subPoint; }
	}
	
	/**
	 * calculate a new subpoint to the left or right of the object to avoid
	 * @return
	 */
	public Point getNewSubPoint(Point collisionPoint, Point beginNode, Direction direction) {
		int offset = 200;
		int angle = (int) Math.atan2(collisionPoint.getY() - beginNode.getY(), collisionPoint.getX() - beginNode.getX());
		int dx = 0;
		int dy = 0;
		
		switch(direction) {
			case LEFT:
				dx = (int) -Math.sin(angle) * offset;
				dy = (int) Math.cos(angle) * offset;
				break;
			case RIGHT:
				dx = (int) Math.sin(angle) * offset;
				dy = (int) -Math.cos(angle) * offset;
				break;
		}
		
		return new Point(collisionPoint.getX() + dx, collisionPoint.getY() + dy);
	}
}
