package robocup.controller.ai.highLevelBehavior.strategy.standard;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
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
		roles.add(RobotMode.COUNTER);
		
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);
	}
	
	@Override
	public void updateZones(FieldPoint ballPosition) {
		// TODO Auto-generated method stub
		
	}
}
