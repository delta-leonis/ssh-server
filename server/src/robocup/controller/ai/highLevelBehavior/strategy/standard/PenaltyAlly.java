package robocup.controller.ai.highLevelBehavior.strategy.standard;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.World;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

public class PenaltyAlly extends Strategy {
	public PenaltyAlly () {
		super();
		roles.add(RobotMode.KEEPER);
		// the robot that takes the penalty, the referee needs to be notified about this robot 
		roles.add(RobotMode.ATTACKER);
		// Try to stand as near to the enemy goal as is allowed, to get the ball if the penalty is blocked
		roles.add(RobotMode.RUNNER);
		roles.add(RobotMode.RUNNER);
		
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);
	}
	@Override
	public void updateZones(FieldPoint ballPosition) {
		if(World.getInstance().getReferee().getAlly().equals(World.getInstance().getReferee().getEastTeam())) {
			zonesForRole.put(RobotMode.ATTACKER, FieldZone.WEST_CENTER);
			zonesForRole.put(RobotMode.RUNNER, FieldZone.WEST_MIDDLE);
			zonesForRole.put(RobotMode.RUNNER, FieldZone.WEST_MIDDLE);
		} else {
			zonesForRole.put(RobotMode.ATTACKER, FieldZone.EAST_CENTER);
			zonesForRole.put(RobotMode.RUNNER, FieldZone.EAST_MIDDLE);
			zonesForRole.put(RobotMode.RUNNER, FieldZone.EAST_MIDDLE);
		}
	}
}
