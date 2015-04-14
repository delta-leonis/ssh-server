package robocup.controller.ai.highLevelBehavior.strategy.standard;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
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
		// TODO Auto-generated method stub
		
	}

}
