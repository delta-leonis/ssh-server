package robocup.controller.ai.highLevelBehavior.zoneBehavior;

import java.util.ArrayList;
import java.util.Arrays;

import robocup.controller.ai.lowLevelBehavior.Attacker;
import robocup.controller.ai.lowLevelBehavior.Coverer;
import robocup.controller.ai.lowLevelBehavior.Keeper;
import robocup.controller.ai.lowLevelBehavior.KeeperDefender;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.Ally;
import robocup.model.Ball;
import robocup.model.Enemy;
import robocup.model.FieldObject;
import robocup.model.Point;
import robocup.model.World;
import robocup.model.Zone;
import robocup.model.enums.FieldZone;
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
	protected abstract RobotMode determineRole(RobotExecuter executer);

	/**
	 * Update an executer.
	 * A role will be assigned to the robot belonging to this executer.
	 * A new lowlevel behavior will be created if the role is different from the previous role.
	 * The lowlevel behavior will be updated as well.
	 * @param executer the executer to update
	 * @param role the role which needs to be assigned to the robot belonging to the executer
	 */
	private void updateExecuter(RobotExecuter executer, RobotMode role) {
		// Set Role for Robot
		((Ally) executer.getRobot()).setRole(role);

		// Execute handle functions based on role
		switch (role) {
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
			System.out.println("Unknown role in Mode, role: " + role);
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
		if (!(executer.getLowLevelBehavior() instanceof Keeper))
			executer.setLowLevelBehavior(new Keeper(executer.getRobot(), ComInterface.getInstance(RobotCom.class), 500,
					false, null, null, null, 0));

		updateKeeper(executer);
	}

	/**
	 * Update the values of the Keeper behavior belonging to the executer.
	 * @param executer the executer to update
	 */
	protected abstract void updateKeeper(RobotExecuter executer);
	
	
	/**
	 * Checks whether a ally has a free shot, will only be checked
	 * if the robot is in one of the 6 center zones (due to accuracy)
	 * 
	 * @param executer
	 * @return
	 */
	public Point hasFreeShot(RobotExecuter executer){
		//only proceed when we are the ballowner
		if(ball.getOwner() instanceof Enemy)
			return null;
		
		FieldZone[] zones = {FieldZone.EAST_CENTER, FieldZone.EAST_LEFT_FRONT, FieldZone.EAST_MIDDLE, FieldZone.EAST_LEFT_SECOND_POST, 
							FieldZone.WEST_CENTER, FieldZone.WEST_LEFT_FRONT, FieldZone.WEST_MIDDLE, FieldZone.WEST_LEFT_SECOND_POST};
		
		//check if the ball is in a zone from which we can actually make the angle
		if(!Arrays.asList(zones).contains(getZoneByObject(ball)))
			return null;

		//World.getInstance().getField().get
		

		return null;
	}
	
	/**
	 * TODO MOVE TO MODEL
	 * Jasper will move this to the model when he will finishes the Zone implementation
	 * 
	 *  Method that returns the Zone in which a given FieldObject is localized
	 *  
	 * @param obj	field object
	 * @return	zone that contains given object 
	 */
	public Zone getZoneByObject(FieldObject obj){
//		for(Zone zone : World.getInstance().getZones())
//			if(zone.check(obj))
//				return zone;
		return null;
	}
}
