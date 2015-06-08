package robocup.controller.ai.highLevelBehavior.strategy.standard;

import org.apache.commons.math3.util.Pair;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.World;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

public class DirectFreeKickDefense extends Strategy {

	public DirectFreeKickDefense() {
		super();
		roles.add(RobotMode.KEEPER);
		roles.add(RobotMode.DISTURBER);
		roles.add(RobotMode.COVERER);
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);
	}

	@Override
	public void updateZones(FieldPoint ballPosition) {
		super.updateZones(ballPosition);

		boolean isEastTeam = World.getInstance().getReferee().getEastTeam()
				.equals(World.getInstance().getReferee().getAlly());

		if (ballPosition.getY() > FieldZone.FieldPointPaletteZones.h.getY()) {
			// ball in north zone
			if (isEastTeam) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.EAST_SOUTH_FRONT));
			} else {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.WEST_SOUTH_FRONT));
			}
		} else if (ballPosition.getY() < -FieldZone.FieldPointPaletteZones.h.getY()) {
			// ball in south zone
			if (isEastTeam) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.EAST_NORTH_FRONT));
			} else {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.WEST_NORTH_FRONT));
			}
		} else {
			// ball in mid zone
			if (isEastTeam) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.EAST_MIDDLE));
			} else {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COVERER, FieldZone.WEST_MIDDLE));
			}
		}
	}
}
