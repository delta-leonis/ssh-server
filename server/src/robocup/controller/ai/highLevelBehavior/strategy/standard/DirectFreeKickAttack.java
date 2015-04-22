package robocup.controller.ai.highLevelBehavior.strategy.standard;

import org.apache.commons.math3.util.Pair;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.World;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

public class DirectFreeKickAttack extends Strategy {

	public DirectFreeKickAttack() {
		super();
		roles.add(RobotMode.KEEPER);
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.ATTACKER);
		roles.add(RobotMode.RUNNER);
		roles.add(RobotMode.RUNNER);
	}

	@Override
	public void updateZones(FieldPoint ballPosition) {
		super.updateZones(ballPosition);

		boolean isEastTeam = World.getInstance().getReferee().getEastTeam()
				.equals(World.getInstance().getReferee().getAlly());

		if (ballPosition.getY() > FieldZone.FieldPointPaletteZones.h.getY()) {
			// ball in north zone
			if (isEastTeam) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.WEST_MIDDLE));
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.WEST_SOUTH_SECONDPOST));
			} else {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.EAST_MIDDLE));
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.EAST_SOUTH_SECONDPOST));
			}
		} else if (ballPosition.getY() < -FieldZone.FieldPointPaletteZones.h.getY()) {
			// ball in south zone
			if (isEastTeam) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.WEST_MIDDLE));
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.WEST_NORTH_SECONDPOST));
			} else {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.EAST_MIDDLE));
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.EAST_SOUTH_SECONDPOST));
			}
		} else {
			// ball in mid zone
			if (isEastTeam) {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.WEST_NORTH_SECONDPOST));
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.WEST_SOUTH_SECONDPOST));
			} else {
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.WEST_NORTH_SECONDPOST));
				zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.EAST_SOUTH_SECONDPOST));
			}
		}
	}
}
