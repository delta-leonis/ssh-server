package robocup.controller.ai.highLevelBehavior.strategy.defense;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

public class ForwardDefending extends Strategy {

	public ForwardDefending() {
		super();
		roles.add(RobotMode.KEEPER);
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);


		roles.add(RobotMode.COUNTER);
		roles.add(RobotMode.COVERER);
		roles.add(RobotMode.COVERER);
	}
	
	@Override
	public void updateZones(FieldPoint ballPosition) {
		//TODO kantafhanklijk
		zonesForRole.put(RobotMode.COUNTER, FieldZone.EAST_CENTER);
	}
}
