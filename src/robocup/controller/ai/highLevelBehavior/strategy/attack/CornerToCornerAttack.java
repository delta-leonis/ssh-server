package robocup.controller.ai.highLevelBehavior.strategy.attack;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.enums.RobotMode;

/**
 * {@link CornerToCornerAttack} is an attack strategy that is used to create a free shot via
 *  giving a cross to one of the 2 {@link RobotMode#RUNNER}s. A {@link RobotMode#ATTACKER}
 *  moves with the ball into the corner and gives a cross to a {@link RobotMode#RUNNER}
 *  in the opposite corner or to a {@link RobotMode#RUNNER} that moves to some point in 
 *  front of the penalty area.
 * <br>
 * For defense during the attack, a {@link RobotMode#KEEPER} and 2 {@link RobotMode#KEEPERDEFENDERS}
 * are used.
 * <br><br>
 * <img src="../../../../../../../images/cornerToCornerAttack.jpg" />
 * <br><br>
 * For more information about the strategy and roles see TactiekDocument
 */
public class CornerToCornerAttack extends Strategy {

	/**
	 * Roles in the {@link CornerToCornerAttack} strategy are assigned in the following order:<br>
	 * <ol>
	 * <li>{@link RobotMode#KEEPER}</li>
	 * <li>{@link RobotMode#ATTACKER}</li>
	 * <li>{@link RobotMode#RUNNER}</li>
	 * <li>{@link RobotMode#RUNNER}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDER}</li>
	 * <li>{@link RobotMode#KEEPERDEFENDER}</li>
	 * </ol>
	 */
	public CornerToCornerAttack() {
		super();
		roles.add(RobotMode.KEEPER);
		roles.add(RobotMode.ATTACKER);
		roles.add(RobotMode.RUNNER);
		roles.add(RobotMode.RUNNER);
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);
	}

	@Override
	public void updateZones(FieldPoint ballPosition) {
		super.updateZones(ballPosition);

	}
}
