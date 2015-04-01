package robocup.controller.ai.highLevelBehavior.strategy.attack;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.enums.RobotMode;

/**
 * {@link SecondPostKickIn} is an attack strategy that is based on creating a free space by
 * luring enemy robots to one side (north or south). When the enemy robots are pulled to one side,
 * the {@link RobotMode#RUNNER} runs to the second post and kicks the assist in the goal.
 * 
 * For more information see TactiekDocument
 */
public class SecondPostKickIn extends Strategy {

	/**
	 * 
	 */
	public SecondPostKickIn() {
		super();
		roles.add(RobotMode.KEEPER);
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);


		roles.add(RobotMode.ATTACKER);
		roles.add(RobotMode.ATTACKER);
		roles.add(RobotMode.RUNNER);
	}
	
	public void updateZones() {
		
	}
}
