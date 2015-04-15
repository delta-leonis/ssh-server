package robocup.controller.ai.highLevelBehavior.strategy.standard;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.World;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

public class FreeKickForward extends Strategy {
	public FreeKickForward () {
		super();
		roles.add(RobotMode.KEEPER);
		// robot taking the kickoff
		roles.add(RobotMode.ATTACKER);
		// outer left
		roles.add(RobotMode.RUNNER);
		// outer right
		roles.add(RobotMode.RUNNER);
		
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);
	}
	
	@Override
	public void updateZones(FieldPoint ballPosition) {
		if(World.getInstance().getReferee().getAlly().equals(World.getInstance().getReferee().getEastTeam())) {
			zonesForRole.put(RobotMode.RUNNER, FieldZone.WEST_SOUTH_FRONT);
			zonesForRole.put(RobotMode.RUNNER, FieldZone.WEST_NORTH_FRONT);
			if(ballPosition.getY() <= 0.0) {
				zonesForRole.put(RobotMode.ATTACKER, FieldZone.EAST_NORTH_FRONT);
			} else {
				zonesForRole.put(RobotMode.ATTACKER, FieldZone.EAST_SOUTH_FRONT);
			}
		} else {
			zonesForRole.put(RobotMode.RUNNER, FieldZone.EAST_SOUTH_FRONT);
			zonesForRole.put(RobotMode.RUNNER, FieldZone.EAST_NORTH_FRONT);
			if(ballPosition.getY() <= 0.0) {
				zonesForRole.put(RobotMode.ATTACKER, FieldZone.WEST_NORTH_FRONT);
			} else {
				zonesForRole.put(RobotMode.ATTACKER, FieldZone.WEST_SOUTH_FRONT);
			}
		}
	}
}
