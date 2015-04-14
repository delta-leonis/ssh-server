package robocup.controller.ai.highLevelBehavior.strategy.standard;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.enums.RobotMode;

public class PenaltyEnemy extends Strategy {
	public PenaltyEnemy () {
		super();
		roles.add(RobotMode.KEEPER);
		
		// create a wall to regain the ball after a failed penalty
		roles.add(RobotMode.RUNNER);
		roles.add(RobotMode.RUNNER);
		roles.add(RobotMode.RUNNER);
		roles.add(RobotMode.RUNNER);
		
		// stands in a position for the counter attack
		roles.add(RobotMode.COUNTER);
	}
	@Override
	public void updateZones(FieldPoint ballPosition) {
		// TODO Auto-generated method stub
		
	}

}
