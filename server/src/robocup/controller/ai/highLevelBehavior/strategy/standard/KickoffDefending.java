package robocup.controller.ai.highLevelBehavior.strategy.standard;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.enums.RobotMode;

public class KickoffDefending extends Strategy {
	public KickoffDefending () {
		super();
		roles.add(RobotMode.KEEPER);
		// Center robot coverer
		roles.add(RobotMode.COVERER);
		// Top side field coverer
		roles.add(RobotMode.COVERER);
		// Bottom side field coverer
		roles.add(RobotMode.COVERER);
		
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);
	}
	
	@Override
	public void updateZones(FieldPoint ballPosition) {
		// TODO Auto-generated method stub
		
	}

}
