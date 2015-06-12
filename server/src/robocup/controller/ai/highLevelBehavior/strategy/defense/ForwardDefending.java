package robocup.controller.ai.highLevelBehavior.strategy.defense;

import org.apache.commons.math3.util.Pair;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.controller.ai.lowLevelBehavior.Coverer;
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

	private World world;

	/**
	 * Roles in the {@link ForwardDefending} strategy are assigned in the following order:<br>
	 * <ol>
	 * <li>{@link RobotMode#KEEPER}</li>
	 * <li>{@link RobotMode#COUNTER}</li>
	 * <li>{@link RobotMode#COVERER}</li>
	 * <li>{@link RobotMode#COVERER}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDER}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDER}</li>
	 * </ol>
	 */
	public ForwardDefending() {
		super();
		roles.add(RobotMode.KEEPER);
		roles.add(RobotMode.COUNTER);
		roles.add(RobotMode.COVERER);
		roles.add(RobotMode.COVERER);
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);

		world = World.getInstance();
	}

	@Override
	public void updateZones(FieldPoint ballPosition) {
		super.updateZones(ballPosition);

		if (world.getReferee().getAlly().equals(world.getReferee().getEastTeam())) {
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COUNTER, FieldZone.WEST_CENTER));
		} else {
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COUNTER, FieldZone.EAST_CENTER));
		}
		// TODO: Test
		updateCoverer1();
		updateCoverer2();
	}

	/**
	 * Creates zones for a {@link Coverer} which will cover
	 * 	1 SECOND_POST
	 * 	2 CORNER
	 *  3 FRONT
	 */
	private void updateCoverer1() {
		// If we're the east team.
		if (world.getReferee().getAlly().equals(world.getReferee().getEastTeam())) {
			// Check for enemies on second post first
			if (world.getEnemyRobotsInZone(FieldZone.EAST_NORTH_SECONDPOST).size() > 0) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.EAST_NORTH_SECONDPOST));
			} else if (world.getEnemyRobotsInZone(FieldZone.EAST_SOUTH_SECONDPOST).size() > 0) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.EAST_SOUTH_SECONDPOST));
			}
			// Check for enemies on corner next
			else if (world.getEnemyRobotsInZone(FieldZone.EAST_NORTH_CORNER).size() > 0) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.EAST_NORTH_CORNER));
			} else if (world.getEnemyRobotsInZone(FieldZone.EAST_SOUTH_CORNER).size() > 0) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.EAST_SOUTH_CORNER));
			}
			// Check for enemies on front after
			else if (world.getEnemyRobotsInZone(FieldZone.EAST_NORTH_FRONT).size() > 0) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.EAST_NORTH_FRONT));
			} else if (world.getEnemyRobotsInZone(FieldZone.EAST_SOUTH_FRONT).size() > 0) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.EAST_SOUTH_FRONT));
			}
			// If we're clear
			else {
				// Stand in the second post opposite to the ball.
				if (world.getBall().getPosition().getY() <= 0) {
					zonesForRole
							.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.EAST_NORTH_SECONDPOST));
				} else {
					zonesForRole
							.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.EAST_SOUTH_SECONDPOST));
				}
			}
		} else {
			// Check for enemies on second post first
			if (world.getEnemyRobotsInZone(FieldZone.WEST_NORTH_SECONDPOST).size() > 0) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.WEST_NORTH_SECONDPOST));
			} else if (world.getEnemyRobotsInZone(FieldZone.WEST_SOUTH_SECONDPOST).size() > 0) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.WEST_SOUTH_SECONDPOST));
			}
			// Check for enemies on corner next
			else if (world.getEnemyRobotsInZone(FieldZone.WEST_NORTH_CORNER).size() > 0) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.WEST_NORTH_CORNER));
			} else if (world.getEnemyRobotsInZone(FieldZone.WEST_SOUTH_CORNER).size() > 0) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.WEST_SOUTH_CORNER));
			}
			// Check for enemies on front after
			else if (world.getEnemyRobotsInZone(FieldZone.WEST_NORTH_FRONT).size() > 0) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.WEST_NORTH_FRONT));
			} else if (world.getEnemyRobotsInZone(FieldZone.WEST_SOUTH_FRONT).size() > 0) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.WEST_SOUTH_FRONT));
			}
			// If we're clear
			else {
				// Stand in the second post opposite to the ball.
				if (world.getBall().getPosition().getY() <= 0) {
					zonesForRole
							.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.WEST_NORTH_SECONDPOST));
				} else {
					zonesForRole
							.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.WEST_SOUTH_SECONDPOST));
				}
			}
		}
	}

	/**
	 * Creates zones for a {@link Coverer} which will cover
	 * 	1 CENTER
	 * 	2 MIDDLE
	 *  3 FRONT
	 */
	private void updateCoverer2() {
		// If we're the east team.
		if (world.getReferee().getAlly().equals(world.getReferee().getEastTeam())) {
			// Check for enemies on center first
			if (world.getEnemyRobotsInZone(FieldZone.EAST_CENTER).size() > 0) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.EAST_CENTER));
			}

			// Check for enemies on middle next
			else if (world.getEnemyRobotsInZone(FieldZone.EAST_MIDDLE).size() > 0) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.EAST_MIDDLE));
			}
			// Check for enemies on front after
			else if (world.getEnemyRobotsInZone(FieldZone.EAST_NORTH_FRONT).size() > 0) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.EAST_NORTH_FRONT));
			} else if (world.getEnemyRobotsInZone(FieldZone.EAST_SOUTH_FRONT).size() > 0) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.EAST_SOUTH_FRONT));
			}
			// If we're clear
			else {
				// Going to center is priority
				if (world.getEnemyRobotsInZone(FieldZone.EAST_CENTER).size() > 0) {
					zonesForRole
							.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.EAST_NORTH_SECONDPOST));
				}
			}
		} else {
			// Check for enemies on center first
			if (world.getEnemyRobotsInZone(FieldZone.WEST_CENTER).size() > 0) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.WEST_CENTER));
			}

			// Check for enemies on middle next
			else if (world.getEnemyRobotsInZone(FieldZone.WEST_MIDDLE).size() > 0) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.WEST_MIDDLE));
			}
			// Check for enemies on front after
			else if (world.getEnemyRobotsInZone(FieldZone.WEST_NORTH_FRONT).size() > 0) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.WEST_NORTH_FRONT));
			} else if (world.getEnemyRobotsInZone(FieldZone.WEST_SOUTH_FRONT).size() > 0) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.WEST_SOUTH_FRONT));
			}
			// If we're clear
			else {
				// Going to center is priority
				if (world.getEnemyRobotsInZone(FieldZone.WEST_CENTER).size() > 0) {
					zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.WEST_CENTER));
				}
			}
		}
	}
}
