package robocup.controller.ai.highLevelBehavior.strategy.defense;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

/**
 * {@link ZonallyForward} is an defense strategy based on covering important zones
 * and shot lines. 2 {@link RobotMode#DISTURBER_COVERER} are situated in the zones
 * at the left front and right front to either cover a enemy robot or disturb the
 * enemy robot with the ball. The {@link RobotMode#KEEPER} blocks the shot line
 * together with 2 {@link RobotMode#KEEPERDEFENDER}s.
 * <br>
 * For the possibility to start up a counter attack, a {@link RobotMode#COUNTER} is
 * used.
 * <br><br>
 * <img src="../../../../../../../images/zonallyForward.jpg" />
 * <br><br>
 * For more information about the strategy and roles see TactiekDocument
 */
public class ZonallyForward extends Strategy {

	/**
	 * Roles in the {@link ZonallyForward} strategy are assigned in the following order:<br>
	 * <ol>
	 * <li>{@link RobotMode#KEEPER}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDERS}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDERS}</li>
	 * <li>{@link RobotMode#COUNTER}</li>
	 * <li>{@link RobotMode#DISTURBER_COVERER}</li>
	 * <li>{@link RobotMode#DISTURBER_COVERER}</li>
	 * </ol>
	 */
	public ZonallyForward() {
		super();
		roles.add(RobotMode.KEEPER);
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);
		
		roles.add(RobotMode.COUNTER);
		
		roles.add(RobotMode.DISTURBER_COVERER);
		roles.add(RobotMode.DISTURBER_COVERER);
	}

	@Override
	public void updateZones(FieldPoint ballPosition) {
		//TODO
//		if(iets.getOwnSide() == EAST) {
			zonesForRole.put(RobotMode.DISTURBER_COVERER, FieldZone.EAST_LEFT_FRONT);
			zonesForRole.put(RobotMode.DISTURBER_COVERER, FieldZone.EAST_RIGHT_FRONT);
			zonesForRole.put(RobotMode.COUNTER, FieldZone.WEST_CENTER);
//		} else {
			zonesForRole.put(RobotMode.DISTURBER_COVERER, FieldZone.WEST_LEFT_FRONT);
			zonesForRole.put(RobotMode.DISTURBER_COVERER, FieldZone.WEST_RIGHT_FRONT);
			zonesForRole.put(RobotMode.COUNTER, FieldZone.EAST_CENTER);
//		}
	}
}