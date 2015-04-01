package robocup.controller.ai.highLevelBehavior.strategy.defense;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

public class ZonallyForward extends Strategy {

	public ZonallyForward() {
		super();
		roles.add(RobotMode.KEEPER);
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);
		
		roles.add(RobotMode.COUNTER);
		
		roles.add(RobotMode.DISTURBER_COVERER);
		roles.add(RobotMode.DISTURBER_COVERER);
	}
	
	public void updateZones() {
		//TODO kantafhanklijk
		zonesForRole.put(RobotMode.DISTURBER_COVERER, FieldZone.WEST_LEFT_FRONT);
		zonesForRole.put(RobotMode.DISTURBER_COVERER, FieldZone.WEST_RIGHT_FRONT);
		zonesForRole.put(RobotMode.COUNTER, FieldZone.EAST_CENTER);
	}
}