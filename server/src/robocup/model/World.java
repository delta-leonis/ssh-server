package robocup.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import java.util.TreeMap;
import java.util.Map.Entry;

import robocup.controller.ai.lowLevelBehavior.Keeper;
import robocup.model.enums.Command;
import robocup.model.enums.FieldZone;
import robocup.model.enums.TeamColor;
import robocup.view.GUI;

/**
 * Model representation of the physical "world", including the {@link Field}, all the
 * {@link Robot robots} (even non playing {@link Robot robots}) and the {@link Ball}
 */
public class World extends Observable {

	private static World instance;

	private Ball ball;
	private Referee referee;
	private Field field;

	private ArrayList<Robot> allyTeam;
	private ArrayList<Robot> enemyTeam;
	private ArrayList<Robot> robotList;

	private int robotRadius = Robot.DIAMETER / 2;

	private int fieldHeight;
	private int fieldWidth;

	private static final int TOTAL_TEAM_SIZE = 11;
	private final int STOP_BALL_DISTANCE = 500; // in mm

	public GUI gui;

	/**
	 * Constructor for the {@link World} Can only be called as a singleton.
	 */
	private World() {
		ball = new Ball();
		// set a ball position to prevent null pointers
		ball.setPosition(new FieldPoint(400, 200));
		referee = new Referee();
		field = new Field();

		// initialize all robots
		allyTeam = new ArrayList<Robot>();
		enemyTeam = new ArrayList<Robot>();
		;

		for (int i = 0; i < TOTAL_TEAM_SIZE; i++) {
			allyTeam.add(new Ally(i, 150));
		}
		for (int i = 0; i < TOTAL_TEAM_SIZE; i++) {
			enemyTeam.add(new Enemy(i, 150));
		}
		referee.initAllyTeam(allyTeam);
		referee.initEnemyTeam(enemyTeam);

		robotList = new ArrayList<Robot>();
		robotList.addAll(allyTeam);
		robotList.addAll(enemyTeam);

	}

	/**
	 * @return Singleton for the {@link World}
	 */
	public static World getInstance() {
		if (instance == null) {
			instance = new World();
		}
		return instance;
	}

	/**
	 * Set used {@link GUI}
	 * 
	 * @param current GUI 
	 */
	public void setGUI(GUI _gui) {
		gui = _gui;
	}

	/**
	 * mainly used to push updates
	 * 
	 * @return the {@link GUI} object
	 */
	public GUI getGUI() {
		return gui;
	}

	/**
	 * Sends a notification to the observers of {@link World}
	 * @param message appends to "HandlerFinished"
	 */
	public void HandlerFinished(String message) {
		setChanged();
		notifyObservers(message + "HandlerFinished");
	}

	/**
	 * @return the {@link Ball} that is currently in the field.
	 */
	public Ball getBall() {
		return ball;
	}

	/**
	 * @return the {@link Referee}
	 */
	public Referee getReferee() {
		return referee;
	}

	/**
	 * @return the {@link Field} that is used for this game.
	 */
	public Field getField() {
		return field;
	}

	/**
	 * Returns the {@link Team} with the given color. this method is an adapter
	 * from the old model, so its better to use referee getTeambyColor
	 * 
	 * @param color the {@link TeamColor} of the {@link Team}
	 * @return the {@link Team} with the given color. Returns null if there is
	 *         no {@link Team} with the given color.
	 */
	public Team getTeamByColor(TeamColor color) {
		return referee.getTeamByColor(color);
	}

	/**
	 * 
	 * @return all the robots in the current match including substitutes
	 */
	public ArrayList<Robot> getAllRobots() {
		return robotList;
	}

	/**
	 * @return all robots currently on the playing field
	 */
	public ArrayList<Robot> getAllRobotsOnSight() {
		ArrayList<Robot> onsight = new ArrayList<Robot>();
		for (Robot robot : robotList) {
			if (robot.isOnSight())
				onsight.add(robot);
		}
		return onsight;
	}

	/**
	 * Sets the {@link Field} of the current match. The {@link Field} contains
	 * all variables regarding the {@link Field}. (Think of field width, goal
	 * length etc.)
	 * 
	 * @param field
	 *            the {@link Field} to set for the current match.
	 */
	public void setField(Field field) {
		this.field = field;
	}

	@Override
	public String toString() {
		return "World \r\n[ball=" + ball + "\r\nreferee=" + referee + "\r\n" + field + "]";
	}

	/**
	 * Calculate if ally {@link Team} is closer to the {@link Ball}
	 * 
	 * @return true when the ally {@link Team} is closer
	 */
	public boolean allyHasBall() {
		ArrayList<Robot> allies = getReferee().getAlly().getRobots();
		ArrayList<Robot> enemies = getReferee().getEnemy().getRobots();

		double distanceAlly = getTeamDistanceToBall(allies);
		double distanceEnemy = getTeamDistanceToBall(enemies);

		return distanceAlly <= distanceEnemy;
	}

	/**
	 * Count all attacking {@link Enemy enemies}. An {@link Enemy} is attacking when it is located on
	 * the same field half as our {@link Keeper}.
	 * 
	 * @return sum of all attacking {@link Enemy enemies}
	 */
	public int getAttackingEnemiesCount() {
		int count = 0;
		FieldPoint keeperPosition = referee.getAlly().getRobotByID(referee.getAlly().getGoalie()).getPosition();

		for (Robot r : referee.getEnemy().getRobots()) {
			if (r.getPosition().getX() > 0.0 && keeperPosition.getX() > 0.0 || r.getPosition().getX() < 0.0
					&& keeperPosition.getX() < 0.0)
				count++;
		}

		return count;
	}

	/**
	 * Get the closest {@link Robot} to the {@link Ball}. This is either an {@link Ally} or an {@link Enemy}
	 * robot.
	 * 
	 * @return the closest {@link Robot} to the {@link Ball}
	 */
	public Robot getClosestRobotToBall() {
		ArrayList<Robot> robots = new ArrayList<Robot>();
		robots.addAll(getReferee().getAlly().getRobots());
		robots.addAll(getReferee().getEnemy().getRobots());

		double minDistance = -1.0;
		Robot minDistanceRobot = null;

		for (Robot robot : robots) {
			if (minDistance == -1.0) {
				minDistanceRobot = robot;
				minDistance = robot.getPosition().getDeltaDistance(ball.getPosition());
			} else {
				double distance = robot.getPosition().getDeltaDistance(ball.getPosition());

				if (distance < minDistance) {
					minDistanceRobot = robot;
					minDistance = distance;
				}
			}
		}

		return minDistanceRobot;
	}

	/**
	 * Get the distance from the closest {@link Robot} in one {@link Team} to the {@link Ball}
	 * 
	 * @param robots
	 *            the team of {@link Robot robots}
	 * @return the distance of the closest {@link Robot}
	 */
	private double getTeamDistanceToBall(ArrayList<Robot> robots) {
		if (ball == null)
			return Integer.MAX_VALUE;

		double minDistance = -1.0;

		for (Robot r : robots) {
			if (minDistance == -1.0)
				minDistance = r.getPosition().getDeltaDistance(ball.getPosition());
			else {
				double distance = r.getPosition().getDeltaDistance(ball.getPosition());

				if (distance < minDistance)
					minDistance = distance;
			}
		}

		return minDistance;
	}

	/**
	 * method that returns a robot by checking vertex points of the given
	 * {@link FieldZone} the closest robot will be returned
	 * 
	 * this method is not yet finished, as {@link Robot robots} "within" the {@link FieldZone} should get
	 * priority, as should the center of the zone be more important
	 * 
	 * @param fieldZone
	 * @return	Robot without role in given {@link FieldZone}
	 */
	public Robot getClosestRobotToZoneWithoutRole(FieldZone fieldZone) {
		Ally foundAlly = null;
		double closestDistance = Double.MAX_VALUE;

		for (Robot itRobot : allyTeam) {
			Ally itAlly = (Ally) itRobot;

			if (itAlly.getRole() == null) {
				double itDistance = fieldZone.getClosestVertex(itAlly.getPosition()).getDeltaDistance(
						itAlly.getPosition());
				if (itDistance < closestDistance) {
					closestDistance = itDistance;
					foundAlly = itAlly;
				}
			}
		}
		return foundAlly;
	}

	/**
	 * Helper function for {@link Referee} {@link Command commands}, checks last {@Link Command} issued when the
	 * robot may move because of the "keep 50 cm clearance" rule, pathfinding
	 * must find a way from the {@link Ball}
	 * 
	 * @param robotID
	 * @return <b>boolean</b> indicating if movement is allowed
	 */
	public boolean robotMayMove(int robotID) {
		// Halt = all robots stop
		if (referee.getCommand() == Command.HALT) {
			return false;
		}

		// Stop = keep 50cm from ball
		if (referee.getCommand() == Command.STOP) {
			// if the distance to ball is less then 50cm, is so return false
			if (referee.getAlly().getRobotByID(robotID).getPosition().getDeltaDistance(ball.getPosition()) < STOP_BALL_DISTANCE) {
				return false;
			}

			// Goal = Should be treated the same as STOP
		} else if (referee.getCommand() == Command.GOAL_YELLOW || referee.getCommand() == Command.GOAL_BLUE) {
			return false;
		}
		return true;
	}

	/**
	 * Method that retrieves all {@link Ally allies} that are present within a given polygon
	 * 
	 * @param argPoly
	 *            the polygon in which the method looks for {@link Ally allies}, a point list
	 *            with absolute locations
	 * @return an list with all the ally {@link Robot robots}
	 */
	public ArrayList<Ally> getAllyRobotsInArea(FieldPoint[] argPoly) {
		ArrayList<Ally> foundAllies = new ArrayList<Ally>();
		for (Robot ally : allyTeam) {
			if (areaContainsCircle(ally.getPosition(), argPoly, robotRadius)) {
				foundAllies.add((Ally) ally);
			}
		}
		return foundAllies;
	}

	/**
	 * Method that retrieves all enemies that are present within a zone
	 * 
	 * @param argPoly
	 *            the polygon in which the method looks for enemies, a point
	 *            list with absolute locations
	 * @return an list with all the {@link Enemy enemies}
	 */
	public ArrayList<Enemy> getEnemyRobotsInArea(FieldPoint[] argPoly) {
		ArrayList<Enemy> foundEnemies = new ArrayList<Enemy>();
		for (Robot enemy : enemyTeam) {
			if (areaContainsCircle(enemy.getPosition(), argPoly, robotRadius)) {
				foundEnemies.add((Enemy) enemy);
			}
		}
		return foundEnemies;
	}

	/**
	 * Method that retrieves all {@link Robot robots} that are present within a {@link FieldZone}
	 * 
	 * @param argPoly
	 *            the polygon in which the method looks for {@link Robot robots}, a {@link FieldPoint} list
	 *            with absolute locations
	 * @return an list with all the {@link Robot robots}
	 */
	public ArrayList<Robot> getAllRobotsInArea(FieldPoint... argPoly) {
		ArrayList<Robot> foundRobots = new ArrayList<Robot>();
		for (Robot robot : robotList) {
			if (areaContainsCircle(robot.getPosition(), argPoly, robotRadius)) {
				foundRobots.add(robot);
			}
		}
		return foundRobots;
	}

	/**
	 * Method that retrieves all allies that are present within a {@link FieldZone}
	 * 
	 * @param fieldZones
	 *            array with all the zones where to look for robots
	 * @return an list with all the found robots
	 */
	public ArrayList<Ally> getAllyRobotsInZones(ArrayList<FieldZone> fieldZones) {
		ArrayList<Ally> foundAllies = new ArrayList<Ally>();

		for (FieldZone fieldZone : FieldZone.values()) {
			for (Robot ally : allyTeam) {
				if (fieldZone.contains(ally.getPosition())) {
					foundAllies.add((Ally) ally);
				}
			}
		}
		return foundAllies;
	}

	/**
	 * Method that retrieves all enemies that are present within a {@link FieldZone}
	 * 
	 * @param fieldZones
	 *            array with all the {@link FieldZone zones} where to look for {@link Robot robots}
	 * @return an list with all the found {@link Robot robots}
	 */
	public ArrayList<Enemy> getEnemyRobotsInZones(ArrayList<FieldZone> fieldZones) {
		ArrayList<Enemy> foundEnemies = new ArrayList<Enemy>();

		for (FieldZone fieldZone : fieldZones) {
			for (Robot enemy : enemyTeam) {
				if (fieldZone.contains(enemy.getPosition())) {
					foundEnemies.add((Enemy) enemy);
				}
				;
			}
		}
		return foundEnemies;
	}

	/**
	 * Method that locates an {@link FieldObject} and returns the {@link Field} where the {@link FieldObject} is
	 * (or null if not found)
	 * 
	 * @param fieldObject
	 *            the object to locate
	 * @return FieldZone where the given {@link FieldObject} is located
	 */
	public FieldZone locateFieldObject(FieldObject fieldObject) {
		for (FieldZone fieldZone : FieldZone.values()) {
			if (fieldZone.contains(fieldObject.getPosition())) {
				return fieldZone;
			}
		}
		return null;
	}

	/**
	 * Method that retrieves all {@link Robot robots} that are present within given {@link FieldZone zones}
	 * 
	 * @param fieldZones
	 *            a list with {@link FieldZone fieldzones} which have to be searched for {@link Robot robots}
	 * @return list containing all the found {@link Robot robots} in the {@link FieldZone fieldzones} 
	 */
	public ArrayList<Robot> getAllRobotsInZones(ArrayList<FieldZone> fieldZones) {
		ArrayList<Robot> foundRobots = new ArrayList<Robot>();

		for (FieldZone fieldZone : fieldZones) {
			for (Robot robot : robotList) {
				if (fieldZone.contains(robot.getPosition())) {
					foundRobots.add(robot);
				}
				;
			}
		}
		return foundRobots;
	}

	/**
	 * method that calculates if a circle falls in or touches a polygon accepts
	 * 
	 * @param argPoint
	 *            the center of the circle
	 * @param areaPoly
	 *            the point array with corner locations of the polygon
	 * @param radius
	 *            the radius of the circle
	 */
	public boolean areaContainsCircle(FieldPoint argPoint, FieldPoint[] areaPoly, double radius) {
		boolean result = false;

		for (int edges = 0; edges < areaPoly.length - 1; edges++) {
			if (pointToLineDistance(new FieldPoint(areaPoly[edges].getX() + (fieldWidth / 2), areaPoly[edges].getY()
					+ (fieldHeight / 2)), new FieldPoint(areaPoly[edges + 1].getX() + (fieldWidth / 2),
					areaPoly[edges + 1].getY() + (fieldHeight / 2)), new FieldPoint(argPoint.getX() + (fieldWidth / 2),
					argPoint.getY() + (fieldHeight / 2))) < radius) {
				result = true;
			}
		}

		result = result || pointInPolygon(argPoint, areaPoly);
		return result;
	}

	/**
	 * a method that calculates the distance of the right-angle between a point
	 * and a line
	 * 
	 * @param A
	 *            the start point of the line
	 * @param B
	 *            the end point of the line
	 * @param P
	 *            the point to check the distance from the line with
	 */
	public double pointToLineDistance(FieldPoint A, FieldPoint B, FieldPoint P) {
		double normalLength = Math.sqrt((B.getX() - A.getX()) * (B.getX() - A.getX()) + (B.getY() - A.getY())
				* (B.getY() - A.getY()));
		return Math.abs((P.getX() - A.getX()) * (B.getY() - A.getY()) - (P.getY() - A.getY()) * (B.getX() - A.getX()))
				/ normalLength;
	}

	/**
	 * a method that checks if a point falls within a polygon
	 * 
	 * @param argPoint the point
	 * @param point [] the array with the locations of the polygon
	 */
	public boolean pointInPolygon(FieldPoint argPoint, FieldPoint[] areaPoly) {
		boolean result = false;
		for (int i = 0, j = areaPoly.length - 1; i < areaPoly.length; j = i++) {
			if ((areaPoly[i].getY() > argPoint.getY()) != (areaPoly[j].getY() > argPoint.getY())
					&& (argPoint.getX() < (areaPoly[j].getX() - areaPoly[i].getX())
							* (argPoint.getY() - areaPoly[i].getY()) / (areaPoly[j].getY() - areaPoly[i].getY())
							+ areaPoly[i].getX())) {
				result = !result;
			}
		}
		return result;
	}

	/**
	 * Checks whether a {@link Robot} has a free shot, will only be checked if the {@link Robot}
	 * is in one of the 6 center zones (due to accuracy) returns a
	 * {@link FieldPoint} that represents the ideal shooting position Uses all
	 * zones except for the 4 corners Uses a maximum of 5 obstacles
	 * 
	 * @return Point ideal aim position in goal
	 */
	public FieldPoint hasFreeShot() {
		return hasFreeShot(5);
	}

	/**
	 * Checks whether a {@link Robot} has a free shot, will only be checked if the  {@link Robot}
	 * is in one of the 6 center zones (due to accuracy) returns a
	 * {@link FieldPoint} that represents the ideal shooting position Uses all
	 * zones except for the 4 corners
	 * 
	 * @param maxObstacles
	 *            maximum number of obstacles in the shooting triangle
	 *            (ball-leftpost-rightpost)
	 * @return Point ideal aim position in goal
	 */
	public FieldPoint hasFreeShot(int maxObstacles) {
		FieldZone[] zones = { FieldZone.EAST_NORTH_FRONT, FieldZone.EAST_CENTER, FieldZone.EAST_SOUTH_FRONT,
				FieldZone.EAST_MIDDLE, FieldZone.EAST_NORTH_SECONDPOST, FieldZone.EAST_SOUTH_SECONDPOST,
				FieldZone.WEST_NORTH_FRONT, FieldZone.WEST_CENTER, FieldZone.WEST_SOUTH_FRONT, FieldZone.WEST_MIDDLE,
				FieldZone.WEST_SOUTH_SECONDPOST, FieldZone.WEST_NORTH_SECONDPOST };

		return hasFreeShot(zones, maxObstacles);
	}

	/**
	 * Checks whether a {@link Robot} has a free shot, will only be checked if the {@link Robot}
	 * is in one of the 6 center zones (due to accuracy) returns a
	 * {@link FieldPoint} that represents the ideal shooting position
	 * 
	 * @param zones
	 *            zones that the ball has to be in in order to check a free shot
	 * @param maxObstacles
	 *            maximum number of obstacles in the shooting triangle
	 *            (ball-leftpost-rightpost)
	 * @return Point ideal aim position in goal
	 */
	public FieldPoint hasFreeShot(FieldZone[] zones, int maxObstacles) {
		Ball ball = World.getInstance().getBall();
		// only proceed when we are the ballowner
		if (ball.getOwner() instanceof Enemy)
			return null;

		// check if the ball is in a zone from which we can actually make the
		// angle
		if (!Arrays.asList(zones).contains(World.getInstance().locateFieldObject(ball)))
			return null;

		// get the enemy goal (checking which side is ours, and get the opposite
		Goal enemyGoal = (World.getInstance().getReferee().isWestTeamColor(World.getInstance().getReferee()
				.getAllyTeamColor())) ? World.getInstance().getField().getEastGoal() : World.getInstance().getField()
				.getWestGoal();

		ArrayList<Robot> obstacles = World.getInstance().getAllRobotsInArea(
				new FieldPoint[] { enemyGoal.getFrontSouth(), enemyGoal.getFrontNorth(), ball.getPosition() });

		// No obstacles?! shoot directly in the center of the goal;
		if (obstacles.size() == 0)
			return new FieldPoint(enemyGoal.getFrontSouth().getX(), 0.0);

		if (obstacles.size() >= maxObstacles)
			return null;

		// make a list with all blocked areas.
		// Y is the same for all points, so key = x1, and value = x2
		// that way the map is automatically ordered in size, note that adding a
		// new
		// point should check whether the key exist, and if the new value is
		// bigger or smaller to
		// prevent loss of points
		TreeMap<Double, Double> obstructedArea = new TreeMap<Double, Double>();

		for (Robot obstacle : obstacles) {
			double distance = obstacle.getPosition().getDeltaDistance(ball.getPosition());
			double divertAngle = Math.atan((Robot.DIAMETER / 2) / distance);

			double obstacleLeftAngle = Math.toRadians(ball.getPosition().getAngle(obstacle.getPosition()))
					+ divertAngle;
			double obstacleRightAngle = Math.toRadians(ball.getPosition().getAngle(obstacle.getPosition()))
					- divertAngle;

			double dx = enemyGoal.getFrontSouth().getX() - ball.getPosition().getX();
			double dyL = Math.tan(obstacleLeftAngle) * dx;
			double dyR = Math.tan(obstacleRightAngle) * dx;

			FieldPoint L = new FieldPoint((ball.getPosition().getX() + dx), (ball.getPosition().getY() + dyL));
			FieldPoint R = new FieldPoint((ball.getPosition().getX() + dx), (ball.getPosition().getY() + dyR));

			// is there a point with the same start X coordinate
			// if so, check whether it is bigger, and replace it if necessary
			if (!obstructedArea.containsKey(L.toPoint2D().getY())
					|| (obstructedArea.get(L.toPoint2D().getY())) > R.toPoint2D().getY())
				obstructedArea.put(L.toPoint2D().getY(), R.toPoint2D().getY());
		}
		double minY = enemyGoal.getFrontSouth().getY();
		double maxY = enemyGoal.getFrontNorth().getY();
		obstructedArea = minMax(obstructedArea, minY, maxY);
		obstructedArea = mergeOverlappingValues(obstructedArea);

		TreeMap<Double, Double> availableArea = invertMap(obstructedArea);

		if (obstructedArea.firstKey() != minY)
			availableArea.put(minY, obstructedArea.firstKey());
		if (obstructedArea.lastEntry().getValue() != maxY)
			availableArea.put(obstructedArea.lastEntry().getValue(), maxY);

		double x = enemyGoal.getFrontSouth().getX();

		if (availableArea.size() <= 0)
			return null;

		// in this case size DOES matter
		Double biggestKey = availableArea.firstKey();
		for (Entry<Double, Double> entry : availableArea.entrySet())
			if (entry.getValue() - entry.getKey() > availableArea.get(biggestKey) - biggestKey)
				biggestKey = entry.getKey();

		System.out.println("Size: " + biggestKey + availableArea.get(biggestKey));

		// return point that lies in the center of the biggest point
		FieldPoint hitmarker = new FieldPoint(x, (biggestKey / 2 + availableArea.get(biggestKey) / 2));
		return hitmarker;
	}

	/**
	 * Ensures all values are within given limits
	 * 
	 * @param map map that has to be processed
	 * @param min minimum value
	 * @param max maximum value
	 * @return map that honorers limits
	 */
	private TreeMap<Double, Double> minMax(TreeMap<Double, Double> map, double min, double max) {
		TreeMap<Double, Double> newMap = new TreeMap<Double, Double>();
		for (Entry<Double, Double> entry : map.entrySet()) {
			Double newKey, newValue;
			newKey = Math.min(Math.max(entry.getKey(), min), max);
			newValue = Math.min(Math.max(entry.getValue(), min), max);
			newMap.put(newKey, newValue);
		}
		return newMap;
	}

	/**
	 * "inverts" a TreeMap, example:  <br>
	 *  Y1 | Y2 <br>
	 *  16 | 100  <br>
	 * 120 | 130  <br><br>
	 * 
	 * returns: <br>
	 *  Y1 | Y2 <br>
	 * 100 | 120 <br>
	 * 
	 * @param map
	 * @return inverted map
	 */
	private TreeMap<Double, Double> invertMap(TreeMap<Double, Double> map) {
		TreeMap<Double, Double> invertedMap = new TreeMap<Double, Double>();
		Double prevY1 = null, prevY2 = null;
		for (Entry<Double, Double> entry : map.entrySet()) {
			if (prevY1 == null) {
				prevY1 = entry.getKey();
				prevY2 = entry.getValue();
				continue;
			}
			Double Y1 = entry.getKey();
			invertedMap.put(prevY2, Y1);
		}
		return invertedMap;
	}

	/**
	 * Merges a TreeMap that has overlapping values, example  <br>
	 * Y1 | Y2 <br>
	 * 16 | 100 <br>
	 * 80 | 130 <br> <br>
	 * 
	 * will return:  <br>
	 * Y1 | Y2 <br>
	 * 16 | 130 <br>
	 * 
	 * @param map to be processed
	 * @return processed map
	 */
	private TreeMap<Double, Double> mergeOverlappingValues(TreeMap<Double, Double> map) {
		TreeMap<Double, Double> mergedMap = new TreeMap<Double, Double>();
		Double prevY1 = null, prevY2 = null;
		for (Entry<Double, Double> entry : map.entrySet()) {
			if (prevY1 == null) {
				prevY1 = entry.getKey();
				prevY2 = entry.getValue();
				continue;
			}
			Double Y1 = entry.getKey();
			Double Y2 = entry.getValue();
			if (prevY2 >= Y1) {
				// meergeeee
				prevY1 = (prevY1 > Y1 ? Y1 : prevY1);
				prevY2 = (prevY2 > Y2 ? prevY2 : Y2);
			} else {
				mergedMap.put((prevY1 < prevY2 ? prevY1 : prevY2), (prevY1 > prevY2 ? prevY1 : prevY2));
				// no merge
				prevY1 = entry.getKey();
				prevY2 = entry.getValue();
			}
		}
		mergedMap.put((prevY1 < prevY2 ? prevY1 : prevY2), (prevY1 > prevY2 ? prevY1 : prevY2));
		return mergedMap;
	}
}
