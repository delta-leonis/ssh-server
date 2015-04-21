package robocup.controller.ai.highLevelBehavior.strategy.defense;

import org.apache.commons.math3.util.Pair;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.World;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

/**
 * {@link ForwardDefending} is an defense strategy that is used to force success from a
 * counter attack. The shot line of the enemy is blocked by a {@link RobotMode#KEEPER}
 * and 2 {@link RobotMode#KEEPERDEFENDER}s. A {@link RobotMode#COUNTER} is waiting for
 * the ball to come. 2 {@link RobotMode#COVERER}s block the passing lines to enemy
 * robots.
 * <br><br>
 * <img src="../../../../../../../images/forwardDefending.jpg" />
 * <br><br>
 * For more information about the strategy and roles see TactiekDocument
 */
public class ForwardDefending extends Strategy {

	/**
	 * Roles in the {@link ForwardDefending} strategy are assigned in the following order:<br>
	 * <ol>
	 * <li>{@link RobotMode#KEEPER}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDERS}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDERS}</li>
	 * <li>{@link RobotMode#COUNTER}</li>
	 * <li>{@link RobotMode#COVERER}</li>
	 * <li>{@link RobotMode#COVERER}</li>
	 * </ol>
	 */
	public ForwardDefending() {
		super();
		roles.add(RobotMode.KEEPER);
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);

		roles.add(RobotMode.COUNTER);
		roles.add(RobotMode.COVERER);
		roles.add(RobotMode.COVERER);
	}

	@Override
	public void updateZones(FieldPoint ballPosition) {
		super.updateZones(ballPosition);

		if (World.getInstance().getReferee().getAlly().equals(World.getInstance().getReferee().getEastTeam())) {
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COUNTER, FieldZone.WEST_CENTER));
		} else {
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COUNTER, FieldZone.EAST_CENTER));
		}
	}
}
