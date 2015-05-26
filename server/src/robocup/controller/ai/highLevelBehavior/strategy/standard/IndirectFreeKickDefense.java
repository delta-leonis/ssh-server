package robocup.controller.ai.highLevelBehavior.strategy.standard;

import org.apache.commons.math3.util.Pair;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.World;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

public class IndirectFreeKickDefense extends Strategy {

	public IndirectFreeKickDefense() {
		super();
		roles.add(RobotMode.KEEPER);
		roles.add(RobotMode.GOALPOSTCOVERER);
		roles.add(RobotMode.COVERER);
		roles.add(RobotMode.COVERER);
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);
	}

	@Override
	public void updateZones(FieldPoint ballPosition) {
		super.updateZones(ballPosition);

		boolean isEastTeam = World.getInstance().getReferee().getEastTeam()
				.equals(World.getInstance().getReferee().getAlly());

		if (isEastTeam) {
			if (World.getInstance().getBall().getPosition().getY() > 0) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.GOALPOSTCOVERER,
						FieldZone.EAST_SOUTH_SECONDPOST));
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.EAST_NORTH_SECONDPOST));
			} else {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.GOALPOSTCOVERER,
						FieldZone.EAST_NORTH_SECONDPOST));
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.EAST_NORTH_SECONDPOST));
			}

			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.EAST_MIDDLE));
		} else {
			if (World.getInstance().getBall().getPosition().getY() > 0) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.GOALPOSTCOVERER,
						FieldZone.WEST_SOUTH_SECONDPOST));
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.EAST_NORTH_SECONDPOST));
			} else {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.GOALPOSTCOVERER,
						FieldZone.WEST_NORTH_SECONDPOST));
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.EAST_SOUTH_SECONDPOST));
			}

			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.WEST_MIDDLE));
		}
	}
}