package robocup.controller.ai.highLevelBehavior.strategy.attack;

import org.apache.commons.math3.util.Pair;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.World;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

/**
 * {@link FreeShotRoundPlay} is an attack strategy that is used to create a free shot via
 *  passing round between 3 {@link RobotMode#ATTACKER}s. When a {@link RobotMode#ATTACKER}
 *  has the ball, he shoots when there is a free shot, otherwise passes to one of the other
 *  {@link RobotMode#ATTACKER}s. When there is no option to shoot or to pass, the ball
 *  possessing robot starts to dribble.
 * <br>
 * For defense during the attack, a {@link RobotMode#KEEPER} and 2 {@link RobotMode#KEEPERDEFENDERS}
 * are used.
 * <br><br>
 * <img src="../../../../../../../images/freeShotRoundPlay.jpg" />
 * <br><br>
 * For more information about the strategy and roles see TactiekDocument
 */
public class FreeShotRoundPlay extends Strategy {

	/**
	 * Roles in the {@link FreeShotRoundPlay} strategy are assigned in the following order:<br>
	 * <ol>
	 * <li>{@link RobotMode#KEEPER}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDERS}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDERS}</li>
	 * <li>{@link RobotMode#ATTACKER}</li>
	 * <li>{@link RobotMode#ATTACKER}</li>
	 * <li>{@link RobotMode#ATTACKER}</li>
	 * </ol>
	 */
	public FreeShotRoundPlay() {
		super();
		roles.add(RobotMode.KEEPER);
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);

		roles.add(RobotMode.ATTACKER);
		roles.add(RobotMode.ATTACKER);
		roles.add(RobotMode.ATTACKER);
	}

	@Override
	public void updateZones(FieldPoint ballPosition) {
		if (World.getInstance().getReferee().getAlly().equals(World.getInstance().getReferee().getEastTeam())) {
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.ATTACKER, FieldZone.WEST_CENTER));
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.ATTACKER, FieldZone.WEST_NORTH_SECONDPOST));
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.ATTACKER, FieldZone.WEST_SOUTH_SECONDPOST));
		} else {
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.ATTACKER, FieldZone.EAST_CENTER));
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.ATTACKER, FieldZone.EAST_NORTH_SECONDPOST));
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.ATTACKER, FieldZone.EAST_SOUTH_SECONDPOST));
		}
	}
}
