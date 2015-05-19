package robocup.controller.ai.highLevelBehavior.strategy.standard;

import org.apache.commons.math3.util.Pair;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.World;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

public class PenaltyDefense extends Strategy {

	public PenaltyDefense() {
		super();
		roles.add(RobotMode.PENALTYKEEPER);
		roles.add(RobotMode.COUNTER);
		roles.add(RobotMode.RUNNER);
		roles.add(RobotMode.RUNNER);
		roles.add(RobotMode.RUNNER);
		roles.add(RobotMode.RUNNER);
	}

	@Override
	public void updateZones(FieldPoint ballPosition) {
		super.updateZones(ballPosition);

		boolean isEastTeam = World.getInstance().getReferee().getEastTeam()
				.equals(World.getInstance().getReferee().getAlly());

		if (isEastTeam) {
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COUNTER, FieldZone.WEST_MIDDLE));
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.EAST_NORTH_SECONDPOST));
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.EAST_SOUTH_SECONDPOST));
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.EAST_NORTH_FRONT));
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.EAST_SOUTH_FRONT));
		} else {
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COUNTER, FieldZone.EAST_MIDDLE));
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.WEST_NORTH_SECONDPOST));
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.WEST_SOUTH_SECONDPOST));
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.WEST_NORTH_FRONT));
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.RUNNER, FieldZone.WEST_SOUTH_FRONT));
		}
	}
}
