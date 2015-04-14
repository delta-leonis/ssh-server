package robocup.controller.ai.highLevelBehavior.strategy.standard;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
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
		// TODO Auto-generated method stub
		
	}

}
