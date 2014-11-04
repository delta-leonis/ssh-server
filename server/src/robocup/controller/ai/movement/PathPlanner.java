package robocup.controller.ai.movement;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Calendar;

import robocup.model.Point;
import robocup.model.Robot;
import robocup.model.World;


public class PathPlanner {
	ArrayList<Robot> objects;
	private static int  robotObjectWidth = 270;

	/**
	 * Create A Path planner
	 */
	public PathPlanner() {
		objects = new ArrayList<Robot>();
		objects.addAll(World.getInstance().getEnemy().getRobots());
		objects.addAll(World.getInstance().getAlly().getRobots());
	}

	/**
	 * get the next free node on the route to the endNode
	 * 
	 * @param beginNode
	 *            starting point of route
	 * @param endNode
	 *            end point of route
	 * @param robotId
	 * @return next free node on route
	 */
	public Point getNextRoutePoint(Point beginNode, Point endNode, int robotId) {
//		long time =  System.nanoTime();

		// make new line with given start and endpoints
		Line2D line = new Line2D.Float(beginNode.getX(), beginNode.getY(), endNode.getX(), endNode.getY());
		// get intersecting obstacle
		Rectangle2D obstacle = lineIntersectsObject(line, robotId);
		Point subNode = null;
		// as long as there is an obstacle on the calculated path keep adding
		// new subNodes
		while (obstacle != null) {
			
			// create new subPoint
			subNode = getNewSubPoint(obstacle, beginNode, subNode);
			// create new line with calculated subNode
			line = new Line2D.Float(beginNode.getX(), beginNode.getY(), subNode.getX(), subNode.getY());
			// check if new line intersects an object
			obstacle = lineIntersectsObject(line, robotId);
			

			/*
			 * object avoidance when robot is inside a objection detection box of another robot.
			 */
			//check if startnode is inside the obstacle avoid box and create new subpoint to move outside of it. also a really basic version of object avoidance
			if(obstacle != null && obstacle.contains(beginNode.getX(), beginNode.getY())){
				subNode = getNewSubPoint(obstacle, beginNode, subNode);
				break;
			}
			
		}
//		System.out.println("passed time: " + (System.nanoTime() - time));
		if (subNode != null) {
			return subNode;
		} else {
			return endNode;
		}
	}

	/**
	 * Check if one of the robots intersects the line on which the robot is
	 * going to travel
	 * 
	 * @param line
	 *            line2D line which needs to be checked for intersections
	 * @param robotId
	 * 
	 * @return return intersecting object
	 */
	private Rectangle2D lineIntersectsObject(Line2D line, int robotId) {
		Rectangle2D rect = null;
		// check all robots/objects if they are on the path
		for (Robot r : objects) {
			rect = new Rectangle2D.Float(r.getPosition().getX(), r.getPosition().getY(), robotObjectWidth, robotObjectWidth);
			if (line.intersects(rect) && r.getRobotID() != robotId ) { 
				break;
			}
			rect = null;
		}
		return rect;
	}

	/**
	 * calculate new subPoint on route
	 * @param obstacle 
	 * @param beginNode start Point
	 * @param subNode subNode Point
	 * @return next subNode Point
	 */
	private Point getNewSubPoint(Rectangle2D obstacle, Point beginNode, Point subNode) {
		// get new random Point away from obstacle
		if (subNode != null) {

			int offset = 200;
			double angle = Math.atan2(subNode.getY() - beginNode.getY(), subNode.getX() - beginNode.getX());

			// if using left side of object
			double dx = Math.sin(angle) * offset * -1;
			double dy = Math.cos(angle) * offset;

			// if using right side
			// double dx = Math.sin(angle) * offset;
			// double dy = Math.cos(angle) * offset *-1;

			// implement method to calculate new points
			subNode = new Point(subNode.getX() + (float) dx, subNode.getY() + (float) dy);
		} else {
			subNode = new Point((float) obstacle.getX(), (float) obstacle.getY());
		}

		return subNode;

	}
}
