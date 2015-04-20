package robocup.controller.ai.highLevelBehavior.strategy.attack;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.enums.RobotMode;

/**
 * {@link PenaltyAreaKickIn} is an attack strategy that is used to create a free shot via
 *  a robot in the corner. The {@link RobotMode#ATTACKER} in the corner gets the ball from an
 *  other {@link RobotMode#ATTACKER} and passes the ball back so that the other
 *  {@link RobotMode#ATTACKER} can kick in the created free shot. A {@link RobotMode#RUNNER}
 *  moves to the opposite corner for an extra passing option.
 * <br>
 * For defense during the attack, a {@link RobotMode#KEEPER} and 2 {@link RobotMode#KEEPERDEFENDERS}
 * are used.
 * <br><br>
 * <img src="../../../../../../../images/PenaltyAreaKickIn.jpg" />
 * <br><br>
 * For more information about the strategy and roles see TactiekDocument
 */
public class PenaltyAreaKickIn extends Strategy {

	/**
	 * Roles in the {@link PenaltyAreaKickIn} strategy are assigned in the following order:<br>
	 * <ol>
	 * <li>{@link RobotMode#KEEPER}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDERS}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDERS}</li>
	 * <li>{@link RobotMode#ATTACKER}</li>
	 * <li>{@link RobotMode#ATTACKER}</li>
	 * <li>{@link RobotMode#RUNNER}</li>
	 * </ol>
	 */
	public PenaltyAreaKickIn() {
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
