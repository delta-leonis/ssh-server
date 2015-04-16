package robocup.controller.ai.highLevelBehavior.strategy.standard;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.enums.RobotMode;

/**
 * {@link GameHalt} is a strategy that is assigned when the entire team has to freeze in place.
 * Robots should not be able to move while this strategy is active.
 * <br><br>
 * For more information about the strategy and roles see TactiekDocument
 */
public class GameHalt extends Strategy {
	/**
	 * Robot roles do not really matter because robots do not move while this strategy is active.
	 * Roles in the {@link GameHalt} strategy are assigned in the following order:<br>
	 * <ol>
	 * <li>{@link RobotMode#KEEPER}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDER}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDER}</li>
	 * <li>{@link RobotMode#RUNNER}</li>
	 * <li>{@link RobotMode#RUNNER}</li>
	 * <li>{@link RobotMode#RUNNER}</li>
	 * </ol>
	 */
	public GameHalt () {
		super();
		roles.add(RobotMode.KEEPER);

		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);
		
		roles.add(RobotMode.RUNNER);
		roles.add(RobotMode.RUNNER);
		roles.add(RobotMode.RUNNER);
		
	}
	
	@Override
	public void updateZones(FieldPoint ballPosition) {
		// TODO Auto-generated method stub
		
	}

}
