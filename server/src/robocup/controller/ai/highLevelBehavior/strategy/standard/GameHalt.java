package robocup.controller.ai.highLevelBehavior.strategy.standard;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.enums.RobotMode;

public class GameHalt extends Strategy {
	public GameHalt () {
		super();
		roles.add(RobotMode.KEEPER);
		// two robots stand as close to the ball as possible, because we might get the ball
		roles.add(RobotMode.DISTURBER);
		roles.add(RobotMode.DISTURBER);
		// one robot stands ready for the counter attack (or to cover an enemy when they get the ball)
		roles.add(RobotMode.COUNTER);
		// two robots defend
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);
	}
	
	@Override
	public void updateZones(FieldPoint ballPosition) {
		// TODO Auto-generated method stub
		
	}

}
