package robocup.controller.ai.highLevelBehavior.strategy.standard;

import org.apache.commons.math3.util.Pair;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

/**
 * {@link GameStop} The strategy that is selected when the game is halted.
 * Two robots stand near the ball {@link RobotMode#RUNNER} and one {@link RobotMode#COUNTER} tries to stand in an open position to receive the ball.
 * There are also the standard defensive roles, consisting of a {@link RobotMode#KEEPER} and 2 {@link RobotMode#KEEPERDEFENDER}s.
 * <br><br>
 * For more information about the strategy and roles see TactiekDocument
 */
public class GameStop extends Strategy {

	/**
	 * Roles in the {@link GameStop} strategy are assigned in the following order:<br>
	 * <ol>
	 * <li>{@link RobotMode#KEEPER}</li>
	 * <li>{@link RobotMode#RUNNER}</li>
	 * <li>{@link RobotMode#RUNNER}</li>
	 * <li>{@link RobotMode#COUNTER}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDER}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDER}</li>
	 * </ol>
	 */
	public GameStop() {
		super();
		roles.add(RobotMode.KEEPER);
		// two robots stand as close to the ball as possible, because we might have to kick off or defend
		roles.add(RobotMode.RUNNER);
		roles.add(RobotMode.RUNNER);
		// one robot stands ready for the counter attack (or to cover an enemy when they get the ball)
		roles.add(RobotMode.COUNTER);
		// two robots defend
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);
	}

	/**
	 * Method that declares the zones for all the roles in this strategy
	 */
	@Override
	public void updateZones(FieldPoint ballPosition) {
		if (ballPosition.getX() <= 0.0) {
			if (ballPosition.getY() <= 0.0) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.WEST_NORTH_FRONT));
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.WEST_NORTH_FRONT));
				// TODO counter achterin? waarom geen runner?
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COUNTER, FieldZone.WEST_SOUTH_FRONT));
			} else {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.WEST_SOUTH_FRONT));
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.WEST_SOUTH_FRONT));
				// TODO counter achterin? waarom geen runner?
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COUNTER, FieldZone.WEST_NORTH_FRONT));
			}
		} else {
			if (ballPosition.getY() <= 0.0) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.EAST_NORTH_FRONT));
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.EAST_NORTH_FRONT));
				// TODO counter achterin? waarom geen runner?
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COUNTER, FieldZone.EAST_SOUTH_FRONT));
			} else {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.EAST_SOUTH_FRONT));
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.EAST_SOUTH_FRONT));
				// TODO counter achterin? waarom geen runner?
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COUNTER, FieldZone.EAST_NORTH_FRONT));
			}
		}
	}
}
