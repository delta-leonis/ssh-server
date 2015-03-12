package robocup.controller.ai.highLevelBehavior.zoneBehavior;

import java.util.ArrayList;

import robocup.controller.ai.lowLevelBehavior.Attacker;
import robocup.controller.ai.lowLevelBehavior.Keeper;
import robocup.controller.ai.lowLevelBehavior.KeeperDefender;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.controller.ai.lowLevelBehavior.Stoorder;
import robocup.model.Ally;
import robocup.model.Ball;
import robocup.model.World;
import robocup.model.enums.RobotMode;
import robocup.output.ComInterface;
import robocup.output.RobotCom;

public abstract class Mode {

	protected World world;
	protected Ball ball;

	public Mode(ArrayList<RobotExecuter> executers) {
		world = World.getInstance();
		ball = world.getBall();
	}

	/**
	 * Execute the mode.
	 * New roles will be assigned to every robot and their lowlevel behaviors will be updated.
	 * @param executers
	 */
	public void execute(ArrayList<RobotExecuter> executers) {
		try {
			for (RobotExecuter executer : executers) {
				updateExecuter(executer, determineRole(executer));
			}
		} catch (Exception e) {
			System.out.println("Exception in Mode, please fix me :(");
			e.printStackTrace();
		}
	}

	/**
	 * Determine the role for an executer.
	 * Role will be chosen based on current strategy and mode.
	 * @return role of the robot
	 */
	public abstract RobotMode determineRole(RobotExecuter executer);

	/**
	 * Update an executer.
	 * A role will be assigned to the robot belonging to this executer.
	 * A new lowlevel behavior will be created if the role is different from the previous role.
	 * The lowlevel behavior will be updated as well.
	 * @param executer the executer to update
	 * @param role the role which needs to be assigned to the robot belonging to the executer
	 */
	public void updateExecuter(RobotExecuter executer, RobotMode role) {
		// Set Role for Robot
		((Ally) executer.getRobot()).setRole(role);

		// Execute handle functions based on role
		switch (role) {
		case ATTACKER:
			handleAttacker(executer);
			break;
		case BLOCKER:
			handleBlocker(executer);
			break;
		case DEFENDER:
			handleKeeperDefender(executer);
			break;
		case KEEPER:
			handleKeeper(executer);
			break;
		default:
			System.out.println("Unknown role in Mode, role: " + role);
		}
	}

	/**
	 * Handle the behavior of the Attacker.
	 * A new Attacker behavior will be created if the current lowlevel behavior is not an Attacker.
	 * Update the values of the Attacker afterwards.
	 * @param executer the executer which needs to be handled
	 */
	public void handleAttacker(RobotExecuter executer) {
		if (!(executer.getLowLevelBehavior() instanceof Attacker))
			executer.setLowLevelBehavior(new Attacker(executer.getRobot(), ComInterface.getInstance(RobotCom.class),
					null, null, 0, false, 0));

		updateAttacker(executer);
	}

	/**
	 * Update the values of the Attacker behavior belonging to the executer.
	 * @param executer the executer to update
	 */
	public abstract void updateAttacker(RobotExecuter executer);

	/**
	 * Handle the behavior of the Blocker.
	 * A new Blocker behavior will be created if the current lowlevel behavior is not a Blocker.
	 * Update the values of the Blocker afterwards.
	 * @param executer the executer which needs to be handled
	 */
	public void handleBlocker(RobotExecuter executer) {
		if (!(executer.getLowLevelBehavior() instanceof Stoorder))
			executer.setLowLevelBehavior(new Stoorder(executer.getRobot(), ComInterface.getInstance(RobotCom.class),
					250, null, null, null, 0));

		updateBlocker(executer);
	}

	/**
	 * Update the values of the Blocker behavior belonging to the executer.
	 * @param executer the executer to update
	 */
	public abstract void updateBlocker(RobotExecuter executer);

	/**
	 * Handle the behavior of the KeeperDefender.
	 * A new Attacker behavior will be created if the current lowlevel behavior is not a KeeperDefender.
	 * Update the values of the KeeperDefender afterwards.
	 * @param executer the executer which needs to be handled
	 */
	public void handleKeeperDefender(RobotExecuter executer) {
		if (!(executer.getLowLevelBehavior() instanceof KeeperDefender))
			executer.setLowLevelBehavior(new KeeperDefender(executer.getRobot(), ComInterface
					.getInstance(RobotCom.class), 1200, false, null, null, null, null, 0));

		updateKeeperDefender(executer);
	}

	/**
	 * Update the values of the KeeperDefender behavior belonging to the executer.
	 * @param executer the executer to update
	 */
	public abstract void updateKeeperDefender(RobotExecuter executer);

	/**
	 * Handle the behavior of the Keeper.
	 * A new Keeper behavior will be created if the current lowlevel behavior is not a Keeper.
	 * Update the values of the Keeper afterwards.
	 * @param executer the executer which needs to be handled
	 */
	public void handleKeeper(RobotExecuter executer) {
		if (!(executer.getLowLevelBehavior() instanceof Keeper))
			executer.setLowLevelBehavior(new Keeper(executer.getRobot(), ComInterface.getInstance(RobotCom.class), 500,
					false, null, null, null, 0));

		updateKeeper(executer);
	}

	/**
	 * Update the values of the Keeper behavior belonging to the executer.
	 * @param executer the executer to update
	 */
	public abstract void updateKeeper(RobotExecuter executer);
}
