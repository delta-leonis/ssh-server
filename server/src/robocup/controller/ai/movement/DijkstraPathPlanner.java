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
	 * Create the pathplanner
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
		private boolean stuck = false;	// if start or dest is stuck

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
		
		public Rectangle2D toRect(){
			return new Rectangle2D.Double(position.getX() - DISTANCE_TO_ROBOT, position.getY() - DISTANCE_TO_ROBOT, DISTANCE_TO_ROBOT * 2, DISTANCE_TO_ROBOT * 2);
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
		
		public boolean isStuck(){
			return stuck;
		}
		
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
				setTestVertices((ArrayList<Vertex>)vertices.clone());
			return null;					//Locked in
		}
		Vertex dest = setupDestination(destination);
		if(dest == null){
			System.out.println("Locked in destination");
			if(testMode)
				setTestVertices((ArrayList<Vertex>)vertices.clone());
			return null;					//Locked in
		}
		
		// calculate neighbours for every vertex
		generateNeighbours();
		
		if(testMode)
			setTestVertices((ArrayList<Vertex>)vertices.clone());
		
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
		boolean lockedIn = true; 	//Locked in until proven otherwise
		Vertex source = new Vertex(beginNode);
		source.setDist(0);
		source.setRemovable(false);
		vertices.add(source);
		
		if(isInsideObject(beginNode.toPoint2D())){
			source.setStuck(true);
			int x = (int)beginNode.getX();
			int y = (int)beginNode.getY();
			// North east
			Vertex neighbour = new Vertex(new Point(x + VERTEX_DISTANCE_TO_ROBOT, y + VERTEX_DISTANCE_TO_ROBOT));
			if(isValidPosition(source, neighbour)){
				lockedIn = false;
				vertices.add(neighbour);
				source.addNeighbour(neighbour);
			}
//			// North
//			neighbour = new Vertex(new Point(x, y + VERTEX_DISTANCE_TO_ROBOT));
//			if(isValidPosition(source, neighbour)){
//				vertices.add(neighbour);
//				source.addNeighbour(neighbour);
//			}
			// South east
			neighbour = new Vertex(new Point(x + VERTEX_DISTANCE_TO_ROBOT, y - VERTEX_DISTANCE_TO_ROBOT));
			if(isValidPosition(source, neighbour)){
				lockedIn = false;
				vertices.add(neighbour);
				source.addNeighbour(neighbour);
			}
//			// South
//			neighbour = new Vertex(new Point(x, y - VERTEX_DISTANCE_TO_ROBOT));
//			if(isValidPosition(source, neighbour)){
//				vertices.add(neighbour);
//				source.addNeighbour(neighbour);
//			}
//			// Middle
//			neighbour = new Vertex(new Point(x, y));
//			if(isValidPosition(source, neighbour)){
//				vertices.add(neighbour);
//				source.addNeighbour(neighbour);
//			}
			// North west
			neighbour = new Vertex(new Point(x - VERTEX_DISTANCE_TO_ROBOT, y + VERTEX_DISTANCE_TO_ROBOT));
			if(isValidPosition(source, neighbour)){
				lockedIn = false;
				vertices.add(neighbour);
				source.addNeighbour(neighbour);
			}
//			// East
//			neighbour = new Vertex(new Point(x + VERTEX_DISTANCE_TO_ROBOT, y));
//			if(isValidPosition(source, neighbour)){
//				vertices.add(neighbour);
//				source.addNeighbour(neighbour);
//			}
			// South west
			neighbour = new Vertex(new Point(x - VERTEX_DISTANCE_TO_ROBOT, y - VERTEX_DISTANCE_TO_ROBOT));
			if(isValidPosition(source, neighbour)){
				lockedIn = false;
				vertices.add(neighbour);
				source.addNeighbour(neighbour);
			}
//			// West
//			neighbour = new Vertex(new Point(x - VERTEX_DISTANCE_TO_ROBOT, y));
//			if(isValidPosition(source, neighbour)){
//				vertices.add(neighbour);
//				source.addNeighbour(neighbour);
//			}
			
			// Stand still if we're locked in.
			if(lockedIn)
				return null;
			
			removeAllVectorsInRect(new Rectangle2D.Double(x - VERTEX_DISTANCE_TO_ROBOT + 1, y - VERTEX_DISTANCE_TO_ROBOT + 1, VERTEX_DISTANCE_TO_ROBOT*2 - 2, VERTEX_DISTANCE_TO_ROBOT*2 - 2));
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

		if(isInsideObject(endNode.toPoint2D())){
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
//			// South
//			neighbour = new Vertex(new Point(x, y - VERTEX_DISTANCE_TO_ROBOT));
//			if(isValidPosition(destination, neighbour)){
//				vertices.add(neighbour);
//				neighbour.addNeighbour(destination);
//			}
//			// Middle
//			neighbour = new Vertex(new Point(x, y));
//			if(isValidPosition(destination, neighbour)){
//				vertices.add(neighbour);
//				neighbour.addNeighbour(destination);
//			}
			// South east
			neighbour = new Vertex(new Point(x + VERTEX_DISTANCE_TO_ROBOT, y - VERTEX_DISTANCE_TO_ROBOT));
			if(isValidPosition(destination, neighbour)){
				lockedIn = false;
				vertices.add(neighbour);
				neighbour.addNeighbour(destination);
			}
//			// North
//			neighbour = new Vertex(new Point(x, y + VERTEX_DISTANCE_TO_ROBOT));
//			if(isValidPosition(destination, neighbour)){
//				vertices.add(neighbour);
//				neighbour.addNeighbour(destination);
//			}
			// North west		
			neighbour = new Vertex(new Point(x - VERTEX_DISTANCE_TO_ROBOT, y + VERTEX_DISTANCE_TO_ROBOT));
			if(isValidPosition(destination, neighbour)){
				lockedIn = false;
				vertices.add(neighbour);
				neighbour.addNeighbour(destination);
			}
//			// East
//			neighbour = new Vertex(new Point(x + VERTEX_DISTANCE_TO_ROBOT, y));
//			if(isValidPosition(destination, neighbour)){
//				vertices.add(neighbour);
//				neighbour.addNeighbour(destination);
//			}
			// South west
			neighbour =new Vertex(new Point(x - VERTEX_DISTANCE_TO_ROBOT, y - VERTEX_DISTANCE_TO_ROBOT));
			if(isValidPosition(destination, neighbour)){
				lockedIn = false;
				vertices.add(neighbour);
				neighbour.addNeighbour(destination);
			}
//			// West
//			neighbour = new Vertex(new Point(x - VERTEX_DISTANCE_TO_ROBOT, y));
//			if(isValidPosition(destination, neighbour)){
//				vertices.add(neighbour);
//				neighbour.addNeighbour(destination);
//			}
			if(lockedIn)
				return null;
			
			removeAllVectorsInRect(new Rectangle2D.Double(x - VERTEX_DISTANCE_TO_ROBOT + 1, y - VERTEX_DISTANCE_TO_ROBOT+1, VERTEX_DISTANCE_TO_ROBOT*2 -2, VERTEX_DISTANCE_TO_ROBOT*2 -2));
		}
		return destination;
	}

	/**
	 * Reset objects
	 */
	public void reset() {
		objects.clear();
		vertices.clear();
//		testVertices.clear();
//		filteredVertices.clear();
	}
	
	protected boolean isValidPosition(Vertex source, Vertex destination){
		for(Rectangle2D rect : objects){
			if(rect.intersects(source.toRect())){
				// check whether it's in between source and destination.
				Rectangle2D smallerRect = new Rectangle2D.Double(rect.getX() + 40, rect.getY() + 40, 180, 180);
				if(smallerRect.intersectsLine(source.getPosition().getX(), source.getPosition().getY(), destination.getPosition()
						.getX(), destination.getPosition().getY())){
					return false;
				}
			}
			if(rect.contains(destination.getPosition().toPoint2D())){
				return false;
			}
		}
		return true;
	}
	
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
//		for (int i = 0; /*i < 50 && */vertices.size() > 0; i++) {
		while(vertices.size() > 0){
			if (!vertices.contains(source)) {
				// get closest neighbour
				u = getMinDistNeighbour(u);
				if(u == null){
					u = vertices.get(0);
				}
			}
			
//			System.out.println("Size: " + vertices.size());
//			System.out.println("U: " + i + " : " + u);
			// If we're at the destination, we're done.
//			if (u.equals(dest)){
//				System.out.println("Destination: " + u + " Previous: " + u.getPrevious());
//				return;
//			}
//			
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
//			System.out.println();
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
	 * @param robotId the robot id of the robot who needs a path, no rectangle will be created for this robot
	 * TODO: getRobotsOnSight()
	 */
	protected void generateObjectList(int robotId) {
		// WARNING: documentation for Rectangle2D.Double states the upper left corner should be specified,
		// use lower left corner instead
		objects.clear();
		for (Robot r : world.getReferee().getAlly().getRobotsOnSight())
			if (r.getPosition() != null)
				if (r.getRobotId() != robotId)
					objects.add(new Rectangle2D.Double(r.getPosition().getX() - DISTANCE_TO_ROBOT, r.getPosition()
							.getY() - DISTANCE_TO_ROBOT, DISTANCE_TO_ROBOT*2, DISTANCE_TO_ROBOT*2));

		for (Robot r : world.getReferee().getEnemy().getRobotsOnSight())
			if (r.getPosition() != null)
				objects.add(new Rectangle2D.Double(r.getPosition().getX() - DISTANCE_TO_ROBOT, r.getPosition()
						.getY() - DISTANCE_TO_ROBOT, DISTANCE_TO_ROBOT*2, DISTANCE_TO_ROBOT*2));
	}

	/**
	 * Generate vertices around every robot in the robot list
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
	
	
	protected boolean isObjectNotRemovable(int x, int y){
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
//					if(/*vertex1.isRemovable() && vertex2.isRemovable()*/ !vertex1.isStuck() && !vertex2.isStuck())	//Pre-generated.
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
		//TODO: MAKE SURE NEIGHBOURS OF A NOT REMOVABLE VERTEX DON'T GET REMOVED.
		for (Rectangle2D rect : objects)
			if (rect.intersectsLine(source.getPosition().getX(), source.getPosition().getY(), destination.getPosition()
					.getX(), destination.getPosition().getY()))
				return true;
		
		return false;
	}
	
	protected boolean isInsideObject(Point2D p){
		for(Rectangle2D rect : objects){
			if(rect.contains(p))
				return true;
		}
		return false;
	}

	public ArrayList<Vertex> getTestVertices() {
		return testVertices;
	}

	public void setTestVertices(ArrayList<Vertex> testVertices) {
		this.testVertices = testVertices;
	}

	public ArrayList<Rectangle2D> getObjects() {
		return objects;
	}

	public void setObjects(ArrayList<Rectangle2D> objects) {
		this.objects = objects;
	}
}
