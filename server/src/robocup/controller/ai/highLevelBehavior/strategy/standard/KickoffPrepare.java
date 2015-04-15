package robocup.controller.ai.highLevelBehavior.strategy.standard;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.World;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

public class KickoffPrepare extends Strategy {
	public KickoffPrepare () {
		super();
		roles.add(RobotMode.KEEPER);
		
		roles.add(RobotMode.ATTACKER);
		roles.add(RobotMode.RUNNER);
		roles.add(RobotMode.RUNNER);
		
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);
	}
	
	@Override
	public void updateZones(FieldPoint ballPosition) {
		if(World.getInstance().getReferee().getAlly().equals(World.getInstance().getReferee().getEastTeam())) {
			zonesForRole.put(RobotMode.RUNNER, FieldZone.EAST_NORTH_FRONT);
			zonesForRole.put(RobotMode.RUNNER, FieldZone.EAST_SOUTH_FRONT);
			zonesForRole.put(RobotMode.ATTACKER, FieldZone.EAST_MIDDLE);
		} else {
			zonesForRole.put(RobotMode.RUNNER, FieldZone.WEST_SOUTH_FRONT);
			zonesForRole.put(RobotMode.RUNNER, FieldZone.WEST_SOUTH_FRONT);
			zonesForRole.put(RobotMode.ATTACKER, FieldZone.WEST_MIDDLE);
		}
	}

}
