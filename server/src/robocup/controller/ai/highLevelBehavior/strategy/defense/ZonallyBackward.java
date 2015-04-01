package robocup.controller.ai.highLevelBehavior.strategy.defense;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

public class ZonallyBackward extends Strategy {

	public ZonallyBackward() {
		super();
		roles.add(RobotMode.KEEPER);
		roles.add(RobotMode.KEEPERDEFENDER);
		
		roles.add(RobotMode.KEEPERDEFENDER_COVERER);
		roles.add(RobotMode.KEEPERDEFENDER_COVERER);
		
		roles.add(RobotMode.DISTURBER_COVERER);
		roles.add(RobotMode.DISTURBER_COVERER);
	}
	
	@Override
	public void updateZones(FieldPoint ballPosition) {
		//TODO kantafhanklijk
		zonesForRole.put(RobotMode.DISTURBER_COVERER, FieldZone.WEST_LEFT_FRONT);
		zonesForRole.put(RobotMode.DISTURBER_COVERER, FieldZone.WEST_RIGHT_FRONT);
	}
}
