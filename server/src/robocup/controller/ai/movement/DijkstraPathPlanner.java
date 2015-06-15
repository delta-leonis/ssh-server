package robocup.controller.ai.movement;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.beans.DesignMode;
import java.util.ArrayList;
import java.util.LinkedList;

import robocup.model.FieldPoint;
import robocup.model.Obstruction;
import robocup.model.Robot;
import robocup.model.World;
import robocup.model.enums.FieldZone;

/**
 * Pathplanner class based on Dijkstra's algorithm
 * Converts model to a graph and finds shortest path between two vertices using getRoute.
 * If the source or destination are too close to any other object, the planner will make 
 * a small detour around the source or destination.
 */
public class DijkstraPathPlanner {

	// Distance from the middle of the robot to the vertices around it
	// Basically the "Danger zone" for the Robot. A normal Robot has a radius of 90mm, so if DISTANCE_TO_ROBOT == 160mm,
	// then it means we don't want to get within (160mm - 90mm = ) 70mm of any other Robot. (Based on their center points)
	public static final int MIN_DISTANCE_TO_ROBOT = 195;
	public static final int MAX_DISTANCE_TO_ROBOT = 270;
	public static final int DISTANCE_TO_BALL = 120;
	public static final int DISTANCE_TO_POLYGON = 120;
	// This value is used to determine the vertex points, which are VERTEX_DISTANCE_TO_ROBOT from the middle points of the robots.
	public static final int MIN_VERTEX_DISTANCE_TO_ROBOT = 200;
	public static final int MAX_VERTEX_DISTANCE_TO_ROBOT = 450;
	private World world;
	private ArrayList<Shape> objects;
	private ArrayList<Shape> copyOfObjects;
	protected ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	private ArrayList<Vertex> allVertices = null;		//Vertices used for drawing.
	protected ArrayList<Vertex> notRemovableVertices; 	//Contains the source and destination.
	
	private LinkedList<FieldPoint> currentRoute;
	private FieldPoint source;
	private FieldPoint destination;

	/**
	 * Constructor that creates the pathplanner object
	 * Initializes the pathplanners arraylists and world object
	 */
	public DijkstraPathPlanner() {
		world = World.getInstance();
		objects = new ArrayList<Shape>(); 
		notRemovableVertices = new ArrayList<Vertex>();
	}

	/**
	 * Class representing a vertex in a graph
	 * Contains position, neighbours and previous vertex on the route
	 */
	public class Vertex {
		private FieldPoint position;
		private double distance;
		private ArrayList<Vertex> neighbours;
		private Vertex previous = null;
		private boolean removable = true;
		private boolean stuck = false;			// if there is no path between start and destination

		/**
		 * Create a vertex which contains the position, 
		 * all neighbours for the vertex and the previous vertex in the shortest path
		 * @param position
		 */
		public Vertex(FieldPoint position) {
			this.position = position;
			distance = Double.MAX_VALUE;
			neighbours = new ArrayList<Vertex>();
		}
		
		/**
		 * A method that converts a poin to a rectangle by using the current position as base
		 * @return
		 */
		public Ellipse2D toEllipse(){
			return new Ellipse2D.Double(position.getX() - MIN_DISTANCE_TO_ROBOT, position.getY() - MIN_DISTANCE_TO_ROBOT, MIN_DISTANCE_TO_ROBOT * 2, MIN_DISTANCE_TO_ROBOT * 2);
		}

		/**
		 * getter method that returns the position
		 * @return the position
		 */
		public FieldPoint getPosition() {
			return position;
		}

		/**
		 * getter method that returns the distance covered to reach this vertex
		 * @return the distance
		 */
		public double getDist() {
			return distance;
		}

		/**
		 * setter method that sets the distance
		 * @param alt the distance to set
		 */
		public void setDist(double alt) {
			distance = alt;
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
		 * deprecated method that checks whether the given rectangle shares the same center as the vertex
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
	public LinkedList<FieldPoint> getRoute(FieldPoint beginNode, FieldPoint desti, int robotId, boolean avoidBall, boolean avoidEastGoal, boolean avoidWestGoal) {
		LinkedList<FieldPoint> route = new LinkedList<FieldPoint>();
		boolean found = false;
		source = beginNode;
		destination = desti;

		generateObjectList(robotId, avoidBall, avoidEastGoal, avoidWestGoal);
		copyOfObjects = (ArrayList<Shape>)objects.clone();
		while(isInsidePolygon(new Vertex(destination).toEllipse())){
			if(getClosestVertexToPoint(destination) == null){
				return null;
			}
			destination = getClosestVertexToPoint(destination).getPosition();
		}

		// no object on route
		if (!intersectsObject(new Vertex(beginNode), new Vertex(destination))) {
			route.push(destination);
			found = true;
			reset();
			currentRoute = route;
			return route;
		}
		// generate vertices around robots and remove vertices colliding with
		// robots
		generateVertices(avoidBall);
		removeCollidingVertices();
		
		// add source and dest to vertices list
		Vertex source = setupSource(beginNode);
		if(source == null){
			allVertices = (ArrayList<Vertex>)vertices.clone();
			return null;					//Locked in
		}
		Vertex dest = setupDestination(destination);
		if(dest == null){	//TODO  fix
			allVertices = (ArrayList<Vertex>)vertices.clone();
			return null;					//Locked in
		}
		
		// calculate neighbours for every vertex
		generateNeighbours();
		
		allVertices = (ArrayList<Vertex>)vertices.clone();
		
		if(!found){
			// calculate the shortest path through the graph
			calculatePath(source, dest);
			// add positions to the route list
			Vertex u = dest;
			while (u.getPrevious() != null) {
				route.push(u.getPosition());
				u = u.getPrevious();
			}
			reset();
			
		}
		currentRoute = route;
		return route;
	}
	
	/**
	 * Sets up the source vertex, which has a few special properties.
	 * - A source Vertex may not be removed.
	 * - Its first point in its path is always one of its own generated vertices.
	 * - If these vertices all got removed, then the source is locked in.
	 * @param source The starting point of the robot.
	 */
	protected Vertex setupSource(FieldPoint beginNode){
		boolean lockedIn = true; //Locked in until proven otherwise
		Vertex source = new Vertex(beginNode);
		source.setDist(0);
		source.setRemovable(false);
		vertices.add(source);
		
		if(isInsideObject(source.toEllipse())) {
			source.setStuck(true);
			double x = beginNode.getX();
			double y = beginNode.getY();
			for(int i = 0; i < 8; ++i){
				Vertex neighbour = new Vertex(new FieldPoint(x
						+ Math.cos(Math.toRadians(45*i)) * MAX_VERTEX_DISTANCE_TO_ROBOT, y
						+ Math.sin(Math.toRadians(45*i)) * MAX_VERTEX_DISTANCE_TO_ROBOT));
				if (isValidPosition(source, neighbour)) {
					lockedIn = false;
					vertices.add(neighbour);
					neighbour.addNeighbour(source);
				}
			}
			
			// Stand still if we're locked in.
			if(lockedIn)
				return null;
			
			removeAllVectorsInRect(new Rectangle2D.Double(x - MAX_VERTEX_DISTANCE_TO_ROBOT + 1, y - MAX_VERTEX_DISTANCE_TO_ROBOT + 1, MAX_VERTEX_DISTANCE_TO_ROBOT * 2 - 2, MAX_VERTEX_DISTANCE_TO_ROBOT * 2 - 2));
		}
		
		return source;
	}
	
	/**
	 * Sets up the destination node and everything associated with it.
	 * @param endNode A {@link FieldPoint} that specifies the position of where you want to go.
	 * @return The endNode in Vertex form.
	 */
	protected Vertex setupDestination(FieldPoint endNode) {
		boolean lockedIn = true;
		Vertex destination = new Vertex(endNode);
		destination.setRemovable(false);
		vertices.add(destination);
		
		if (isInsideObject(destination.toEllipse())) {
			destination.setStuck(true);
			double x = endNode.getX();
			double y = endNode.getY();
			for(int i = 0; i < 8; ++i){
				Vertex neighbour = new Vertex(new FieldPoint(x
						+ Math.cos(Math.toRadians(45*i)) * MAX_VERTEX_DISTANCE_TO_ROBOT, y
						+ Math.sin(Math.toRadians(45*i)) * MAX_VERTEX_DISTANCE_TO_ROBOT));
				if (isValidPosition(destination, neighbour)) {
					lockedIn = false;
					vertices.add(neighbour);
					neighbour.addNeighbour(destination);
				}
			}

			if (lockedIn)
				return getClosestVertexToPoint(endNode);

			removeAllVectorsInRect(new Rectangle2D.Double(x
					- MAX_VERTEX_DISTANCE_TO_ROBOT + 1, y
					- MAX_VERTEX_DISTANCE_TO_ROBOT + 1,
					MAX_VERTEX_DISTANCE_TO_ROBOT * 2 - 2,
					MAX_VERTEX_DISTANCE_TO_ROBOT * 2 - 2));
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
	 * Checker method that returns if the robot is not placed in the danger zone of another robot
	 * @param source the original position vertex
	 * @param destination the destination position vertex
	 * @return true if the vertex is allowed to exist here, false otherwise.
	 */
	protected boolean isValidPosition(Vertex source, Vertex destination){
		for(Shape shape : objects){
			Area areaA = new Area(shape);
			areaA.intersect(new Area(source.toEllipse()));
			if(!areaA.isEmpty()) {
				// check whether it's in between source and destination.
				Rectangle2D smallerRect = new Rectangle2D.Double(shape.getBounds2D().getX() + 40, shape.getBounds2D().getY() + 40, 180, 180);
				if(smallerRect.intersectsLine(source.getPosition().getX(), source.getPosition().getY(), destination.getPosition()
						.getX(), destination.getPosition().getY())) {
					return false;
				}
			}
			if(shape.contains(destination.getPosition().toPoint2D())){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Function that removes all vectors in the given rectangle
	 * @param rect The area in which the vectors need to be removed.
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
	 * Calculate the path based on Dijkstra's algorithm.
	 * @param source A {@link Vertex} representing the source.
	 * @param dest A {@link Vertex} of the destination.
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
				double alt = u.getDist() + getDistance(u, v);

				// alternate path is shorter than the previous path to v
				if (alt < v.getDist()) {
					v.setDist(alt);
					v.setPrevious(u);
				}
			}
		}
	}
	

	/**
	 * Get the closest {@link Vertex} neighbor to the given {@link Vertex} u.
	 * @param u The {@link Vertex} we want the closest neighbor to.
	 * @return The closest {@link Vertex} neighbour to the given {@link Vertex}.
	 */
	private Vertex getMinDistNeighbour(Vertex u) {
		double minDist = Double.MAX_VALUE;
		Vertex closest = null;

		for (Vertex v : u.getNeighbours()) {
			double dist = getDistance(u, v);
			if (dist < minDist && !isVertexClosed(v)) {
				closest = v;
				minDist = dist;
			}
		}

		return closest;
	}

	/**
	 * Check if a {@link Vertex} has been used already.
	 * {@link Vertex Vertices} get removed while the path is being calculated.
	 * @param v The {@link Vertex} of which we want to know whether it has been used or not.
	 * @return true if v is not present in the list of open vertices, false otherwise.
	 * @see {@link DijkstraPathPlanner#calculatePath(Vertex, Vertex) calculatePath(Source, Destination)}
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
	 * @param robotId 	the robot id of the {@link robocup.model.Robot Robot} who needs a path, 
	 * 					no rectangle will be created for this robot in this function. 
	 * 					This rectangle might be created in {@link DijkstraPathPlanner#setupSource(FieldPoint) setupSouce()}.
	 */
	protected void generateObjectList(int robotId, boolean avoidBall, boolean avoidEastGoal, boolean avoidWestGoal) {
		Robot thisRobot = world.getAllRobots().get(robotId);
		objects.clear();
		for (Robot r : world.getReferee().getAlly().getRobotsOnSight())
			if (r.getPosition() != null)
				if (r.getRobotId() != robotId && !(r.getIgnore() && thisRobot.getIgnore()))
					objects.add(r.getDangerEllipse(MIN_DISTANCE_TO_ROBOT, MAX_DISTANCE_TO_ROBOT));

		for (Robot r : world.getReferee().getEnemy().getRobotsOnSight())
			if (r.getPosition() != null)
				objects.add(r.getDangerEllipse(MIN_DISTANCE_TO_ROBOT, MAX_DISTANCE_TO_ROBOT));
		
		// We're east team.
		if(avoidEastGoal){
			objects.add(FieldZone.EAST_NORTH_GOAL.getPolygon());
			objects.add(FieldZone.EAST_SOUTH_GOAL.getPolygon());
		}
		// We're west team
		if(avoidWestGoal){
			objects.add(FieldZone.WEST_NORTH_GOAL.getPolygon());
			objects.add(FieldZone.WEST_SOUTH_GOAL.getPolygon());
		}
		
		// Avoid goals.
		objects.add(world.getField().getEastGoal().toRect());
		objects.add(world.getField().getWestGoal().toRect());
		
		ArrayList<Obstruction> obstructions = world.getObstructions();
		for(Obstruction obstruction : obstructions){
			if(obstruction.toPolygon() != null)
				objects.add(obstruction.toPolygon());
		}

		if(avoidBall){
			//Add ball
			objects.add(world.getBall().getDangerRectangle(DISTANCE_TO_BALL));
		}
	}

	/**
	 * Generate vertices around every robot in the robot list
	 * only add vertices when it is not the source destination
	 */
	protected void generateVertices(boolean avoidBall) {
		vertices.clear();
		for (Robot robot : world.getAllRobotsOnSight()) {
			if(robot.getPosition() != null){
				double x = robot.getPosition().getX();
				double y = robot.getPosition().getY();
				double vertexDistance = MIN_VERTEX_DISTANCE_TO_ROBOT + 
						((MAX_VERTEX_DISTANCE_TO_ROBOT - MIN_VERTEX_DISTANCE_TO_ROBOT) * (Math.abs(robot.getSpeed()) / 5000));
				if(!isObjectNotRemovable(x,y)){	//Avoid double vertices from pre-generated vertices in source and dest.
					for(int i = 0; i < 16; ++i){
						vertices.add(new Vertex(new FieldPoint(x + Math.cos(Math.toRadians(22.5 * i)) * vertexDistance,
								y + Math.sin(Math.toRadians(22.5 * i)) * vertexDistance)));
					}
				}
			}
		}
		if(avoidBall){
			double x = world.getBall().getPosition().getX();
			double y = world.getBall().getPosition().getY();
			double vertexDistance = MIN_VERTEX_DISTANCE_TO_ROBOT + 
					((MAX_VERTEX_DISTANCE_TO_ROBOT - MIN_VERTEX_DISTANCE_TO_ROBOT) * (Math.abs(world.getBall().getSpeed()) / 5000));
			for(int i = 0; i < 16; ++i){
				vertices.add(new Vertex(new FieldPoint(x + Math.cos(Math.toRadians(22.5 * i)) * vertexDistance,
						y + Math.sin(Math.toRadians(22.5 * i)) * vertexDistance)));
			}
		}
		
		for(Shape shape : objects){
			if(shape instanceof Polygon){
				Polygon poly = (Polygon)shape;
				FieldPoint center = new FieldPoint(poly.getBounds().getCenterX(), poly.getBounds().getCenterY());
				// Middlepunt naar hoekpunt. Increase distance.
				for(int i = 0; i < poly.xpoints.length - 1; ++i){
					FieldPoint corner = new FieldPoint(poly.xpoints[i], poly.ypoints[i]);
					double angle = center.getAngle(corner);
					double distance = center.getDeltaDistance(corner);
					vertices.add(new Vertex(new FieldPoint(center.getX() + Math.cos(Math.toRadians(angle)) * (distance + DISTANCE_TO_POLYGON), 
															center.getY() + Math.sin(Math.toRadians(angle)) * (distance + DISTANCE_TO_POLYGON))));
				}
			}
			else if(shape instanceof Rectangle2D){
				Rectangle2D rect = (Rectangle2D)shape;
				vertices.add(new Vertex(new FieldPoint(rect.getCenterX() + (rect.getWidth()/2 + DISTANCE_TO_POLYGON), rect.getCenterY() + (rect.getHeight()/2 + DISTANCE_TO_POLYGON))));
				vertices.add(new Vertex(new FieldPoint(rect.getCenterX() - (rect.getWidth()/2 + DISTANCE_TO_POLYGON), rect.getCenterY() + (rect.getHeight()/2 + DISTANCE_TO_POLYGON))));
				vertices.add(new Vertex(new FieldPoint(rect.getCenterX() + (rect.getWidth()/2 + DISTANCE_TO_POLYGON), rect.getCenterY() - (rect.getHeight()/2 + DISTANCE_TO_POLYGON))));
				vertices.add(new Vertex(new FieldPoint(rect.getCenterX() - (rect.getWidth()/2 + DISTANCE_TO_POLYGON), rect.getCenterY() - (rect.getHeight()/2 + DISTANCE_TO_POLYGON))));

			}
		}
	}
	
	/**
	 * Checker method that checks if the rectangle around the given point belongs to a non removable vertex
	 * @param x X value of the center of the given rectangle
	 * @param y Y value of the center of the given rectangle
	 * @returns true if the given center of the rectangle belongs to a not removable vertex. False otherwise.
	 */
	protected boolean isObjectNotRemovable(double x, double y) {
		for(Vertex vertex : notRemovableVertices){
			if(vertex.getPosition().getX() == x || vertex.getPosition().getY() == y){
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
		for (Shape shapes : objects)
			for (Vertex v : vertices)
				if (shapes.contains(v.getPosition().getX(), v.getPosition().getY()) && v.isRemovable())
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
	 * @returns the distance between the two given {@link Vertex vertices}.
	 */
	private double getDistance(Vertex vertex1, Vertex vertex2) {
		return vertex1.getPosition().getDeltaDistance(vertex2.getPosition());
	}

	/**
	 * Calculate whether there's an object between two vertices
	 * @param source The source {@link Vertex}
	 * @param destination The destination {@link Vertex}
	 * @return true when an object is found between the vertices
	 */
	protected boolean intersectsObject(Vertex source, Vertex destination) {
		if(source.getPosition() != null || destination.getPosition() != null){
			Line2D line = new Line2D.Double(source.getPosition().getX(), source.getPosition().getY(), destination.getPosition()
					.getX(), destination.getPosition().getY());
			for (Shape shape : objects){
				if(shape instanceof Ellipse2D){
					if(line.ptSegDist(shape.getBounds().getCenterX(), shape.getBounds().getCenterY()) < shape.getBounds2D().getWidth()/2){
						return true;
					}
				}
				else if(shape instanceof Polygon){
					Polygon poly = (Polygon)shape;
					if(!poly.contains(source.getPosition().toPoint2D())){
						if(lineIntersectsPolygon(line, poly)){
							return true;
						}
					}
				}
				else if(shape instanceof Rectangle2D){
					Rectangle2D rect = (Rectangle2D)shape;
					if(rect.intersectsLine(new Line2D.Double(source.getPosition().getX(), source.getPosition().getY(), destination.getPosition().getX(), destination.getPosition().getY()))){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	protected boolean lineIntersectsPolygon(Line2D line, Polygon polygon){
		for(int i = 0; i < polygon.npoints - 1; ++i){
			Line2D line2 = new Line2D.Double(polygon.xpoints[i], polygon.ypoints[i], polygon.xpoints[i+1], polygon.ypoints[i+1]);
			if(line2.intersectsLine(line)){
				return true;
			}
		}
		return false;
	}
	
	protected boolean isInsidePolygon(Shape r){
		for(Shape shape : objects) {
			Area areaA = new Area(shape);
			areaA.intersect(new Area(r));
			if(!areaA.isEmpty()){
				return true;
			}
		}
		return false;
	}
	
	private Vertex getClosestVertexToPoint(FieldPoint point){
		double minDistance = Double.MAX_VALUE;
		Vertex minVertex = null;
		for(Vertex v : vertices){
			if(!(v.getPosition().getX() == point.getX() && v.getPosition().getY() == point.getY())){
				if(minVertex == null){
					minVertex = v;
				}
				else if(point.getDeltaDistance(v.getPosition()) < minDistance){
					minDistance = point.getDeltaDistance(v.getPosition());
					minVertex = v;
				}
			}
		}
		return minVertex;
	}
	
	/**
	 * Checker method that checks if the given rectangle intersects with any of the other rectangles.
	 * @param r {@link Rectangle2D} we want to test for.
	 * @return true if the {@link Rectangle2D} intersects with a rectangle on the field, false otherwise.
	 */
	protected boolean isInsideObject(Shape r) {
		for(Shape shape : objects) {
			Area areaA = new Area(shape);
			areaA.intersect(new Area(r));
			if(!areaA.isEmpty()){
				return true;
			}
		}
		return false;
	}

	/**
	 * Method that returns a copy of the vertices
	 * @returns a copy of the {@link Vertex vertices}.
	 */
	public ArrayList<Vertex> getAllVertices() {
		return allVertices;
	}

	/**
	 * Getter function that returns all current {@link Shape shapes}
	 * @return all objects in the arraylist objects
	 */
	public ArrayList<Shape> getObjects() {
		return objects;
	}
	
	/**
	 * @returns a copy of the objects, before they get removed.
	 */
	public ArrayList<Shape> getCopyOfObjects(){
		return copyOfObjects;
	}
	
	public LinkedList<FieldPoint> getCurrentRoute(){
		return currentRoute;
	}
	
	public FieldPoint getSource(){
		return source;
	}
	
	public FieldPoint getDestination(){
		return destination;
	}
}
