package robocup.controller.ai.highLevelBehavior.strategy.standard;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.World;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

/**
 * {@link FreeKickForward} is a standard strategy that is used when a free kick is assigned to our team.
 * One robot stands near the ball to shoot it {@link RobotMode#ATTACKER} and 2 {@link RobotMode#RUNNERS}s try to stand in an open position to receive the ball.
 * There are also the standard defensive roles, consisting of a {@link RobotMode#KEEPER} and 2 {@link RobotMode#KEEPERDEFENDER}s.
 * <br><br>
 * <img src="../../../../../../../images/situationAtttacksHalf.png" />
 * <br><br>
 * For more information about the strategy and roles see TactiekDocument
 */
public class FreeKickForward extends Strategy {
	/**
	 * Roles in the {@link FreeKickForward} strategy are assigned in the following order:<br>
	 * <ol>
	 * <li>{@link RobotMode#KEEPER}</li>
	 * <li>{@link RobotMode#ATTACKER}</li>
	 * <li>{@link RobotMode#RUNNER}</li>
	 * <li>{@link RobotMode#RUNNER}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDER}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDER}</li>
	 * </ol>
	 */
	public FreeKickForward () {
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
			zonesForRole.put(RobotMode.RUNNER, FieldZone.WEST_SOUTH_FRONT);
			zonesForRole.put(RobotMode.RUNNER, FieldZone.WEST_NORTH_FRONT);
			if(ballPosition.getY() <= 0.0) {
				zonesForRole.put(RobotMode.ATTACKER, FieldZone.EAST_NORTH_FRONT);
			} else {
				zonesForRole.put(RobotMode.ATTACKER, FieldZone.EAST_SOUTH_FRONT);
			}
		} else {
			zonesForRole.put(RobotMode.RUNNER, FieldZone.EAST_SOUTH_FRONT);
			zonesForRole.put(RobotMode.RUNNER, FieldZone.EAST_NORTH_FRONT);
			if(ballPosition.getY() <= 0.0) {
				zonesForRole.put(RobotMode.ATTACKER, FieldZone.WEST_NORTH_FRONT);
			} else {
				zonesForRole.put(RobotMode.ATTACKER, FieldZone.WEST_SOUTH_FRONT);
			}
		}
	}
}
