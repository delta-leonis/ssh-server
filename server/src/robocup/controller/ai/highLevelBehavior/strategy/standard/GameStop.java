package robocup.controller.ai.highLevelBehavior.strategy.standard;

import org.apache.commons.math3.util.Pair;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.World;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

public class GameStop extends Strategy {

	public GameStop() {
		super();
		roles.add(RobotMode.KEEPER);
		roles.add(RobotMode.DISTURBER);
		roles.add(RobotMode.DISTURBER);
		roles.add(RobotMode.DISTURBER);
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);
	}

	@Override
	public void updateZones(FieldPoint ballPosition) {
		super.updateZones(ballPosition);

		boolean isEastTeam = World.getInstance().getReferee().getEastTeam()
				.equals(World.getInstance().getReferee().getAlly());

		if (isEastTeam) {
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COUNTER, FieldZone.WEST_MIDDLE));
		} else {
			zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COUNTER, FieldZone.EAST_MIDDLE));
		}
	}
}
