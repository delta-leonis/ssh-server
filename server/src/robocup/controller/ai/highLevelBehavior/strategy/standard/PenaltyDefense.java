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
		roles.add(RobotMode.KEEPER);
		roles.add(RobotMode.RUNNER);
		roles.add(RobotMode.RUNNER);
		roles.add(RobotMode.RUNNER);
		roles.add(RobotMode.RUNNER);
		roles.add(RobotMode.COUNTER);
	}

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
