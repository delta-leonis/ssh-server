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
	 * @param beginNode to calculate the best possible route a beginNode is needed
	 * @param endNode to calculate the best possible route a endNode is needed
	 * @return next available free node on the route
	 */
	public Point getNextRoutePoint(Point beginNode, Point endNode) {
		Point subNode = endNode;
		Line2D line = new Line2D.Float(beginNode.getX(), beginNode.getY(), subNode.getX(), subNode.getY());
		while (true) {
			Rectangle2D temp = lineIntersectsObject(line);
			if(temp == null)break;
			subNode = getNewSubPoint(temp);
		}

		return subNode;
	}
	/**
	 * Check if one of the robots intersects the line on which the robot is going to travel
	 * @param line A Line2D is needed to check if there is an intersection with a robot object
	 * @return return the object 
	 */
	public Rectangle2D lineIntersectsObject(Line2D line)
	{
		for(Robot r : objects){
			Rectangle2D rect = new Rectangle2D.Float(r.getPosition().getX(), r.getPosition().getY(), 30, 30);
			if(line.intersects(rect)) return rect;
		}
		return null;
	}
	
	public Point getNewSubPoint(Rectangle2D object){
		return new Point((float)object.getCenterX(),(float)object.getMaxY());
	}
}
