package robocup.controller.ai.highLevelBehavior.strategy.defense;

import org.apache.commons.math3.util.Pair;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.World;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

/**
 * {@link BarricadeDefending} is an defense strategy where the ball possessing opponent
 * is disturbed by a {@link RobotMode#DISTURBER} that stands in front of him.
 * a {@link RobotMode#GOALPOSTCOVERER} is used to prevent goals shot from the second post.
 * 2 {@link RobotMode#KEEPERDEFENDER}s and a {@link RobotMode#KEEPER} keep blocking the line
 * of shot.
 * <br>
 * For the possibility to start up a counter attack, a {@link RobotMode#COUNTER} is
 * used.
 * <br><br>
 * <img src="../../../../../../../images/barricadeDefending.jpg" />
 * <br><br>
 * For more information about the strategy and roles see TactiekDocument
 */
public class BarricadeDefending extends Strategy {

	/**
	 * Roles in the {@link BarricadeDefending} strategy are assigned in the following order:<br>
	 * <ol>
	 * <li>{@link RobotMode#KEEPER}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDERS}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDERS}</li>
	 * <li>{@link RobotMode#COUNTER}</li>
	 * <li>{@link RobotMode#GOALPOSTCOVERER}</li>
	 * <li>{@link RobotMode#DISTURBER}</li>
	 * </ol>
	 */
	public BarricadeDefending() {
		super();
		roles.add(RobotMode.KEEPER);
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);

//		roles.add(RobotMode.COUNTER);
//		roles.add(RobotMode.GOALPOSTCOVERER);
		roles.add(RobotMode.DISTURBER);
	}

	@Override
	public void updateZones(FieldPoint ballPosition) {
		super.updateZones(ballPosition);

		if (World.getInstance().getReferee().getAlly().equals(World.getInstance().getReferee().getEastTeam())) {
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COUNTER, FieldZone.WEST_CENTER));

			if (ballPosition.getY() <= 0.0)
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.GOALPOSTCOVERER,
						FieldZone.EAST_SOUTH_SECONDPOST));
			else
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.GOALPOSTCOVERER,
						FieldZone.EAST_NORTH_SECONDPOST));
		} else {
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COUNTER, FieldZone.EAST_CENTER));

			if (ballPosition.getY() <= 0.0)
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.GOALPOSTCOVERER,
						FieldZone.EAST_SOUTH_SECONDPOST));
			else
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.GOALPOSTCOVERER,
						FieldZone.EAST_NORTH_SECONDPOST));
		}
	}
}
