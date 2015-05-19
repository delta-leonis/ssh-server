package robocup.test.testBehaviors;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.controller.ai.highLevelBehavior.strategy.defense.BarricadeDefending;
import robocup.model.FieldPoint;
import robocup.model.enums.RobotMode;

public class TestStrategy extends Strategy {

	/**
	 * Roles in the {@link BarricadeDefending} strategy are assigned in the following order:<br>
	 * <ol>
	 * <li>{@link RobotMode#KEEPER}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDERS}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDERS}</li>
	 * <li>{@link RobotMode#COUNTER}</li>
	 * <li>{@link RobotMode#GOALPOSTCOVERER}</li>
	 * <li>{@link RobotMode#DISTURBER}</li>
	 * </ol>
	 */
	public TestStrategy() {
		super();
		roles.add(RobotMode.DISTURBER);
		roles.add(RobotMode.DISTURBER);
		roles.add(RobotMode.DISTURBER);
		roles.add(RobotMode.DISTURBER);
		roles.add(RobotMode.DISTURBER);
		roles.add(RobotMode.DISTURBER);
	}

	@Override
	public void updateZones(FieldPoint ballPosition) {
		super.updateZones(ballPosition);
	}
}
