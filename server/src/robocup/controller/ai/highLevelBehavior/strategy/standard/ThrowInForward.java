package robocup.controller.ai.highLevelBehavior.strategy.standard;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.World;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

/**
 * {@link ThrowInForward} is a standard strategy that is used when our team may throw the ball in.
 * One {@link RobotMode#ATTACKER} stands throws the ball in, two {@link RobotMode#RUNNER}s will try to create an opening at the top and bottom of the field.
 * There are also the standard defensive roles, consisting of a {@link RobotMode#KEEPER} and 2 {@link RobotMode#KEEPERDEFENDER}s.
 * <br><br>
 * <img src="../../../../../../../images/situationThrowIn.png" />
 * <br><br>
 * For more information about the strategy and roles see TactiekDocument
 */
public class ThrowInForward extends Strategy {
	/**
	 * Roles in the {@link ThrowInForward} strategy are assigned in the following order:<br>
	 * <ol>
	 * <li>{@link RobotMode#KEEPER}</li>
	 * <li>{@link RobotMode#ATTACKER}</li>
	 * <li>{@link RobotMode#RUNNER}</li>
	 * <li>{@link RobotMode#RUNNER}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDER}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDER}</li>
	 * </ol>
	 */
	public ThrowInForward () {
		super();
		roles.add(RobotMode.KEEPER);
		// robot taking the kickoff
		roles.add(RobotMode.ATTACKER);
		// outer left
		roles.add(RobotMode.RUNNER);
		// outer right
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
