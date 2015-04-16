package robocup.controller.ai.highLevelBehavior.strategy.standard;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.World;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

/**
 * {@link PenaltyEnemy} is a standard strategy that is used when the enemy team can take a penalty shootout.
 * One {@link RobotMode#COUNTER} stands near the the center of the field for an eventual counter attack.
 * The other robots are {@link RobotMode#RUNNER}s that stand on the edge of the allowed playfield near our goal area to retrieve the ball if the penalty misses.
 * Finally there is the keeper who tries to block the penalty {@link RobotMode#KEEPER}.
 * <br><br>
 * <img src="../../../../../../../images/situationPenalty.png" />
 * <br><br>
 * For more information about the strategy and roles see TactiekDocument
 */
public class PenaltyEnemy extends Strategy {
	/**
	 * Roles in the {@link PenaltyEnemy} strategy are assigned in the following order:<br>
	 * <ol>
	 * <li>{@link RobotMode#KEEPER}</li>
	 * <li>{@link RobotMode#RUNNER}</li>
	 * <li>{@link RobotMode#RUNNER}</li>
	 * <li>{@link RobotMode#RUNNER}</li>
	 * <li>{@link RobotMode#RUNNER}</li>
	 * <li>{@link RobotMode#COUNTER}</li>
	 * </ol>
	 */
	public PenaltyEnemy () {
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
		if(World.getInstance().getReferee().getAlly().equals(World.getInstance().getReferee().getEastTeam())) {
			zonesForRole.put(RobotMode.COUNTER, FieldZone.WEST_MIDDLE);
		} else {
			zonesForRole.put(RobotMode.COUNTER, FieldZone.EAST_MIDDLE);
		}	
	}
}