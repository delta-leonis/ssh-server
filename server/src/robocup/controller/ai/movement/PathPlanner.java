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
	}

	public enum Direction {
		LEFT,
		RIGHT
	}
	
	/**
	 * Get the next point on the route to the endNode
	 * @param beginNode position of the robot
	 * @param endNode destination
	 * @param robotId id of the robot
	 * @return returns the next point on the route towards the destination
	 */
	public Point getNextRoutePoint(Point beginNode, Point endNode, int robotId) {
		objects = new ArrayList<Robot>();
		objects.addAll(World.getInstance().getEnemy().getRobots());
		objects.addAll(World.getInstance().getAlly().getRobots());
		
		Point collisionPoint = getCollision(beginNode, endNode, robotId);
		
		// no collision, all okay
		if(collisionPoint == null)
			return endNode;

		SubPoint left = getNextRouteSubPoint(beginNode, new SubPoint(0, getNewSubPoint(collisionPoint, beginNode, Direction.LEFT)), 
																		Direction.LEFT, robotId);
		SubPoint right = getNextRouteSubPoint(beginNode, new SubPoint(0, getNewSubPoint(collisionPoint, beginNode, Direction.RIGHT)), 
																		Direction.RIGHT, robotId);

//		System.out.println("Left: " + left.subPoint());
//		System.out.println("Right: " + right.subPoint());

		if(left != null && right != null) {
			return right.iteration() <= left.iteration() ? right.subPoint() : left.subPoint();
		}
		if(left != null)
			return left.subPoint();
		if(right != null)
			return right.subPoint();
		
		// left and right subpoints too far, lets wreck some enemies
//		if(left == null && right == null)
			return endNode;
	}
	
	/**
	 * Calculate the point where collision will occur
	 * @param beginNode current position of the robot
	 * @param endNode destination
	 * @param robotId id of the robot
	 * @return returns the point where collision will occur, null when no collision detected
	 */
	private Point getCollision(Point beginNode, Point endNode, int robotId) {
		Line2D line = new Line2D.Float(beginNode.getX(), beginNode.getY(), endNode.getX(), endNode.getY());

		for (Robot r : objects) {
			if(r.getPosition() != null) {
				Rectangle2D rect = new Rectangle2D.Float(r.getPosition().getX(), r.getPosition().getY(), 360, 360);

				if(line.intersects(rect) && r.getRobotID() != robotId)
					return r.getPosition();
			}
		}

		return null;
	}

	/**
	 * Calculate the next sub point to the left or right
	 * @param beginNode position of the robot
	 * @param subPoint previous subpoint
	 * @param direction direction where the subpoint needs to go, either LEFT or RIGHT
	 * @param robotId id of the robot
	 * @return a new subpoint in the given direction
	 */
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
	 * @param collisionPoint point where the collision will occur
	 * @param beginNode position of the robot
	 * @param direction direction where the new subpoint will be, either LEFT or RIGHT
	 * @return returns a new subpoint
	 */
	public Point getNewSubPoint(Point collisionPoint, Point beginNode, Direction direction) {
		int offset = 200;
		double angle = Math.atan2(collisionPoint.getY() - beginNode.getY(), collisionPoint.getX() - beginNode.getX());
		double dx = 0;
		double dy = 0;
		
		switch(direction) {
			case LEFT:
				dx = -Math.sin(angle) * offset;
				dy = Math.cos(angle) * offset;
				break;
			case RIGHT:
				dx = Math.sin(angle) * offset;
				dy = -Math.cos(angle) * offset;
				break;
		}
	
		return new Point(collisionPoint.getX() + (int) dx, collisionPoint.getY() + (int) dy);
	}
}
