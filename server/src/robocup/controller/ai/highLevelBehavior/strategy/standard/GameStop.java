package robocup.controller.ai.highLevelBehavior.strategy.standard;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.enums.RobotMode;

public class GameStop extends Strategy {

	public GameStop() {
		super();
		roles.add(RobotMode.KEEPER);
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.COUNTER);
		roles.add(RobotMode.DISTURBER);
		roles.add(RobotMode.DISTURBER);
	}

	@Override
	public void updateZones(FieldPoint ballPosition) {
		
	}
}
