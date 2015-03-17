package robocup.controller.ai.highLevelBehavior.zoneBehavior;

import java.util.ArrayList;
import java.util.Arrays;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
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
import robocup.model.Robot;
import robocup.model.World;
import robocup.model.Zone;
import robocup.model.enums.FieldZone;
import robocup.output.ComInterface;
import robocup.output.RobotCom;

public abstract class Mode {

	protected World world;
	protected Ball ball;
	protected Strategy strategy;

	/** Co-ordinates of the goal on the left side of the field */
	private static final Point MID_GOAL_NEGATIVE = new Point(-(World.getInstance().getField().getLength() / 2), 0);
	/** Co-ordinates of the goal on the right side of the field */
	private static final Point MID_GOAL_POSITIVE = new Point(World.getInstance().getField().getLength() / 2, 0);

	public Mode(Strategy strategy) {
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
	 * Set the roles for all executers based on current strategy and mode.
	 */
	protected abstract void setRoles(ArrayList<RobotExecuter> executers);

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
