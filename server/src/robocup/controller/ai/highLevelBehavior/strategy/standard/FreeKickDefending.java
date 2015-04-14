package robocup.controller.ai.highLevelBehavior.strategy.standard;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.enums.RobotMode;

public class FreeKickDefending extends Strategy {
	
	public FreeKickDefending () {
		super();
		roles.add(RobotMode.KEEPER);
		// Second pole defender
		roles.add(RobotMode.GOALPOSTCOVERER);
		// Covers the biggest theat of the enemy
		roles.add(RobotMode.DISTURBER);
		// covers the enemy that may kick the ball
		roles.add(RobotMode.COVERER);
		
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);
	}
	@Override
	public void updateZones(FieldPoint ballPosition) {
		// TODO Auto-generated method stub
		
	}

}
