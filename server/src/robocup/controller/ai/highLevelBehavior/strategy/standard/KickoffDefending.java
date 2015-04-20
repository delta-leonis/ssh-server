package robocup.controller.ai.highLevelBehavior.strategy.standard;

import org.apache.commons.math3.util.Pair;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.World;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

/**
 * {@link KickoffDefending} is a standard strategy that is used when the opponent has a kickoff.
 * One robot stands near the ball to cover the goal {@link RobotMode#COVERER} and 2 {@link RobotMode#COVERER}s 
 * try to stand between the most prominent opposing attackers and our goal area.
 * There are also the standard defensive roles, consisting of a {@link RobotMode#KEEPER} and 2 {@link RobotMode#KEEPERDEFENDER}s.
 * <br><br>
 * <img src="../../../../../../../images/situationKickoff.png" />
 * <br><br>
 * For more information about the strategy and roles see TactiekDocument
 */
public class KickoffDefending extends Strategy {

	/**
	 * Roles in the {@link KickoffDefending} strategy are assigned in the following order:<br>
	 * <ol>
	 * <li>{@link RobotMode#KEEPER}</li>
	 * <li>{@link RobotMode#COVERER}</li>
	 * <li>{@link RobotMode#COVERER}</li>
	 * <li>{@link RobotMode#COVERER}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDER}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDER}</li>
	 * </ol>
	 */
	public KickoffDefending() {
		super();
		roles.add(RobotMode.KEEPER);
		// Center robot coverer
		roles.add(RobotMode.COVERER);
		// Top side field coverer
		roles.add(RobotMode.COVERER);
		// Bottom side field coverer
		roles.add(RobotMode.COVERER);

		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);
	}

	/**
	 * Method that declares the zones for all the roles in this strategy
	 */
	@Override
	public void updateZones(FieldPoint ballPosition) {
		if (World.getInstance().getReferee().getAlly().equals(World.getInstance().getReferee().getEastTeam())) {
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.EAST_NORTH_FRONT));
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.EAST_SOUTH_FRONT));
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.EAST_MIDDLE));
		} else {
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.WEST_SOUTH_FRONT));
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.WEST_SOUTH_FRONT));
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.WEST_MIDDLE));
		}
	}
}
