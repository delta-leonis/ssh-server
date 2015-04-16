package robocup.controller.ai.highLevelBehavior.strategy.standard;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.World;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

/**
 * {@link KickoffPrepare} is a standard strategy that is used when our team can kickoff.
 * One robot stands near the ball to shoot it {@link RobotMode#ATTACKER} and 2 {@link RobotMode#RUNNER}s 
 * stand on both sides of the kicking robot, trying to create an opening.
 * There are also the standard defensive roles, consisting of a {@link RobotMode#KEEPER} and 2 {@link RobotMode#KEEPERDEFENDER}s.
 * <br><br>
 * <img src="../../../../../../../images/situationKickoff.png" />
 * <br><br>
 * For more information about the strategy and roles see TactiekDocument
 */
public class KickoffPrepare extends Strategy {
	/**
	 * Roles in the {@link KickoffPrepare} strategy are assigned in the following order:<br>
	 * <ol>
	 * <li>{@link RobotMode#KEEPER}</li>
	 * <li>{@link RobotMode#ATTACKER}</li>
	 * <li>{@link RobotMode#RUNNER}</li>
	 * <li>{@link RobotMode#RUNNER}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDER}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDER}</li>
	 * </ol>
	 */
	public KickoffPrepare () {
		super();
		roles.add(RobotMode.KEEPER);
		
		roles.add(RobotMode.ATTACKER);
		roles.add(RobotMode.RUNNER);
		roles.add(RobotMode.RUNNER);
		
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);
	}
	
	/**
	 * Method that declares the zones for all the roles in this strategy
	 */
	@Override
	public void updateZones(FieldPoint ballPosition) {
		if(World.getInstance().getReferee().getAlly().equals(World.getInstance().getReferee().getEastTeam())) {
			zonesForRole.put(RobotMode.RUNNER, FieldZone.EAST_NORTH_FRONT);
			zonesForRole.put(RobotMode.RUNNER, FieldZone.EAST_SOUTH_FRONT);
			zonesForRole.put(RobotMode.ATTACKER, FieldZone.EAST_MIDDLE);
		} else {
			zonesForRole.put(RobotMode.RUNNER, FieldZone.WEST_SOUTH_FRONT);
			zonesForRole.put(RobotMode.RUNNER, FieldZone.WEST_SOUTH_FRONT);
			zonesForRole.put(RobotMode.ATTACKER, FieldZone.WEST_MIDDLE);
		}
	}

}
