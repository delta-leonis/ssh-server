package robocup.controller.ai.highLevelBehavior.strategy.standard;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.enums.RobotMode;

public class ThrowInForward extends Strategy {
	public ThrowInForward () {
		super();
		roles.add(RobotMode.KEEPER);
		roles.add(RobotMode.COUNTER);
		roles.add(RobotMode.GOALPOSTCOVERER);

		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.DISTURBER);
	}
	@Override
	public void updateZones(FieldPoint ballPosition) {
		// TODO Auto-generated method stub
		
	}

}
