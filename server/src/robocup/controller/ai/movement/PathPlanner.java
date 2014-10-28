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
	public Point getNextRoutePoint(Point beginNode, Point endNode, int robotId) {
		Point subNode = endNode;
		Line2D line = new Line2D.Float(beginNode.getX(), beginNode.getY(), subNode.getX(), subNode.getY());
		while (true) {
//			System.out.println("nop");
//			System.out.println("beginNodeX: "+beginNode.getX() + " beginNodeY: " + beginNode.getY());
//			System.out.println("subNodeX: "+subNode.getX() + " subNodeY: " + subNode.getY());
			Rectangle2D temp = lineIntersectsObject(line, robotId);
//			System.out.println("temp: " + temp);
			if(temp == null){
				
				break;
			}
			subNode = getNewSubPoint(temp, beginNode);
			line = new Line2D.Float(beginNode.getX(), beginNode.getY(), subNode.getX(), subNode.getY());

		}
//		System.out.println("snx:" + subNode.getX() + " snY: " + subNode.getY());
		return subNode;
	}
	/**
	 * Check if one of the robots intersects the line on which the robot is going to travel
	 * @param line A Line2D is needed to check if there is an intersection with a robot object
	 * @return return the object 
	 */
	private Rectangle2D lineIntersectsObject(Line2D line, int robotId)
	{
		Rectangle2D rect = null;
		for(Robot r : objects){
			rect = new Rectangle2D.Float(r.getPosition().getX(), r.getPosition().getY(), 100, 100);
			if(line.intersects(rect) && r.getRobotID() != robotId){
//				System.out.println(r.getRobotID());
//				System.out.println("robot: "+r.getRobotID() + "posx:" + rect.getCenterX() + "posy: " + rect.getCenterY());
				break;
			}
			rect = null;
		}
		return rect;
	}
	
	private Point getNewSubPoint(Rectangle2D object, Point beginNode){
		double objectX = object.getCenterX();
		double objectY = object.getCenterY();
		double beginX = beginNode.getX();
		double beginY = beginNode.getY();
		if(beginX > objectX && beginY > objectY) return new Point((float)objectX - 200, (float)objectY + 200);
		if(beginX > objectX && beginY < objectY) return new Point((float)objectX + 200, (float)objectY + 200);
		if(beginX < objectX && beginY > objectY) return new Point((float)objectX + 200, (float)objectY + 200);
		if(beginX < objectX && beginY < objectY) return new Point((float)objectX - 80, (float)objectY + 80);
		//get point around intersecting object
//		System.out.println("returned null from subPoint");
		return null;
	}
}
