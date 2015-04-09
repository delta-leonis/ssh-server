package robocup.controller.ai.highLevelBehavior.zoneBehavior;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.TreeMap;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.controller.ai.lowLevelBehavior.Attacker;
import robocup.controller.ai.lowLevelBehavior.Coverer;
import robocup.controller.ai.lowLevelBehavior.Keeper;
import robocup.controller.ai.lowLevelBehavior.KeeperDefender;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.Ally;
import robocup.model.Ball;
import robocup.model.Enemy;
import robocup.model.FieldPoint;
import robocup.model.Goal;
import robocup.model.Robot;
import robocup.model.World;
import robocup.model.enums.FieldZone;
import robocup.output.ComInterface;
import robocup.output.RobotCom;

public abstract class Mode {

	protected World world;
	protected Ball ball;
	protected Strategy strategy;

	/** Co-ordinates of the goal on the left side of the field */
	private static final FieldPoint MID_GOAL_NEGATIVE = new FieldPoint(-(World.getInstance().getField().getLength() / 2), 0);
	/** Co-ordinates of the goal on the right side of the field */
	private static final FieldPoint MID_GOAL_POSITIVE = new FieldPoint(World.getInstance().getField().getLength() / 2, 0);

	public Mode(Strategy strategy, ArrayList<RobotExecuter> executers) {
		world = World.getInstance();
		ball = world.getBall();
		this.strategy = strategy;
	}

	/**
	 * Execute the mode.
	 * New roles will be assigned to every robot and their lowlevel behaviors will be updated.
	 * @param executers
	 */
	public void execute(ArrayList<RobotExecuter> executers) {
		try {
			setRoles(executers);

			for (RobotExecuter executer : executers)
				updateExecuter(executer);

		} catch (Exception e) {
			System.out.println("Exception in Mode, please fix me :(");
			e.printStackTrace();
		}
	}

	/**
	 * Get the strategy for this Mode
	 * @return the strategy
	 */
	public Strategy getStrategy() {
		return strategy;
	}

	/**
	 * Set the roles for all executers based on current strategy and mode.
	 */
	public abstract void setRoles(ArrayList<RobotExecuter> executers);

	/**
	 * Update an executer.
	 * A new lowlevel behavior will be created if the role is different from the previous role.
	 * The lowlevel behavior will receive updated values.
	 * @param executer the executer to update
	 */
	private void updateExecuter(RobotExecuter executer) {
		// Execute handle functions based on role
		switch (((Ally) executer.getRobot()).getRole()) {
		case ATTACKER:
			handleAttacker(executer);
			break;
		case COVERER:
			handleCoverer(executer);
			break;
		case KEEPERDEFENDER:
			handleKeeperDefender(executer);
			break;
		case KEEPER:
			handleKeeper(executer);
			break;
		default:
			System.out.println("Role used without handle function, please add me in Mode.java, role: "
					+ ((Ally) executer.getRobot()).getRole());
		}
	}

	/**
	 * Handle the behavior of the Attacker.
	 * A new Attacker behavior will be created if the current lowlevel behavior is not an Attacker.
	 * Update the values of the Attacker afterwards.
	 * @param executer the executer which needs to be handled
	 */
	private void handleAttacker(RobotExecuter executer) {
		if (!(executer.getLowLevelBehavior() instanceof Attacker))
			executer.setLowLevelBehavior(new Attacker(executer.getRobot(), ComInterface.getInstance(RobotCom.class),
					null, null, 0, false, 0));

		updateAttacker(executer);
	}

	/**
	 * Update the values of the Attacker behavior belonging to the executer.
	 * @param executer the executer to update
	 */
	protected abstract void updateAttacker(RobotExecuter executer);

	/**
	 * Handle the behavior of the Blocker.
	 * A new Blocker behavior will be created if the current lowlevel behavior is not a Blocker.
	 * Update the values of the Blocker afterwards.
	 * @param executer the executer which needs to be handled
	 */
	private void handleCoverer(RobotExecuter executer) {
		if (!(executer.getLowLevelBehavior() instanceof Coverer))
			executer.setLowLevelBehavior(new Coverer(executer.getRobot(), ComInterface.getInstance(RobotCom.class),
					250, null, null, null, 0));

		updateCoverer(executer);
	}

	/**
	 * Update the values of the Blocker behavior belonging to the executer.
	 * @param executer the executer to update
	 */
	protected abstract void updateCoverer(RobotExecuter executer);

	/**
	 * Handle the behavior of the KeeperDefender.
	 * A new Attacker behavior will be created if the current lowlevel behavior is not a KeeperDefender.
	 * Update the values of the KeeperDefender afterwards.
	 * @param executer the executer which needs to be handled
	 */
	private void handleKeeperDefender(RobotExecuter executer) {
		if (!(executer.getLowLevelBehavior() instanceof KeeperDefender))
			executer.setLowLevelBehavior(new KeeperDefender(executer.getRobot(), ComInterface
					.getInstance(RobotCom.class), 1200, false, null, null, null, null, 0));

		updateKeeperDefender(executer);
	}

	/**
	 * Update the values of the KeeperDefender behavior belonging to the executer.
	 * @param executer the executer to update
	 */
	protected abstract void updateKeeperDefender(RobotExecuter executer);

	/**
	 * Handle the behavior of the Keeper.
	 * A new Keeper behavior will be created if the current lowlevel behavior is not a Keeper.
	 * Update the values of the Keeper afterwards.
	 * @param executer the executer which needs to be handled
	 */
	private void handleKeeper(RobotExecuter executer) {
		Robot keeper = executer.getRobot();

		// TODO determine field half in a better way
		if (!(executer.getLowLevelBehavior() instanceof Keeper))
			executer.setLowLevelBehavior(new Keeper(keeper, ComInterface.getInstance(RobotCom.class), 500, false, ball
					.getPosition(), keeper.getPosition(), keeper.getPosition().getX() < 0 ? MID_GOAL_NEGATIVE
					: MID_GOAL_POSITIVE, world.getField().getWidth() / 2));

		updateKeeper(executer);
	}

	/**
	 * Update the values of the Keeper behavior belonging to the executer.
	 * @param executer the executer to update
	 */
	protected abstract void updateKeeper(RobotExecuter executer);

	/**
	 * Get all Robots without a role
	 * @return ArrayList containing all Ally robots without a role
	 */
	protected ArrayList<Ally> getAllyRobotsWithoutRole() {
		ArrayList<Ally> robotsWithoutRole = new ArrayList<Ally>();

		for (Robot robot : world.getReferee().getAlly().getRobots())
			if (((Ally) robot).getRole() == null)
				robotsWithoutRole.add((Ally) robot);

		return robotsWithoutRole;
	}

	/**
	 * Checks whether a ally has a free shot, will only be checked
	 * if the robot is in one of the 6 center zones (due to accuracy)
	 * returns a {@link FieldPoint} that represents the ideal shooting position
	 * Uses all zones except for the 4 corners
	 * Uses a maximum of 5 obstacles
	 * 
	 * @return Point	ideal aim position in goal
	 */
	public FieldPoint hasFreeShot(){
		return hasFreeShot(5);
	}
	
	/**
	 * Checks whether a ally has a free shot, will only be checked
	 * if the robot is in one of the 6 center zones (due to accuracy)
	 * returns a {@link FieldPoint} that represents the ideal shooting position
	 * Uses all zones except for the 4 corners
	 * 
	 * @param maxObstacles	maximum number of obstacles in the shooting triangle (ball-leftpost-rightpost)
	 * @return Point	ideal aim position in goal
	 */
	public FieldPoint hasFreeShot(int maxObstacles){
		FieldZone[] zones = {FieldZone.EAST_NORTH_FRONT, FieldZone.EAST_CENTER, FieldZone.EAST_SOUTH_FRONT, FieldZone.EAST_MIDDLE, FieldZone.EAST_NORTH_SECONDPOST, FieldZone.EAST_SOUTH_SECONDPOST, 
							FieldZone.WEST_NORTH_FRONT, FieldZone.WEST_CENTER, FieldZone.WEST_SOUTH_FRONT, FieldZone.WEST_MIDDLE, FieldZone.WEST_SOUTH_SECONDPOST, FieldZone.WEST_NORTH_SECONDPOST};

		return hasFreeShot(zones, maxObstacles);
	}
	
	/**
	 * Checks whether a ally has a free shot, will only be checked
	 * if the robot is in one of the 6 center zones (due to accuracy)
	 * returns a {@link FieldPoint} that represents the ideal shooting position
	 *  
	 * @param zones		zones that the ball has to be in in order to check a free shot
	 * @param maxObstacles	maximum number of obstacles in the shooting triangle (ball-leftpost-rightpost)
	 * @return Point	ideal aim position in goal
	 */
	public FieldPoint hasFreeShot(FieldZone[] zones, int maxObstacles){
    	Ball ball = World.getInstance().getBall();
		//only proceed when we are the ballowner
		if(ball.getOwner() instanceof Enemy)
			return null;

		//check if the ball is in a zone from which we can actually make the angle
		if(!Arrays.asList(zones).contains(World.getInstance().locateFieldObject(ball)))
			return null;

		//get the enemy goal (checking which side is ours, and get the opposite 
		Goal enemyGoal = (World.getInstance().getReferee().isWestTeamColor(World.getInstance().getReferee().getAllyTeamColor())) ?  World.getInstance().getField().getEastGoal() : World.getInstance().getField().getWestGoal();

		ArrayList<Robot> obstacles = World.getInstance().getAllRobotsInArea(new FieldPoint[]{enemyGoal.getFrontSouth(), enemyGoal.getFrontNorth(), ball.getPosition()});

		//No obstacles?! shoot directly in the center of the goal;
		if(obstacles.size() == 0)
			return new FieldPoint(enemyGoal.getFrontSouth().getX(), 0.0);
		
		if(obstacles.size() >= maxObstacles)
			return null;

		//make a list with all blocked areas.
		//Y is the same for all points, so key = x1, and value = x2
		//that way the map is automatically ordered in size, note that adding a new
		//point should check whether the key exist, and if the new value is bigger or smaller to
		//prevent loss of points
		TreeMap<Double, Double> obstructedArea = new TreeMap<Double, Double>();

		for(Robot obstacle : obstacles){
			double distance = obstacle.getPosition().getDeltaDistance(ball.getPosition()); 
			double divertAngle = Math.atan((Robot.DIAMETER/2) / distance);			
			
			double obstacleLeftAngle = Math.toRadians(ball.getPosition().getAngle(obstacle.getPosition()))  + divertAngle;
			double obstacleRightAngle = Math.toRadians(ball.getPosition().getAngle(obstacle.getPosition())) - divertAngle;
			
			double dx = enemyGoal.getFrontSouth().getX() - ball.getPosition().getX();
			double dyL = Math.tan(obstacleLeftAngle) * dx;
			double dyR = Math.tan(obstacleRightAngle) * dx;

			FieldPoint L = new FieldPoint((ball.getPosition().getX() + dx),
								(ball.getPosition().getY() + dyL));
			FieldPoint R = new FieldPoint((ball.getPosition().getX() + dx),
					(ball.getPosition().getY() + dyR));

			//is there a point with the same start X coordinate
			//if so, check whether it is bigger, and replace it if necessary
			if(!obstructedArea.containsKey(L.toPoint2D().getY()) ||
					(obstructedArea.get(L.toPoint2D().getY())) > R.toPoint2D().getY())
				obstructedArea.put(L.toPoint2D().getY(), R.toPoint2D().getY());
		}
		double minY = enemyGoal.getFrontSouth().getY();
		double maxY = enemyGoal.getFrontNorth().getY();
		obstructedArea = minMax(obstructedArea, minY, maxY);
		obstructedArea = mergeOverlappingValues(obstructedArea);

		TreeMap<Double, Double> availableArea = invertMap(obstructedArea);

		if(obstructedArea.firstKey() != minY)
			availableArea.put(minY, obstructedArea.firstKey());
		if(obstructedArea.lastEntry().getValue() != maxY)
			availableArea.put(obstructedArea.lastEntry().getValue(), maxY);

		double x = enemyGoal.getFrontSouth().getX();
		
		if(availableArea.size() <= 0)
			return null;
		
		//in this case size DOES matter
		Double biggestKey = availableArea.firstKey();
		for(Entry<Double, Double> entry : availableArea.entrySet())
			if(entry.getValue() - entry.getKey() > availableArea.get(biggestKey) - biggestKey)
				biggestKey = entry.getKey();

		System.out.println("Size: " + biggestKey + availableArea.get(biggestKey));
		
		//return point that lies in the center of the biggest point
		FieldPoint hitmarker = new FieldPoint(x, (biggestKey/2 + availableArea.get(biggestKey)/2));
		return hitmarker;
	}

	/**
	 * Ensures all values are within given limits
	 * @param map	map that has to be processed
	 * @param min	minimum value
	 * @param max	maximum value
	 * @return map that honorers limits
	 */
	private TreeMap<Double, Double> minMax(
			TreeMap<Double, Double> map, double min, double max) {
		TreeMap<Double, Double> newMap = new TreeMap<Double, Double>();
		for(Entry<Double, Double> entry : map.entrySet()){
			Double newKey, newValue;
			newKey = Math.min(Math.max(entry.getKey(), min), max);
			newValue = Math.min(Math.max(entry.getValue(), min), max);
			newMap.put(newKey, newValue);
		}
		return newMap;
	}

	/**
	 * "inverts" a TreeMap,
	 * example:		 Y1 | Y2
	 * 				 16 | 100
	 * 				120 | 130
	 * returns:		 Y1 | Y2
	 * 				100 | 120 
	 * @param map
	 * @return
	 */
	private TreeMap<Double, Double> invertMap(
			TreeMap<Double, Double> map) {
		TreeMap<Double, Double> invertedMap = new TreeMap<Double, Double>();
		Double prevY1 = null,
				prevY2 = null;
		for(Entry<Double, Double> entry : map.entrySet()){
			if(prevY1 == null){
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
	 * Merges a TreeMap that has overlapping values,
	 * example	Y1 | Y2
	 * 			16 | 100
	 * 			80 | 130
	 * will return:   Y1 | Y2
	 * 				  16 | 130
	 * @param map to be processed
	 * @return processed map
	 */
    private TreeMap<Double, Double> mergeOverlappingValues(TreeMap<Double, Double> map){
		TreeMap<Double, Double> mergedMap = new TreeMap<Double, Double>();
		Double prevY1 = null,
				prevY2 = null;
		for(Entry<Double, Double> entry : map.entrySet()){
			if(prevY1 == null){
				prevY1 = entry.getKey();
				prevY2 = entry.getValue();
				continue;
			}
			Double Y1 = entry.getKey();
			Double Y2 = entry.getValue();
			if(prevY2 >= Y1){
				//meergeeee
				prevY1 = (prevY1 > Y1 ? Y1 : prevY1);
				prevY2 = (prevY2 > Y2 ? prevY2 : Y2);
			}else {
				mergedMap.put((prevY1 < prevY2 ? prevY1 : prevY2), (prevY1 > prevY2 ? prevY1 : prevY2));
				//no merge
				prevY1 = entry.getKey();
				prevY2 = entry.getValue();
			}
		}
		mergedMap.put((prevY1 < prevY2 ? prevY1 : prevY2), (prevY1 > prevY2 ? prevY1 : prevY2));
		return mergedMap;
    }
	/**
	 * Find a RobotExecuter based on robot id
	 * @param robotId the robotid
	 * @param executers a list containing all the executers
	 * @return RobotExecuter belonging to a robot with id robotId
	 */
	protected RobotExecuter findExecuter(int robotId, ArrayList<RobotExecuter> executers) {
		for (RobotExecuter r : executers) {
			if (r.getRobot().getRobotId() == robotId)
				return r;
		}
		return null;
	}
}
