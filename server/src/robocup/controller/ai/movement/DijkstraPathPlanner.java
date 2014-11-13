package robocup.controller.ai.movement;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedList;

import robocup.model.Point;
import robocup.model.Robot;
import robocup.model.World;

public class DijkstraPathPlanner {
	
	private static final int DISTANCE_TO_ROBOT = 200;
	private World world;
	private ArrayList<Rectangle2D> objects;
	private ArrayList<Vertex> vertices;
	private ArrayList<Vertex> filteredVertices;

	/**
	 * Create the pathplanner
	 */
	public DijkstraPathPlanner() {
		world = World.getInstance();
		objects = new ArrayList<Rectangle2D>();
		vertices = new ArrayList<Vertex>();
		filteredVertices = new ArrayList<Vertex>();
	}
	
	private class Vertex {
		private Point position;
		private int distance;
		private ArrayList<Vertex> neighbours;
		private Vertex previous = null;

		public Vertex(Point position) {
			this.position = position;
			distance = Integer.MAX_VALUE;
			neighbours = new ArrayList<Vertex>();
		}
		
		public Point getPosition() { return position; }
		public int getDist() { return distance; }
		public void setDist(int dist) { distance = dist; }
		public void addNeighbour(Vertex v) { neighbours.add(v); }
		public ArrayList<Vertex> getNeighbours() { return neighbours; }
		public void setPrevious(Vertex v) { previous = v; }
		public Vertex getPrevious() { return previous; }
	    @Override
	    public boolean equals(Object vertex) {
	        return ((Vertex)vertex).getPosition().getX() == position.getX() && ((Vertex)vertex).getPosition().getY() == position.getY();
	    }
	}
	
	/**
	 * Get the shortest route from a begin point to a destination for one specific robot
	 * @param beginNode starting point for the robot
	 * @param destination destination of the robot
	 * @param robotId robot id of the robot
	 * @return list with points forming the shortest route
	 */
	public LinkedList<Point> getRoute(Point beginNode, Point destination, int robotId) {
		LinkedList<Point> route = new LinkedList<Point>();
		
		// no object on route
		if(!intersectsObject(new Vertex(beginNode), new Vertex(destination))) {
			route.push(destination);
			return route;
		}

		generateObjectList(robotId);
		generateVertices();
		removeCollidingVertices();
		
		Vertex source = new Vertex(beginNode);
		source.setDist(0);
		vertices.add(source);
		
		Vertex dest = new Vertex(destination);
		vertices.add(dest);
		
		generateNeighbours();
		
		Vertex u = source;
		
		while(vertices.size() > 0) {
			if(!vertices.contains(source))
				u = getMinDistNeighbour(u);
			
			vertices.remove(u);
			
			for(Vertex v : u.getNeighbours()) {
				int alt = u.getDist() + getSquareDistance(u, v);
				if(alt < v.getDist()) {
					v.setDist(alt);
					v.setPrevious(u);
				}
			}
		}
		
		u = dest;
		while(u.getPrevious() != null) {
			route.push(u.getPosition());
		}
		
		return route;
	}

	/**
	 * Get the closest neighbour for the vertex u
	 * @param u the vertex
	 * @return closest neighbour vertex
	 */
	private Vertex getMinDistNeighbour(Vertex u) {
		int minDist = Integer.MAX_VALUE;
		Vertex closest = null;

		for(Vertex v : u.getNeighbours()) {
			int dist = getSquareDistance(u, v);
			if(closest == null || dist < minDist) {
				closest = v;
				minDist = dist;
			}
		}
		
		return closest;
	}

	/**
	 * Create a rectangle around every robot so we can calculate intersections
	 * @param robotId the robot id of the robot who needs a path, no rectangle will be created for this robot
	 */
	private void generateObjectList(int robotId) {
		for(Robot r : world.getAlly().getRobots())
			if(r.getRobotID() != robotId)
				objects.add(new Rectangle2D.Float(r.getPosition().getX(), r.getPosition().getY(), 399, 399));

		for(Robot r : world.getEnemy().getRobots())
			objects.add(new Rectangle2D.Float(r.getPosition().getX(), r.getPosition().getY(), 399, 399));
	}

	/**
	 * Generate vertices around every robot in the robot list
	 */
	private void generateVertices() {
		for(Rectangle2D rect : objects) {
			int x = (int) rect.getCenterX();
			int y = (int) rect.getCenterY();

			vertices.add(new Vertex(new Point(x + DISTANCE_TO_ROBOT, y + DISTANCE_TO_ROBOT)));
			vertices.add(new Vertex(new Point(x + DISTANCE_TO_ROBOT, y - DISTANCE_TO_ROBOT)));
			vertices.add(new Vertex(new Point(x - DISTANCE_TO_ROBOT, y + DISTANCE_TO_ROBOT)));
			vertices.add(new Vertex(new Point(x - DISTANCE_TO_ROBOT, y - DISTANCE_TO_ROBOT)));
		}
	}

	/**
	 * Remove all vertices located inside rectangles
	 */
	private void removeCollidingVertices() {
		for(Rectangle2D rect : objects)
			for(Vertex v : vertices)
				if(rect.contains(v.getPosition().getX(), v.getPosition().getY()))
					filteredVertices.add(v);
		
		vertices.removeAll(filteredVertices);
	}

	/**
	 * Calculate which vertices can be reached for every vertex
	 */
	private void generateNeighbours() {
		for(Vertex vertex1 : vertices)
			for(Vertex vertex2 : vertices)
				if(!vertex1.equals(vertex2))
					if(!intersectsObject(vertex1, vertex2))
						vertex1.addNeighbour(vertex2);
	}

	/**
	 * Get the square distance between 2 vertices
	 * @param vertex1
	 * @param vertex2
	 * @return square distance
	 */
	private int getSquareDistance(Vertex vertex1, Vertex vertex2) {
        int xSquare = (int) Math.pow(vertex1.getPosition().getX() - vertex2.getPosition().getX(), 2);
        int ySquare = (int) Math.pow(vertex1.getPosition().getY() - vertex2.getPosition().getY(), 2);
        return xSquare + ySquare;
	}

	/**
	 * calculate if there's an object between two vertices
	 * @param vertex1
	 * @param vertex2
	 * @return true when an object is found between the vertices
	 */
	private boolean intersectsObject(Vertex vertex1, Vertex vertex2) {
		for(Rectangle2D rect : objects)
			if(rect.intersectsLine(vertex1.getPosition().getX(), vertex1.getPosition().getY(), 
					vertex2.getPosition().getX(), vertex2.getPosition().getY()))
				return true;
		
		return false;
	}
}
