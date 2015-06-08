package robocup.controller.ai.highLevelBehavior.zoneBehavior;

import java.util.ArrayList;
import java.util.logging.Logger;

import robocup.Main;
import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.controller.ai.lowLevelBehavior.Attacker;
import robocup.controller.ai.lowLevelBehavior.Counter;
import robocup.controller.ai.lowLevelBehavior.Coverer;
import robocup.controller.ai.lowLevelBehavior.Disturber;
import robocup.controller.ai.lowLevelBehavior.GoalPostCoverer;
import robocup.controller.ai.lowLevelBehavior.Keeper;
import robocup.controller.ai.lowLevelBehavior.KeeperDefender;
import robocup.controller.ai.lowLevelBehavior.PenaltyKeeper;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.controller.ai.lowLevelBehavior.Runner;
import robocup.model.Ally;
import robocup.model.Ball;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.World;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

public abstract class Mode {

	protected World world;
	protected Ball ball;
	protected Strategy strategy;
	protected ArrayList<RobotExecuter> executers;
	protected Logger LOGGER = Logger.getLogger(Main.class.getName());
	
    /** Co-ordinates of the goal on the left side of the field */
    private static final FieldPoint MID_GOAL_NEGATIVE = new FieldPoint(-3000, 0);
    /** Co-ordinates of the goal on the right side of the field */
    private static final FieldPoint MID_GOAL_POSITIVE = new FieldPoint(3000, 0);


	public Mode(Strategy strategy, ArrayList<RobotExecuter> executers) {
		world = World.getInstance();
		ball = world.getBall();
		this.strategy = strategy;
		this.executers = executers;
	}

	/**
	 * Execute the mode.
	 * New roles will be assigned to every robot and their lowlevel behaviors will be updated.
	 * @param executers
	 */
	public void execute() {
		try {
			for (RobotExecuter executer : executers)
				updateExecuter(executer);

		} catch (Exception e) {
			World.getInstance().stop();
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
	public void assignRoles() {
		strategy.updateZones(ball.getPosition());

		// clear executers so we start clean
		for (RobotExecuter executer : executers) {
			((Ally) executer.getRobot()).setRole(null);
			((Ally) executer.getRobot()).setPreferredZone(null);
		}

		for (RobotMode role : strategy.getRoles()) {
			FieldZone zone = strategy.getZoneForRole(role);

			if (role == RobotMode.KEEPER || role == RobotMode.PENALTYKEEPER) {
				// Find executer belonging to the goalie and set role
				((Ally) findExecuter(world.getReferee().getAlly().getGoalie(), executers).getRobot()).setRole(role);
			} else if (zone != null) {
				Ally closestRobot = getClosestAllyToZoneWithoutRole(zone);
				if (closestRobot != null) {
					closestRobot.setRole(role);
					closestRobot.setPreferredZone(zone);
				}
			} else if((role == RobotMode.DISTURBER || role == RobotMode.ATTACKER) && getClosestAllyToBall() != null){
				getClosestAllyToBallWithoutRole().setRole(role);
			} else {
				ArrayList<Ally> allyRobots = getAllyRobotsWithoutRole();
				if (allyRobots.size() != 0) {
					Ally robot = allyRobots.get((int) (Math.random() * allyRobots.size()));
					robot.setRole(role);
				}
			}
		}
	}

	/**
	 * Update an executer.
	 * A new lowlevel behavior will be created if the role is different from the previous role.
	 * The lowlevel behavior will receive updated values.
	 * @param executer the executer to update
	 */
	private void updateExecuter(RobotExecuter executer) {
		// Execute handle functions based on role
		if (((Ally) executer.getRobot()).getRole() != null) {
			switch (((Ally) executer.getRobot()).getRole()) {
			case ATTACKER:
				handleAttacker(executer);
				break;
			case COUNTER:
				handleCounter(executer);
				break;
			case COVERER:
				handleCoverer(executer);
				break;
			case DISTURBER:
				handleDisturber(executer);
				break;
			case DISTURBER_COVERER:
				FieldZone ballZone = world.locateFieldObject(ball);
				FieldZone robotZone = world.locateFieldObject(executer.getRobot());
				if (ballZone.equals(robotZone))
					handleDisturber(executer);
				else
					handleCoverer(executer);
				break;
			case GOALPOSTCOVERER:
				handleGoalPostCoverer(executer);
				break;
			case KEEPER:
				handleKeeper(executer);
				break;
			case KEEPERDEFENDER:
				handleKeeperDefender(executer);
				break;
			case KEEPERDEFENDER_COVERER:
				if ((ball.getPosition().getY() > 0 && executer.getRobot().getPosition().getY() > 0)
						|| (ball.getPosition().getY() <= 0 && executer.getRobot().getPosition().getY() <= 0))
					handleKeeperDefender(executer);
				else
					handleCoverer(executer);
				break;
			case PENALTYKEEPER:
				handlePenaltyKeeper(executer);
				break;
			case RUNNER:
				handleRunner(executer);
				break;
			default:
				LOGGER.severe("Role used without handle function, please add me in Mode.java, role: "
						+ ((Ally) executer.getRobot()).getRole());
			}
		}
	}

	/**
	 * Handle the behavior of the Penalty Keeper.
	 * A new Penalty Keeper behavior will be created if the current lowlevel behavior is not an Penalty Keeper.
	 * Update the values of the Penalty Keeper afterwards.
	 * @param executer the executer which needs to be handled
	 */
	private void handlePenaltyKeeper(RobotExecuter executer) {
		if (!(executer.getLowLevelBehavior() instanceof PenaltyKeeper))
			executer.setLowLevelBehavior(new PenaltyKeeper(executer.getRobot(), world.getField().getLength()));
		updatePenaltyKeeper(executer);
	}

	/**
	 * Update the values of the Penalty Keeper behavior belonging to the executer.
	 * @param executer the executer to update
	 */
	protected abstract void updatePenaltyKeeper(RobotExecuter executer);

	/**
	 * Handle the behavior of the GoalPost Coverer.
	 * A new GoalPost Coverer behavior will be created if the current lowlevel behavior is not an GoalPost Coverer.
	 * Update the values of the GoalPost Coverer afterwards.
	 * @param executer the executer which needs to be handled
	 */
	private void handleGoalPostCoverer(RobotExecuter executer) {
		if (!(executer.getLowLevelBehavior() instanceof GoalPostCoverer)) {
			double XPoint = world.getReferee().getEastTeam().equals(world.getReferee().getAlly()) ? world.getField()
					.getLength() / 2 : -world.getField().getLength() / 2;
			double YPoint = ball.getPosition().getY() / Math.abs(ball.getPosition().getY())
					* world.getField().getEastGoal().getWidth() / 4 * -1;

			executer.setLowLevelBehavior(new GoalPostCoverer(executer.getRobot(), new FieldPoint(XPoint, YPoint)));
		}
		updateGoalPostCoverer(executer);
	}

	/**
	 * Update the values of the GoalPost Coverer behavior belonging to the executer.
	 * @param executer the executer to update
	 */
	protected abstract void updateGoalPostCoverer(RobotExecuter executer);

	/**
	 * Handle the behavior of the Disturber.
	 * A new Disturber behavior will be created if the current lowlevel behavior is not an Disturber.
	 * Update the values of the Disturber afterwards.
	 * @param executer the executer which needs to be handled
	 */
	private void handleDisturber(RobotExecuter executer) {
		if (!(executer.getLowLevelBehavior() instanceof Disturber)) {
			FieldPoint centerGoalPosition = world.getReferee().getEastTeam().equals(world.getReferee().getAlly()) ? MID_GOAL_POSITIVE : MID_GOAL_NEGATIVE;
			executer.setLowLevelBehavior(new Disturber(executer.getRobot(), centerGoalPosition));
		}

		updateDisturber(executer);
	}

	/**
	 * Update the values of the Disturber behavior belonging to the executer.
	 * @param executer the executer to update
	 */
	protected abstract void updateDisturber(RobotExecuter executer);

	/**
	 * Handle the behavior of the Counter.
	 * A new Counter behavior will be created if the current lowlevel behavior is not an Counter.
	 * Update the values of the Counter afterwards.
	 * @param executer the executer which needs to be handled
	 */
	private void handleCounter(RobotExecuter executer) {
		if (!(executer.getLowLevelBehavior() instanceof Counter))
			executer.setLowLevelBehavior(new Counter(executer.getRobot()));
		updateCounter(executer);
	}

	/**
	 * Update the values of the Counter behavior belonging to the executer.
	 * @param executer the executer to update
	 */
	protected abstract void updateCounter(RobotExecuter executer);

	/**
	 * Handle the behavior of the Attacker.
	 * A new Attacker behavior will be created if the current lowlevel behavior is not an Attacker.
	 * Update the values of the Attacker afterwards.
	 * @param executer the executer which needs to be handled
	 */
	private void handleAttacker(RobotExecuter executer) {
		if (!(executer.getLowLevelBehavior() instanceof Attacker))
			executer.setLowLevelBehavior(new Attacker(executer.getRobot()));

		updateAttacker(executer);
	}

	private void handleRunner(RobotExecuter executer) {
		if (!(executer.getLowLevelBehavior() instanceof Runner))
			executer.setLowLevelBehavior(new Runner(executer.getRobot()));

		updateRunner(executer);
	}

	public abstract void updateRunner(RobotExecuter executor);

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
			executer.setLowLevelBehavior(new Coverer(executer.getRobot()));

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
		if (!(executer.getLowLevelBehavior() instanceof KeeperDefender)) {
			FieldPoint centerGoalPosition = world.getReferee().getEastTeam().equals(world.getReferee().getAlly()) ? MID_GOAL_POSITIVE : MID_GOAL_NEGATIVE;
			executer.setLowLevelBehavior(new KeeperDefender(executer.getRobot(), centerGoalPosition));
		}

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
		if (!(executer.getLowLevelBehavior() instanceof Keeper)) {
			FieldPoint centerGoalPosition = world.getReferee().getEastTeam().equals(world.getReferee().getAlly()) ? MID_GOAL_POSITIVE : MID_GOAL_NEGATIVE;
			executer.setLowLevelBehavior(new Keeper(keeper, centerGoalPosition));
		}

		updateKeeper(executer);
	}

	/**
	 * Update the values of the Keeper behavior belonging to the executer.
	 * @param executer the executer to update
	 */
	protected abstract void updateKeeper(RobotExecuter executer);

	/**
	 * Get the closest robot to a zone
	 * @param zone 
	 * @return
	 */
	private Ally getClosestAllyToZoneWithoutRole(FieldZone zone) {
		double minDistance = Double.MAX_VALUE;
		Ally minDistRobot = null;

		for (Ally robot : getAllyRobotsWithoutRole()) {
			if (robot.getPosition() != null && zone != null) {
				double dist = robot.getPosition().getDeltaDistance(zone.getCenterPoint());
				if (dist < minDistance) {
					minDistance = dist;
					minDistRobot = robot;
				}
			}
		}

		return minDistRobot;
	}

	public Ally getClosestAllyToBall() {
		double minDistance = Double.MAX_VALUE;
		Ally minDistRobot = null;

		for (Ally robot : getAllyRobotsWithoutRole()) {
			if (robot.getPosition() != null) {
				double dist = robot.getPosition().getDeltaDistance(world.getBall().getPosition());

				if (dist < minDistance) {
					minDistance = dist;
					minDistRobot = robot;
				}
			}
		}

		return minDistRobot;
	}

	public Ally getClosestAllyToBallWithoutRole() {
		double minDistance = Double.MAX_VALUE;
		Ally minDistRobot = null;

		for (Ally robot : getAllyRobotsWithoutRole()) {
			if (robot.getPosition() != null) {
				double dist = robot.getPosition().getDeltaDistance(world.getBall().getPosition());

				if (dist < minDistance) {
					minDistance = dist;
					minDistRobot = robot;
				}
			}
		}

		return minDistRobot;
	}

	/**
	 * Get all Robots without a role
	 * @return ArrayList containing all Ally robots without a role
	 */
	private ArrayList<Ally> getAllyRobotsWithoutRole() {
		ArrayList<Ally> robotsWithoutRole = new ArrayList<Ally>();

		for (Robot robot : world.getReferee().getAlly().getRobotsOnSight())
			if (((Ally) robot).getRole() == null)
				robotsWithoutRole.add((Ally) robot);

		return robotsWithoutRole;
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
