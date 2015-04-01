package robocup.controller.ai.highLevelBehavior.strategy.attack;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

public class FreeShotRoundPlay extends Strategy {

	public FreeShotRoundPlay() {
		super();
		roles.add(RobotMode.KEEPER);
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);


		roles.add(RobotMode.ATTACKER);
		roles.add(RobotMode.ATTACKER);
		roles.add(RobotMode.ATTACKER);
	}
	
	@Override
	public void updateZones(FieldPoint ballPosition) {
		//TODO kant afhanklijk maken
		zonesForRole.put(RobotMode.ATTACKER, FieldZone.WEST_CENTER);
		zonesForRole.put(RobotMode.ATTACKER, FieldZone.WEST_RIGHT_SECOND_POST);
		zonesForRole.put(RobotMode.ATTACKER, FieldZone.WEST_LEFT_SECOND_POST);
	}
}