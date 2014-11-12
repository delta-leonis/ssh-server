package robocup.controller.ai.movement;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import robocup.model.Point;
import robocup.model.Robot;
import robocup.model.World;

public class AStarPathPlanner {
	
	private static final int DISTANCE_TO_ROBOT = 200;
	private World world;
	private ArrayList<Rectangle2D> objects;
	private ArrayList<Point> vertices;
	private ArrayList<Point> filteredVertices;
	private ArrayList<Edge> edges;

	public AStarPathPlanner() {
		world = World.getInstance();
		objects = new ArrayList<Rectangle2D>();
		vertices = new ArrayList<Point>();
		filteredVertices = new ArrayList<Point>();
		edges = new ArrayList<Edge>();
	}
	
	private class Edge {
		private Point start;
		private Point end;
		private int cost;

		public Edge(Point start, Point end, int cost) {
			this.start = start;
			this.end = end;
			this.cost = cost;
		}
		
		public Point getStart() { return start; }
		public Point getEnd() { return end; }
		public int getCost() { return cost; }
	}
	
	public Point[] getRoute(Point beginNode, Point destination, int robotId) {
		generateObjectList(robotId);
		generateVertices();
		removeCollidingVertices();
		vertices.add(beginNode);
		vertices.add(destination);
		generateEdges();
		
		return null;
	}

	private void generateObjectList(int robotId) {
		for(Robot r : world.getAlly().getRobots())
			if(r.getRobotID() != robotId)
				objects.add(new Rectangle2D.Float(r.getPosition().getX(), r.getPosition().getY(), 399, 399));

		for(Robot r : world.getEnemy().getRobots())
			objects.add(new Rectangle2D.Float(r.getPosition().getX(), r.getPosition().getY(), 399, 399));
	}

	private void generateVertices() {
		for(Rectangle2D rect : objects) {
			int x = (int) rect.getCenterX();
			int y = (int) rect.getCenterY();
			
			vertices.add(new Point(x + DISTANCE_TO_ROBOT, y + DISTANCE_TO_ROBOT));
			vertices.add(new Point(x + DISTANCE_TO_ROBOT, y - DISTANCE_TO_ROBOT));
			vertices.add(new Point(x - DISTANCE_TO_ROBOT, y + DISTANCE_TO_ROBOT));
			vertices.add(new Point(x - DISTANCE_TO_ROBOT, y - DISTANCE_TO_ROBOT));
		}
	}

	private void removeCollidingVertices() {
		for(Rectangle2D rect : objects)
			for(Point p : vertices)
				if(rect.contains(p.getX(), p.getY()))
					filteredVertices.add(p);
		
		vertices.removeAll(filteredVertices);
	}

	private void generateEdges() {
		for(Point vertice1 : vertices) {
			for(Point vertice2 : vertices) {
				if(!vertice1.equals(vertice2)) {
					if(!intersectsObject(vertice1, vertice2)) {
						int xSquare = (int) Math.pow(vertice1.getX() - vertice2.getX(), 2);
						int ySquare = (int) Math.pow(vertice1.getY() - vertice2.getY(), 2);
						// cost of the edge is the distance, not using square root for performance
						edges.add(new Edge(vertice1, vertice2, xSquare + ySquare));
					}
				}
			}
		}
	}

	private boolean intersectsObject(Point vertice1, Point vertice2) {
		for(Rectangle2D rect : objects)
			if(rect.intersectsLine(vertice1.getX(), vertice1.getY(), vertice2.getX(), vertice2.getY()))
				return true;
		
		return false;
	}
}
