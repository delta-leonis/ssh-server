package robocup.controller.ai.highLevelBehavior.strategy.standard;

import org.apache.commons.math3.util.Pair;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.World;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

/**
 * {@link PenaltyDefense} is a standard strategy that is used when the enemy team can take a penalty shootout.
 * One {@link RobotMode#COUNTER} stands near the the center of the field for an eventual counter attack.
 * The other robots are {@link RobotMode#RUNNER}s that stand on the edge of the allowed playfield near our goal area to retrieve the ball if the penalty misses.
 * Finally there is the keeper who tries to block the penalty {@link RobotMode#KEEPER}.
 * <br><br>
 * <img src="../../../../../../../images/situationPenalty.png" />
 * <br><br>
 * For more information about the strategy and roles see TactiekDocument
 */
public class PenaltyDefense extends Strategy {

	/**
	 * Roles in the {@link PenaltyDefense} strategy are assigned in the following order:<br>
	 * <ol>
	 * <li>{@link RobotMode#KEEPER}</li>
	 * <li>{@link RobotMode#RUNNER}</li>
	 * <li>{@link RobotMode#RUNNER}</li>
	 * <li>{@link RobotMode#RUNNER}</li>
	 * <li>{@link RobotMode#RUNNER}</li>
	 * <li>{@link RobotMode#COUNTER}</li>
	 * </ol>
	 */
	public PenaltyDefense() {
		super();
		roles.add(RobotMode.KEEPER);
		// create a wall to regain the ball after a failed penalty
		roles.add(RobotMode.RUNNER);
		roles.add(RobotMode.RUNNER);
		roles.add(RobotMode.RUNNER);
		roles.add(RobotMode.RUNNER);

		// stands in a position for the counter attack
		roles.add(RobotMode.COUNTER);
	}

	/**
	 * Method that declares the zones for all the roles in this strategy
	 */
	@Override
	public void updateZones(FieldPoint ballPosition) {
		super.updateZones(ballPosition);

		if (World.getInstance().getReferee().getAlly().equals(World.getInstance().getReferee().getEastTeam())) {
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COUNTER, FieldZone.WEST_MIDDLE));
		} else {
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COUNTER, FieldZone.EAST_MIDDLE));
		}
	}
}
