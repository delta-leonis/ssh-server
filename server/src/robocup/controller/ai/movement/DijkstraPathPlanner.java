package robocup.controller.ai.movement;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedList;

import robocup.model.Point;
import robocup.model.Robot;
import robocup.model.World;

/**
 * Pathplanner class based on Dijkstra's algorithm
 * Converts model to a graph, find shortest path between two vertices using getRoute
 */
public class DijkstraPathPlanner {

	// distance from the middle of the robot to the vertices around it
	// Basically the "Danger zone" for the Robot. A normal Robot has a radius of 90mm, so if DISTANCE_TO_ROBOT == 130mm,
	// then it means we don't want to get within (180mm - 90mm = ) 90mm of any other Robot.
	public static final int DISTANCE_TO_ROBOT = 180;
	// This value is used to determine the vertex points, which are VERTEX_DISTANCE_TO_ROBOT from the middle points of the robots.
	public static final int VERTEX_DISTANCE_TO_ROBOT = 400;
	private World world;
	private ArrayList<Rectangle2D> objects;
	protected ArrayList<Vertex> vertices;
	private ArrayList<Vertex> testVertices = null;
	protected ArrayList<Vertex> notRemovableVertices; 	//Contains the source and destination.
	

	/**
	 * Constructor that creates the pathplanner object
	 * Initializes the pathplanners arraylists and world object
	 */
	public DijkstraPathPlanner() {
		world = World.getInstance();
		objects = new ArrayList<Rectangle2D>();
		vertices = new ArrayList<Vertex>();
		notRemovableVertices = new ArrayList<Vertex>();
	}

	/**
	 * Class representing a vertex in a graph
	 * Contains position, neighbours and previous vertex on the route
	 */
	public class Vertex {
		private Point position;
		private int distance;
		private ArrayList<Vertex> neighbours;
		private Vertex previous = null;
		private boolean removable = true;
		private boolean stuck = false;			// if there is no path between start and destination

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
		 * A method that converts a poin to a rectangle by using the current position as base
		 * @return
		 */
		public Rectangle2D toRect(){
			return new Rectangle2D.Double(position.getX() - DISTANCE_TO_ROBOT, position.getY() - DISTANCE_TO_ROBOT, DISTANCE_TO_ROBOT * 2, DISTANCE_TO_ROBOT * 2);
		}

		/**
		 * getter method that returns the position
		 * @return the position
		 */
		public Point getPosition() {
			return position;
		}

		/**
		 * getter method that returns the distance covered to reach this vertex
		 * @return the distance
		 */
		public int getDist() {
			return distance;
		}

		/**
		 * setter method that sets the distance
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
		 * Set the previous vertex in the shortest path to destination
		 * @param v the previous vertex
		 */
		public void setPrevious(Vertex v) {
			previous = v;
		}

		/**
		 * Get the previous vertex in the shortest path to destination
		 * @return previous
		 */
		public Vertex getPrevious() {
			return previous;
		}

		/**
		 * comparison method that compares the given vertex object to this objects current position
		 * @returns true if the position equals the given vertex
		 */
		@Override
		public boolean equals(Object vertex) {
			return position.equals(((Vertex) vertex).getPosition());
		}

		/**
		 * To string method override
		 */
		@Override
		public String toString() {
			return position.toString();
		}
		
		/**
		 * deprecated method that checks wether the given rectangle shares the same center as the vertex
		 * @param rect
		 * @return
		 */
		@Deprecated
		public boolean belongsTo(Rectangle2D rect){
			return position.getX() == rect.getCenterX() && position.getY() == rect.getCenterY();
		}
		
		/**
		 * checker method that checks if the vertex is removable
		 * If it returns that its not removable, then it means that it is probably the starting point
		 *  or the destination
		 * @return true if the vertex is removable, false otherwise
		 */
		public boolean isRemovable() {
			return removable;
		}
		
		/**
		 * Setter method that sets removable false for source and destination points.
		 * @param removable the boolean that indicates that the vertex is removable or not
		 */
		public void setRemovable(boolean removable) {
			this.removable = removable;
		}
		
		/**
		 * checker method that checks if there is a path between the starting point and destination
		 * @return
		 */
		public boolean isStuck(){
			return stuck;
		}
		
		/**
		 * setter method that declares that indicates wether or not the start and endpoints can be connected
		 * @param stuck
		 */
		public void setStuck(boolean stuck){
			this.stuck = stuck;
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
	@SuppressWarnings("unchecked")
	public LinkedList<Point> getRoute(Point beginNode, Point destination, int robotId, boolean testMode) {
		LinkedList<Point> route = new LinkedList<Point>();
		boolean found = false;

		generateObjectList(robotId);

		// no object on route
		if (!intersectsObject(new Vertex(beginNode), new Vertex(destination))) {
			route.push(destination);
			found = true;
			if(!testMode){
				reset();
				return route;
			}
		}

		// generate vertices around robots and remove vertices colliding with
		// robots
		generateVertices();
		removeCollidingVertices();
		
		// add source and dest to vertices list
		Vertex source = setupSource(beginNode);
		if(source == null){
			System.out.println("Locked in source");
			if(testMode)
				testVertices = (ArrayList<Vertex>)vertices.clone();
			return null;					//Locked in
		}
		Vertex dest = setupDestination(destination);
		if(dest == null){
			System.out.println("Locked in destination");
			if(testMode)
				testVertices = (ArrayList<Vertex>)vertices.clone();
			return null;					//Locked in
		}
		
		// calculate neighbours for every vertex
		generateNeighbours();
		
		if(testMode)
			testVertices = (ArrayList<Vertex>)vertices.clone();
		
		if(!found){
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
		}
		return route;
	}
	
	/**
	 * Sets up the source vertex, which has a few special properties.
	 * - A source Vertex may not be removed.
	 * - Its first point in its path is always one of its own generated vertices.
	 * - If these vertices all got removed, then the source is locked in.
	 * @param source The starting point of the robot.
	 */
	protected Vertex setupSource(Point beginNode){
		boolean lockedIn = true; //Locked in until proven otherwise
		Vertex source = new Vertex(beginNode);
		source.setDist(0);
		source.setRemovable(false);
		vertices.add(source);
		
		if(isInsideObject(source.toRect())) {
			source.setStuck(true);
			int x = (int)beginNode.getX();
			int y = (int)beginNode.getY();
			// North east
			Vertex neighbour = new Vertex(new Point(x + VERTEX_DISTANCE_TO_ROBOT, y + VERTEX_DISTANCE_TO_ROBOT));
			if(isValidPosition(source, neighbour)) {
				lockedIn = false;
				vertices.add(neighbour);
				source.addNeighbour(neighbour);
			}
			// South east
			neighbour = new Vertex(new Point(x + VERTEX_DISTANCE_TO_ROBOT, y - VERTEX_DISTANCE_TO_ROBOT));
			if(isValidPosition(source, neighbour)){
				lockedIn = false;
				vertices.add(neighbour);
				source.addNeighbour(neighbour);
			}
			// North west
			neighbour = new Vertex(new Point(x - VERTEX_DISTANCE_TO_ROBOT, y + VERTEX_DISTANCE_TO_ROBOT));
			if(isValidPosition(source, neighbour)){
				lockedIn = false;
				vertices.add(neighbour);
				source.addNeighbour(neighbour);
			}
			// South west
			neighbour = new Vertex(new Point(x - VERTEX_DISTANCE_TO_ROBOT, y - VERTEX_DISTANCE_TO_ROBOT));
			if(isValidPosition(source, neighbour)){
				lockedIn = false;
				vertices.add(neighbour);
				source.addNeighbour(neighbour);
			}
			
			// Stand still if we're locked in.
			if(lockedIn)
				return null;
			
			removeAllVectorsInRect(new Rectangle2D.Double(x - VERTEX_DISTANCE_TO_ROBOT + 1, y - VERTEX_DISTANCE_TO_ROBOT + 1, VERTEX_DISTANCE_TO_ROBOT * 2 - 2, VERTEX_DISTANCE_TO_ROBOT * 2 - 2));
		}
		
		return source;
	}
	
	/**
	 * Sets up the destination node and everything associated with it.
	 * @param endNode A {@link Point} that specifies the position of where you want to go.
	 * @return The endNode in Vertex form.
	 */
	protected Vertex setupDestination(Point endNode){
		boolean lockedIn = true;
		Vertex destination = new Vertex(endNode);
		destination.setRemovable(false);
		vertices.add(destination);
		
		//TODO: Create new destination if we collide with something

		if(isInsideObject(destination.toRect())){
			destination.setStuck(true);
			int x = (int)endNode.getX();
			int y = (int)endNode.getY();
			
			// North east
			Vertex neighbour = new Vertex(new Point(x + VERTEX_DISTANCE_TO_ROBOT, y + VERTEX_DISTANCE_TO_ROBOT));
			if(isValidPosition(destination, neighbour)){
				lockedIn = false;
				vertices.add(neighbour);
				neighbour.addNeighbour(destination);
			}
			// South east
			neighbour = new Vertex(new Point(x + VERTEX_DISTANCE_TO_ROBOT, y - VERTEX_DISTANCE_TO_ROBOT));
			if(isValidPosition(destination, neighbour)){
				lockedIn = false;
				vertices.add(neighbour);
				neighbour.addNeighbour(destination);
			}
			// North west		
			neighbour = new Vertex(new Point(x - VERTEX_DISTANCE_TO_ROBOT, y + VERTEX_DISTANCE_TO_ROBOT));
			if(isValidPosition(destination, neighbour)){
				lockedIn = false;
				vertices.add(neighbour);
				neighbour.addNeighbour(destination);
			}
			// South west
			neighbour =new Vertex(new Point(x - VERTEX_DISTANCE_TO_ROBOT, y - VERTEX_DISTANCE_TO_ROBOT));
			if(isValidPosition(destination, neighbour)){
				lockedIn = false;
				vertices.add(neighbour);
				neighbour.addNeighbour(destination);
			}

			if(lockedIn)
				return null;
			
			removeAllVectorsInRect(new Rectangle2D.Double(x - VERTEX_DISTANCE_TO_ROBOT + 1, y - VERTEX_DISTANCE_TO_ROBOT + 1, VERTEX_DISTANCE_TO_ROBOT * 2 -2, VERTEX_DISTANCE_TO_ROBOT * 2 -2));
		}
		return destination;
	}

	/**
	 * Reset objects
	 */
	public void reset() {
		objects.clear();
		vertices.clear();
	}
	
	/**
	 * checker method that returns if the robot is not placed in the danger zone of another robot
	 * @param source the original position vertex
	 * @param destination the destination position vertex
	 * @return
	 */
	protected boolean isValidPosition(Vertex source, Vertex destination){
		for(Rectangle2D rect : objects){
			if(rect.intersects(source.toRect())) {
				// check whether it's in between source and destination.
				Rectangle2D smallerRect = new Rectangle2D.Double(rect.getX() + 40, rect.getY() + 40, 180, 180);
				if(smallerRect.intersectsLine(source.getPosition().getX(), source.getPosition().getY(), destination.getPosition()
						.getX(), destination.getPosition().getY())) {
					return false;
				}
			}
			if(rect.contains(destination.getPosition().toPoint2D())){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * a function that removes all vectors in the given rectangle
	 * @param rect
	 */
	protected void removeAllVectorsInRect(Rectangle2D rect){
		ArrayList<Vertex> removeVertices = new ArrayList<Vertex>();
		for(Vertex vertex : vertices){
			if(vertex.isRemovable() && rect.contains(vertex.getPosition().toPoint2D())){
				removeVertices.add(vertex);
			}
		}
		vertices.removeAll(removeVertices);
	}

	/**
	 * Calculate the path
	 * @param source first vertex on the path
	 * @param u helper vertex, contains the vertex closest to the previously evaluated vertex
	 * @param dest destination
	 */
	private void calculatePath(Vertex source, Vertex dest) {
		Vertex u = source;
		while(vertices.size() > 0) {
			if (!vertices.contains(source)) {
				// get closest neighbour
				u = getMinDistNeighbour(u);
				if(u == null) {
					u = vertices.get(0);
				}
			}
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
	 * @param u the vertex
	 * @return closest neighbour vertex
	 */
	private Vertex getMinDistNeighbour(Vertex u) {
		int minDist = Integer.MAX_VALUE;
		Vertex closest = null;

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
	 * 
	 * WARNING: documentation for Rectangle2D.Double states the upper left corner should be specified,
     * use lower left corner instead
     * 
	 * @param robotId the robot id of the robot who needs a path, no rectangle will be created for this robot
	 */
	protected void generateObjectList(int robotId) {
		objects.clear();
		for (Robot r : world.getReferee().getAlly().getRobotsOnSight())
			if (r.getPosition() != null)
				if (r.getRobotId() != robotId)
					objects.add(new Rectangle2D.Double(r.getPosition().getX() - DISTANCE_TO_ROBOT, r.getPosition()
							.getY() - DISTANCE_TO_ROBOT, DISTANCE_TO_ROBOT*2, DISTANCE_TO_ROBOT*2));

		for (Robot r : world.getReferee().getEnemy().getRobotsOnSight())
			if (r.getPosition() != null)
				objects.add(new Rectangle2D.Double(r.getPosition().getX() - DISTANCE_TO_ROBOT, r.getPosition()
						.getY() - DISTANCE_TO_ROBOT, DISTANCE_TO_ROBOT * 2, DISTANCE_TO_ROBOT * 2));
	}

	/**
	 * Generate vertices around every robot in the robot list
	 * only add vertices when it is not the source destination
	 */
	protected void generateVertices() {
		vertices.clear();
		for (Rectangle2D rect : getObjects()) {
			int x = (int) rect.getCenterX();
			int y = (int) rect.getCenterY();

			if(!isObjectNotRemovable(x,y)){	//Avoid double vertices from pre-generated vertices in source and dest.
				vertices.add(new Vertex(new Point(x + VERTEX_DISTANCE_TO_ROBOT, y + VERTEX_DISTANCE_TO_ROBOT)));
				vertices.add(new Vertex(new Point(x + VERTEX_DISTANCE_TO_ROBOT, y - VERTEX_DISTANCE_TO_ROBOT)));
				vertices.add(new Vertex(new Point(x - VERTEX_DISTANCE_TO_ROBOT, y + VERTEX_DISTANCE_TO_ROBOT)));
				vertices.add(new Vertex(new Point(x - VERTEX_DISTANCE_TO_ROBOT, y - VERTEX_DISTANCE_TO_ROBOT)));
			}
		}
	}
	
	/**
	 * checker method that checks if the rectangle around the given point belongs to a non removable vertex
	 * @param x center x position of the rectangle
	 * @param y center y position of the rectanlge
	 * @return
	 */
	protected boolean isObjectNotRemovable(int x, int y) {
		for(Vertex vertex : notRemovableVertices){
			if((int)vertex.getPosition().getX() == x || (int)vertex.getPosition().getY() == y){
				return true;
			}
		}
		return false;
	}

	/**
	 * Remove all vertices located inside rectangles
	 */
	protected void removeCollidingVertices() {
		ArrayList<Vertex> filteredVertices = new ArrayList<Vertex>();
		for (Rectangle2D rect : getObjects())
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
	 * @param source
	 * @param destination
	 * @return true when an object is found between the vertices
	 */
	protected boolean intersectsObject(Vertex source, Vertex destination) {
		for (Rectangle2D rect : objects)
			if (rect.intersectsLine(source.getPosition().getX(), source.getPosition().getY(), destination.getPosition()
					.getX(), destination.getPosition().getY()))
				return true;
		
		return false;
	}
	
	/**
	 * checker method that checks if the given point is within a object
	 * @param p point
	 * @return true if point is in an object
	 */
	protected boolean isInsideObject(Rectangle2D r) {
		for(Rectangle2D rect : objects) {
			if(rect.intersects(r))
				return true;
		}
		return false;
	}

	/**
	 * method that returns a copy of the vertices
	 * @return
	 */
	public ArrayList<Vertex> getTestVertices() {
		return testVertices;
	}

	/**
	 * getter functions that returns all current objects
	 * @return all objects in the arraylist objects
	 */
	public ArrayList<Rectangle2D> getObjects() {
		return objects;
	}
}
