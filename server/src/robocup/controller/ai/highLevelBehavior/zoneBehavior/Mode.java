package robocup.controller.ai.highLevelBehavior.zoneBehavior;

import java.util.ArrayList;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.controller.ai.lowLevelBehavior.Attacker;
import robocup.controller.ai.lowLevelBehavior.Coverer;
import robocup.controller.ai.lowLevelBehavior.Keeper;
import robocup.controller.ai.lowLevelBehavior.KeeperDefender;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.Ally;
import robocup.model.Ball;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.World;
import robocup.output.ComInterface;

public abstract class Mode {

	protected World world;
	protected Ball ball;
	protected Strategy strategy;

	/** Co-ordinates of the goal on the left side of the field */
	private static final FieldPoint MID_GOAL_NEGATIVE = new FieldPoint(-(World.getInstance().getField().getHeight() / 2), 0);
	/** Co-ordinates of the goal on the right side of the field */
	private static final FieldPoint MID_GOAL_POSITIVE = new FieldPoint(World.getInstance().getField().getHeight() / 2, 0);

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
	 * TODO: Rename to updateRoles()?
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
			executer.setLowLevelBehavior(new Attacker(executer.getRobot(), ComInterface.getInstance(),
					0.0, 0, null, null));

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
			executer.setLowLevelBehavior(new Coverer(executer.getRobot(), ComInterface.getInstance(),
					250, null, null, 0));

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
					.getInstance(), 1200, false, null, null, null));

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
			executer.setLowLevelBehavior(new Keeper(keeper, ComInterface.getInstance(), 500, false, ball
					.getPosition(), keeper.getPosition().getX() < 0 ? MID_GOAL_NEGATIVE : MID_GOAL_POSITIVE));

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
