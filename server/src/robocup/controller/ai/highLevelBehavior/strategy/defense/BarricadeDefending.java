package robocup.controller.ai.highLevelBehavior.strategy.defense;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

public class BarricadeDefending extends Strategy {

	public BarricadeDefending() {
		super();
		roles.add(RobotMode.KEEPER);
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);


		roles.add(RobotMode.COUNTER);
		roles.add(RobotMode.GOALPOSTCOVERER);
		roles.add(RobotMode.DISTURBER);
	}
	
	@Override
	public void updateZones(FieldPoint ballPosition) {
		//TODO kantafhankelijk
		//vijandelijke helft centrum
		zonesForRole.put(RobotMode.COUNTER, FieldZone.EAST_CENTER);
		//2e paal eigen kant, helft waar de bal niet is
		zonesForRole.put(RobotMode.GOALPOSTCOVERER, FieldZone.EAST_LEFT_SECOND_POST);
	}
}
