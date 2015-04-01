package robocup.controller.ai.highLevelBehavior.strategy.attack;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.enums.RobotMode;

public class PenaltyAreaKickIn extends Strategy {

	public PenaltyAreaKickIn() {
		super();
		roles.add(RobotMode.KEEPER);
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);


		roles.add(RobotMode.ATTACKER);
		roles.add(RobotMode.ATTACKER);
		roles.add(RobotMode.RUNNER);
	}
	
	public void updateZones() {
		
	}
}
