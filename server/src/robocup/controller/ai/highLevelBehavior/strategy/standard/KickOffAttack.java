package robocup.controller.ai.highLevelBehavior.strategy.standard;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.enums.RobotMode;

public class KickOffAttack extends Strategy {

	public KickOffAttack() {
		super();
		roles.add(RobotMode.KEEPER);

		roles.add(RobotMode.ATTACKER);
		roles.add(RobotMode.RUNNER);
		roles.add(RobotMode.RUNNER);

		roles.add(RobotMode.RUNNER);
		roles.add(RobotMode.RUNNER);
	}

	@Override
	public void updateZones(FieldPoint ballPosition) {
		
	}
}
