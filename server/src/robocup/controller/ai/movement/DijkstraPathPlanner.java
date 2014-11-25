package robocup.controller.ai.movement;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedList;

import robocup.model.Point;
import robocup.model.Robot;
import robocup.model.World;

public class DijkstraPathPlanner {
	
	private static final int DISTANCE_TO_ROBOT = 180;
	private World world;
	private ArrayList<Rectangle2D> objects;
	private ArrayList<Vertex> vertices;
	private ArrayList<Vertex> filteredVertices;

	/**
	 * Create the pathplanner
	 */
	public DijkstraPathPlanner() {
		reset();
	}
	
	/**
	 * Class representing a vertex in a graph
	 */
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
	        return position.equals(((Vertex) vertex).getPosition());
	    }
	    @Override
	    public String toString() {
	    	return position.toString();
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

		generateObjectList(robotId);

		// no object on route
		if(!intersectsObject(new Vertex(beginNode), new Vertex(destination))) {
			route.push(destination);
			return route;
		}
		
		// add source and dest to vertices list
		Vertex source = new Vertex(beginNode);
		source.setDist(0);
		vertices.add(source);
		Vertex dest = new Vertex(destination);
		vertices.add(dest);

		// generate vertices around robots and remove vertices colliding with robots
		generateVertices();
		removeCollidingVertices();
		
		// calculate neighbours for every vertex
		generateNeighbours();
		
		// calculate the shortest path through the graph
		Vertex u = source;
		calculatePath(source, u, dest);
		
		// add positions to the route list
		u = dest;
		while(u.getPrevious() != null) {
			route.push(u.getPosition());
			u = u.getPrevious();
		}
		
		// reset lists so we can use the same pathplanner object multiple times
		reset();
		
		return route;
	}
	
	/**
	 * Reset objects
	 */
	private void reset() {
		world = World.getInstance();
		objects = new ArrayList<Rectangle2D>();
		vertices = new ArrayList<Vertex>();
		filteredVertices = new ArrayList<Vertex>();
	}

	/**
	 * Calculate the path
	 * @param source 
	 * @param u
	 * @param dest
	 */
	private void calculatePath(Vertex source, Vertex u, Vertex dest) {
		for(int i = 0; i < 50 && vertices.size() > 0; i++) {
			if(!vertices.contains(source)) {
				u = getMinDistNeighbour(u);
			}
			
			if(u.equals(dest))
				return;
			
			vertices.remove(u);
			
			for(Vertex v : u.getNeighbours()) {
				int alt = u.getDist() + getDistance(u, v);
				
				// alternate path is shorter than the previous path to v
				if(alt < v.getDist()) {
					v.setDist(alt);
					v.setPrevious(u);
				}
			}
		}
	}

	/**
	 * Get the closest neighbour for the vertex u
	 * @param u the vertex
	 * @return closest neighbour vertex
	 */
	private Vertex getMinDistNeighbour(Vertex u) {
		int minDist = Integer.MAX_VALUE;
		Vertex closest = u;

		for(Vertex v : u.getNeighbours()) {
			int dist = getDistance(u, v);
			if(closest == null || dist < minDist && !isVertexClosed(v)) {
				closest = v;
				minDist = dist;
			}
		}
		
		return closest;
	}

	/**
	 * check if a vertex has been used already
	 * @param v the vertex
	 * @return true when v is not present in the list of open vertices
	 */
	private boolean isVertexClosed(Vertex v) {
		for(Vertex u : vertices)
			if(v.equals(u))
				return false;
		
		return true;
	}

	/**
	 * Create a rectangle around every robot so we can calculate intersections
	 * @param robotId the robot id of the robot who needs a path, no rectangle will be created for this robot
	 */
	private void generateObjectList(int robotId) {
		for(Robot r : world.getAlly().getRobots())
			if(r.getPosition() != null)
				if(r.getRobotID() != robotId)
					objects.add(new Rectangle2D.Float(
							r.getPosition().getX() - (DISTANCE_TO_ROBOT - 1),
							r.getPosition().getY() - (DISTANCE_TO_ROBOT - 1),
							DISTANCE_TO_ROBOT * 2 - 2,
							DISTANCE_TO_ROBOT * 2 - 2));

		for(Robot r : world.getEnemy().getRobots())
			if(r.getPosition() != null)
				objects.add(new Rectangle2D.Float(
						r.getPosition().getX() - (DISTANCE_TO_ROBOT - 1),
						r.getPosition().getY() - (DISTANCE_TO_ROBOT - 1),
						DISTANCE_TO_ROBOT * 2 - 2,
						DISTANCE_TO_ROBOT * 2 - 2));
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
	private int getDistance(Vertex vertex1, Vertex vertex2) {
        return (int) vertex1.getPosition().getDeltaDistance(vertex2.getPosition());
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
