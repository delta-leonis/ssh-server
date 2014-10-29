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

	/**
	 * get the next free node on the route to the endNode
	 * 
	 * @param beginNode
	 *            to calculate the best possible route a beginNode is needed
	 * @param endNode
	 *            to calculate the best possible route a endNode is needed
	 * @return next available free node on the route
	 */
	public Point getNextRoutePoint(Point beginNode, Point endNode, int robotId) {

		//create new line
		Line2D line = new Line2D.Float(beginNode.getX(), beginNode.getY(), endNode.getX(), endNode.getY());
		//get intersecting object(rectangle)
		Rectangle2D temp = lineIntersectsObject(line, robotId);
		//init subNode to null
		Point subNode = null;
		//while there are intersecting objects keep inside the loop
		while (temp != null) {
			//make new subNode
			subNode = getNewSubPoint(temp, beginNode, subNode);
			line = new Line2D.Float(beginNode.getX(), beginNode.getY(), subNode.getX(), subNode.getY());
			temp = lineIntersectsObject(line, robotId);
			System.out.println(subNode);

		}
		if (subNode != null) {
			System.out.println("returned subNode");
			return subNode;
		} else {
			System.out.println("returned endNode");
			return endNode;
		}
	}

	/**
	 * Check if one of the robots intersects the line on which the robot is
	 * going to travel
	 * 
	 * @param lineline
	 *            A Line2D is needed to check if there is an intersection with a
	 *            robot object
	 * @return return the object
	 */
	private Rectangle2D lineIntersectsObject(Line2D line, int robotId) {
		Rectangle2D rect = null;
		for (Robot r : objects) {
			rect = new Rectangle2D.Float(r.getPosition().getX(), r.getPosition().getY(), 300, 300);
			if (line.intersects(rect) && r.getRobotID() != robotId) {
				break;
			}
			rect = null;
		}
		return rect;
	}

	private Point getNewSubPoint(Rectangle2D object, Point beginNode, Point subNode) {
		// get new random Point away from obstacle
		
		//dx = sin(gamma) * z
		//dy = cos(gamma) * z
		//gamma = Math.atan2(target.getX() - x, target.getY() - y)

		if (subNode != null) {
			
			
			
			//implement method to calculate new points
			subNode = new Point(subNode.getX() + 200, subNode.getY() );
		} else {
			subNode = new Point((float) object.getX(), (float) object.getY());
		}

		return subNode;

	}
}
