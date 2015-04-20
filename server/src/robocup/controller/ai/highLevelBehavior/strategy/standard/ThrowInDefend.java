package robocup.controller.ai.highLevelBehavior.strategy.standard;

import org.apache.commons.math3.util.Pair;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.World;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

/**
 * {@link ThrowInDefend} is a standard strategy that is used when the enemy team may throw the ball in.
 * One {@link RobotMode#COVERER} stands near the enemy throwing the ball in, blocking the way to our goal.
 * A {@link RobotMode#DISTURBER} covers the most dangerous robot of the opposing team.
 * There are also the standard defensive roles, consisting of a {@link RobotMode#KEEPER} and 2 {@link RobotMode#KEEPERDEFENDER}s.
 * An {@link RobotMode#GOALPOSTCOVERER} blocks off the opposing side of our goal against a one-two attack.
 * <br><br>
 * <img src="../../../../../../../images/situationThrowIn.png" />
 * <br><br>
 * For more information about the strategy and roles see TactiekDocument
 */
public class ThrowInDefend extends Strategy {

	/**
	 * Roles in the {@link ThrowInDefend} strategy are assigned in the following order:<br>
	 * <ol>
	 * <li>{@link RobotMode#KEEPER}</li>
	 * <li>{@link RobotMode#GOALPOSTCOVERER}</li>
	 * <li>{@link RobotMode#DISTURBER}</li>
	 * <li>{@link RobotMode#COVERER}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDER}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDER}</li>
	 * </ol>
	 */
	public ThrowInDefend() {
		super();
		roles.add(RobotMode.KEEPER);
		// Second pole defender
		roles.add(RobotMode.GOALPOSTCOVERER);
		// Covers the biggest theat of the enemy
		roles.add(RobotMode.DISTURBER);
		// covers the enemy that may kick the ball
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
			if (ballPosition.getY() <= 0.0) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.GOALPOSTCOVERER,
						FieldZone.EAST_SOUTH_SECONDPOST));
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.DISTURBER, FieldZone.EAST_SOUTH_FRONT));
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.EAST_NORTH_FRONT));
			} else {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.GOALPOSTCOVERER,
						FieldZone.EAST_NORTH_SECONDPOST));
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.DISTURBER, FieldZone.EAST_NORTH_FRONT));
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.EAST_SOUTH_FRONT));
			}
		} else {
			if (ballPosition.getY() <= 0.0) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.GOALPOSTCOVERER,
						FieldZone.WEST_SOUTH_SECONDPOST));
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.DISTURBER, FieldZone.WEST_SOUTH_FRONT));
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.WEST_NORTH_FRONT));
			} else {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.GOALPOSTCOVERER,
						FieldZone.WEST_NORTH_SECONDPOST));
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.DISTURBER, FieldZone.WEST_NORTH_FRONT));
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.WEST_SOUTH_FRONT));
			}
		}
	}

}
