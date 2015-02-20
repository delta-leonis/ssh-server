package robocup.controller.ai.movement;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedList;

import robocup.model.Point;
import robocup.model.Robot;
import robocup.model.World;

/**
 * Pathplanner class based on Dijkstra's algorithm
 * Converts model to a graph, find shortest path between two vertices using getRoute
 * TODO: If source is removed because of a nearby Robot, move away from said Robot and recalculate path.
 */
public class DijkstraPathPlanner {

	//TODO: Use only one value?
	// distance from the middle of the robot to the vertices around it
	// Basically the "Danger zone" for the Robot. A normal Robot has a radius of 90mm, so if DISTANCE_TO_ROBOT == 130mm,
	// then it means we don't want to get within (130mm - 90mm = ) 40mm of any other Robot.
	protected static final int DISTANCE_TO_ROBOT = 180;
	// This value is used to determine the vertex points, which are VERTEX_DISTANCE_TO_ROBOT from the middle points of the robots.
	protected static final int VERTEX_DISTANCE_TO_ROBOT = 400;
	private World world;
	protected ArrayList<Rectangle2D> objects;
	protected ArrayList<Vertex> vertices;
	protected ArrayList<Vertex> testVertices = null;
//	private ArrayList<Vertex> filteredVertices;		//Moved to the function that uses it. #removeCollidingVertices() 

	/**
	 * Create the pathplanner
	 */
	public DijkstraPathPlanner() {
		world = World.getInstance();
		objects = new ArrayList<Rectangle2D>();
		vertices = new ArrayList<Vertex>();
//		filteredVertices = new ArrayList<Vertex>();
	}

	/**
	 * Class representing a vertex in a graph
	 * Contains position, neighbours and previous vertex on the route
	 */
	protected class Vertex {
		private Point position;
		private int distance;
		private ArrayList<Vertex> neighbours;
		private Vertex previous = null;
		private boolean removable = true;

		/**
		 * Create a vertex which contains the position, 
		 * all neighbours for the vertex and the previous vertex in the shortest path
		 * @param position
		 */
		public Vertex(Point position) {
			this.position = position;
			distance = Integer.MAX_VALUE;
			neighbours = new ArrayList<Vertex>();
		}

		/**
		 * Get the position
		 * @return the position
		 */
		public Point getPosition() {
			return position;
		}

		/**
		 * Get the distance covered to reach this vertex
		 * @return the distance
		 */
		public int getDist() {
			return distance;
		}

		/**
		 * Set the distance
		 * @param dist the distance to set
		 */
		public void setDist(int dist) {
			distance = dist;
		}

		/**
		 * Add a neighbour for this vertex
		 * @param v the vertex to add
		 */
		public void addNeighbour(Vertex v) {
			neighbours.add(v);
		}

		/**
		 * Get all neighbours
		 * @return ArrayList containing all neighbours
		 */
		public ArrayList<Vertex> getNeighbours() {
			return neighbours;
		}

		/**
		 * Set the previous vertex in the shortest path to dest
		 * @param v the previous vertex
		 */
		public void setPrevious(Vertex v) {
			previous = v;
		}

		/**
		 * Get the previous vertex in the shortest path to dest
		 * @return previous
		 */
		public Vertex getPrevious() {
			return previous;
		}

		@Override
		public boolean equals(Object vertex) {
			return position.equals(((Vertex) vertex).getPosition());
		}

		@Override
		public String toString() {
			return position.toString();
		}
		
		public boolean belongsTo(Rectangle2D rect){
			return position.getX() == rect.getCenterX() && position.getY() == rect.getCenterY();
		}
		/**
		 * @return true if the vertex is removable, false otherwise
		 */
		public boolean isRemovable() {
			return removable;
		}
		/**
		 * Set false for source and destination points.
		 */
		public void setRemovable(boolean removable) {
			this.removable = removable;
		}
	}

	/**
	 * Get the shortest route from a begin point to a destination for one specific robot
	 * @param beginNode starting point for the robot
	 * @param destination destination of the robot
	 * @param robotId robot id of the robot
	 * @param manualReset true if you're testing the function.
	 * @return list with points forming the shortest route
	 */
	public LinkedList<Point> getRoute(Point beginNode, Point destination, int robotId, boolean testMode) {
		LinkedList<Point> route = new LinkedList<Point>();

		generateObjectList(robotId);

		// no object on route
		if (!intersectsObject(new Vertex(beginNode), new Vertex(destination))) {
			reset();
			route.push(destination);
			return route;
		}

		// generate vertices around robots and remove vertices colliding with
		// robots
		generateVertices();
		removeCollidingVertices();
		
		// add source and dest to vertices list
		Vertex source = new Vertex(beginNode);
		source.setDist(0);
		source.setRemovable(false);
		vertices.add(source);
		
		Vertex dest = new Vertex(destination);
		dest.setRemovable(false);
		vertices.add(dest);
		
		if(!vertices.contains(source)){
			// Create a point to go to first if the source has been removed.
			// (This means we're too close to another Robot)
		}
		
		if(!vertices.contains(dest)){
			// Create a new destination
		}
		
		// calculate neighbours for every vertex
		generateNeighbours();
		
		if(testMode)
			testVertices = (ArrayList<Vertex>)vertices.clone();
		
		// calculate the shortest path through the graph
		calculatePath(source, dest);

		// add positions to the route list
		Vertex u = dest;
//		for(int i = 0; i < 50 && u.getPrevious() != null; i++) {
		while (u.getPrevious() != null) {
			route.push(u.getPosition());
			u = u.getPrevious();
		}

		// reset lists so we can use the same pathplanner object multiple times
		if(!testMode){
			reset();
		}
		return route;
	}

	/**
	 * Reset objects
	 */
	protected void reset() {
		objects.clear();
		vertices.clear();
//		filteredVertices.clear();
	}

	/**
	 * Calculate the path
	 * @param source first vertex on the path
	 * @param u helper vertex, contains the vertex closest to the previously evaluated vertex
	 * @param dest destination
	 */
	private void calculatePath(Vertex source, Vertex dest) {
		Vertex u = source;
		for (int i = 0; i < 50 && vertices.size() > 0; i++) {

			if (!vertices.contains(source)) {
				// get closest neighbour
				u = getMinDistNeighbour(u);
			}
			
			// If we're at the destination, we're done.
			if (u.equals(dest))
				return;
			
			vertices.remove(u);

			// calculate new costs for neighbours
			for (Vertex v : u.getNeighbours()) {
				int alt = u.getDist() + getDistance(u, v);

				// alternate path is shorter than the previous path to v
				if (alt < v.getDist()) {
					v.setDist(alt);
					v.setPrevious(u);
				}
			}
		}
	}
	

	/**
	 * Get the closest neighbour for the vertex u
	 * TODO: If the vertex is "locked in", infinite loops will ensue
	 * @param u the vertex
	 * @return closest neighbour vertex
	 */
	private Vertex getMinDistNeighbour(Vertex u) {
		int minDist = Integer.MAX_VALUE;
		Vertex closest = u;

		for (Vertex v : u.getNeighbours()) {
			int dist = getDistance(u, v);
			if (dist < minDist && !isVertexClosed(v)) {
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
		for (Vertex u : vertices)
			if (v.equals(u))
				return false;

		return true;
	}

	/**
	 * Create a rectangle around every robot so we can calculate intersections
	 * @param robotId the robot id of the robot who needs a path, no rectangle will be created for this robot
	 * TODO: getRobotsOnSight()
	 */
	protected void generateObjectList(int robotId) {
		// WARNING: documentation for Rectangle2D.Double states the upper left corner should be specified,
		// use lower left corner instead
		for (Robot r : world.getAlly().getRobotsOnSight())
			if (r.getPosition() != null)
				if (r.getRobotId() != robotId)
					objects.add(new Rectangle2D.Double(r.getPosition().getX() - DISTANCE_TO_ROBOT, r.getPosition()
							.getY() - DISTANCE_TO_ROBOT, DISTANCE_TO_ROBOT*2, DISTANCE_TO_ROBOT*2));

		for (Robot r : world.getEnemy().getRobotsOnSight())
			if (r.getPosition() != null)
				objects.add(new Rectangle2D.Double(r.getPosition().getX() - DISTANCE_TO_ROBOT, r.getPosition()
						.getY() - DISTANCE_TO_ROBOT, DISTANCE_TO_ROBOT*2, DISTANCE_TO_ROBOT*2));
	}

	/**
	 * Generate vertices around every robot in the robot list
	 */
	protected void generateVertices() {
		for (Rectangle2D rect : objects) {
			int x = (int) rect.getCenterX();
			int y = (int) rect.getCenterY();

			vertices.add(new Vertex(new Point(x + VERTEX_DISTANCE_TO_ROBOT, y + VERTEX_DISTANCE_TO_ROBOT)));
			vertices.add(new Vertex(new Point(x + VERTEX_DISTANCE_TO_ROBOT, y - VERTEX_DISTANCE_TO_ROBOT)));
			vertices.add(new Vertex(new Point(x - VERTEX_DISTANCE_TO_ROBOT, y + VERTEX_DISTANCE_TO_ROBOT)));
			vertices.add(new Vertex(new Point(x - VERTEX_DISTANCE_TO_ROBOT, y - VERTEX_DISTANCE_TO_ROBOT)));
		}
	}

	/**
	 * Remove all vertices located inside rectangles
	 */
	protected void removeCollidingVertices() {
		ArrayList<Vertex> filteredVertices = new ArrayList<Vertex>();
		for (Rectangle2D rect : objects)
			for (Vertex v : vertices)
				if (rect.contains(v.getPosition().getX(), v.getPosition().getY()) && v.isRemovable())
					filteredVertices.add(v);

		vertices.removeAll(filteredVertices);
	}

	/**
	 * Calculate which vertices can be reached for every vertex
	 */
	protected void generateNeighbours() {
		for (Vertex vertex1 : vertices)
			for (Vertex vertex2 : vertices)
				if (!vertex1.equals(vertex2))
					if (!intersectsObject(vertex1, vertex2))
						vertex1.addNeighbour(vertex2);
	}

	/**
	 * Get the distance between 2 vertices
	 * @param vertex1
	 * @param vertex2
	 * @return distance
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
	protected boolean intersectsObject(Vertex vertex1, Vertex vertex2) {
//		for (Rectangle2D rect : objects)
//			if (rect.intersectsLine(vertex1.getPosition().getX(), vertex1.getPosition().getY(), vertex2.getPosition()
//					.getX(), vertex2.getPosition().getY()))
//				return true;
//
//		return false;
		for (Rectangle2D rect : objects){
			if(!vertex1.isRemovable() && rect.contains(new Point2D.Double(vertex1.getPosition().getX(),
																	vertex1.getPosition().getY()))){	//Don't count source and dest as collision.
//				rect.
				continue;
			}
			if(!vertex2.isRemovable() && rect.contains(new Point2D.Double(vertex2.getPosition().getX(),
																			vertex2.getPosition().getY()))){	//Don't count source and dest as collision.
				continue;
			}
			if (rect.intersectsLine(vertex1.getPosition().getX(), vertex1.getPosition().getY(), 
					vertex2.getPosition().getX(), vertex2.getPosition().getY())){
				return true;
			}
		}
		return false;
	}
	
	public boolean intersectsNotRemovableObject(Vertex vertex1, Vertex vertex2, Rectangle2D rect){
		boolean result = false;
		//Split rectangle in 4
		Rectangle2D[] rectangleSplit = new Rectangle2D[4];
		// north west
		
		
		return result;
	}
}
