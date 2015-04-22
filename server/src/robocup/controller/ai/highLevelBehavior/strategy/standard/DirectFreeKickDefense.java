package robocup.controller.ai.highLevelBehavior.strategy.standard;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.enums.RobotMode;

public class DirectFreeKickDefense extends Strategy {

	public DirectFreeKickDefense() {
		super();
		roles.add(RobotMode.KEEPER);
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.GOALPOSTCOVERER);
		roles.add(RobotMode.COUNTER);
	}

	@Override
	public void updateZones(FieldPoint ballPosition) {

	}
}