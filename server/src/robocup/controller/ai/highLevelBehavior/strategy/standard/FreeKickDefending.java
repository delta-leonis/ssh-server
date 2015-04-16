package robocup.controller.ai.highLevelBehavior.strategy.standard;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.World;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

/**
 * {@link FreeKickDefending} is a standard strategy that is used when a free kick is assigned to the opposing team.
 * The shot line of the enemy is blocked by a {@link RobotMode#KEEPER} and 2 {@link RobotMode#KEEPERDEFENDER}s.
 * The open (other) side of the goal is blocked by the {@link RobotMode#GOALPOSTCOVERER}.
 * A {@link RobotMode#COVERER} stands between the robot that kicks the ball and the goal, close to the opposing robot.
 * Finally a {@link RobotMode#DISTURBER} blocks robots on the other side of the vertical axis, blocking any attack from the other side
 * <br><br>
 * <img src="../../../../../../../images/situationAtttacksHalf.png" />
 * <br><br>
 * For more information about the strategy and roles see TactiekDocument
 */
public class FreeKickDefending extends Strategy {
	/**
	 * Roles in the {@link FreeKickDefending} strategy are assigned in the following order:<br>
	 * <ol>
	 * <li>{@link RobotMode#KEEPER}</li>
	 * <li>{@link RobotMode#GOALPOSTCOVERER}</li>
	 * <li>{@link RobotMode#DISTURBER}</li>
	 * <li>{@link RobotMode#COVERER}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDER}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDER}</li>
	 * </ol>
	 */
	public FreeKickDefending () {
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
		if(World.getInstance().getReferee().getAlly().equals(World.getInstance().getReferee().getEastTeam())) {
			if(ballPosition.getY() <= 0.0) {
				zonesForRole.put(RobotMode.GOALPOSTCOVERER, FieldZone.EAST_SOUTH_SECONDPOST);
				zonesForRole.put(RobotMode.DISTURBER, FieldZone.EAST_SOUTH_FRONT);
				zonesForRole.put(RobotMode.COVERER, FieldZone.EAST_NORTH_FRONT);
			} else {
				zonesForRole.put(RobotMode.GOALPOSTCOVERER, FieldZone.EAST_NORTH_SECONDPOST);
				zonesForRole.put(RobotMode.DISTURBER, FieldZone.EAST_NORTH_FRONT);
				zonesForRole.put(RobotMode.COVERER, FieldZone.EAST_SOUTH_FRONT);
			}
		} else {
			if(ballPosition.getY() <= 0.0) {
				zonesForRole.put(RobotMode.GOALPOSTCOVERER, FieldZone.WEST_SOUTH_SECONDPOST);
				zonesForRole.put(RobotMode.DISTURBER, FieldZone.WEST_SOUTH_FRONT);
				zonesForRole.put(RobotMode.COVERER, FieldZone.WEST_NORTH_FRONT);
			} else {
				zonesForRole.put(RobotMode.GOALPOSTCOVERER, FieldZone.WEST_NORTH_SECONDPOST);
				zonesForRole.put(RobotMode.DISTURBER, FieldZone.WEST_NORTH_FRONT);
				zonesForRole.put(RobotMode.COVERER, FieldZone.WEST_SOUTH_FRONT);
			}
		}
	}
}