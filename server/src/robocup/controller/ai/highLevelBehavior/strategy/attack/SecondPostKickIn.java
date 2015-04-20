package robocup.controller.ai.highLevelBehavior.strategy.attack;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.enums.RobotMode;

/**
 * {@link SecondPostKickIn} is an attack strategy that is based on creating a free space by
 * luring enemy robots to one side (north or south). When the enemy robots are pulled to one side,
 * the {@link RobotMode#RUNNER} runs to the second post and kicks the assist in the goal. The 
 * assist is given by an {@link RobotMode#ATTACKER}, while an other {@link RobotMode#ATTACKER} 
 * moves to the corner for an extra passing option.
 * <br>
 * For defense during the attack, a {@link RobotMode#KEEPER} and 2 {@link RobotMode#KEEPERDEFENDERS}
 * are used.
 * <br><br>
 * <img src="../../../../../../../images/secondPostKickIn.jpg" />
 * <br><br>
 * For more information about the strategy and roles see TactiekDocument
 */
public class SecondPostKickIn extends Strategy {

	/**
	 * Roles in the {@link SecondPostKickIn} strategy are assigned in the following order:<br>
	 * <ol>
	 * <li>{@link RobotMode#KEEPER}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDERS}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDERS}</li>
	 * <li>{@link RobotMode#ATTACKER}</li>
	 * <li>{@link RobotMode#ATTACKER}</li>
	 * <li>{@link RobotMode#RUNNER}</li>
	 * </ol>
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

	@Override
	public void updateZones(FieldPoint ballPosition) {

	}
}
