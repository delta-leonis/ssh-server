package robocup.controller.ai.highLevelBehavior.strategy.standard;

import org.apache.commons.math3.util.Pair;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.World;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

/**
 * {@link PenaltyAttack} is a standard strategy that is used when our team can take a penalty shootout.
 * One robot stands near the ball to shoot it {@link RobotMode#ATTACKER} and 2 {@link RobotMode#RUNNER}s 
 * stand on the edge of the allowed playfield near the enemy goal (to retrieve the ball if the penalty misses).
 * There are also the standard defensive roles, consisting of a {@link RobotMode#KEEPER} and 2 {@link RobotMode#KEEPERDEFENDER}s.
 * <br><br>
 * <img src="../../../../../../../images/situationPenalty.png" />
 * <br><br>
 * For more information about the strategy and roles see TactiekDocument
 */
public class PenaltyAttack extends Strategy {

	/**
	 * Roles in the {@link PenaltyAttack} strategy are assigned in the following order:<br>
	 * <ol>
	 * <li>{@link RobotMode#KEEPER}</li>
	 * <li>{@link RobotMode#ATTACKER}</li>
	 * <li>{@link RobotMode#RUNNER}</li>
	 * <li>{@link RobotMode#RUNNER}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDER}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDER}</li>
	 * </ol>
	 */
	public PenaltyAttack() {
		super();
		roles.add(RobotMode.KEEPER);
		// the robot that takes the penalty, the referee needs to be notified about this robot 
		roles.add(RobotMode.ATTACKER);
		// Try to stand as near to the enemy goal as is allowed, to get the ball if the penalty is blocked
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
		super.updateZones(ballPosition);

		boolean isEastTeam = World.getInstance().getReferee().getEastTeam()
				.equals(World.getInstance().getReferee().getAlly());

		if (isEastTeam) {
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.ATTACKER, FieldZone.WEST_NORTH_GOAL));
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.WEST_NORTH_FRONT));
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.WEST_SOUTH_FRONT));
		} else {
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.ATTACKER, FieldZone.EAST_NORTH_GOAL));
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.EAST_NORTH_FRONT));
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.EAST_SOUTH_FRONT));
		}
	}
}
