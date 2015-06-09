package robocup.controller.ai.lowLevelBehavior;

import robocup.controller.ai.movement.GotoPosition;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.enums.RobotMode;

public class GoalPostCoverer extends Keeper {

	private FieldPoint paalPosition;
	private FieldPoint enemyRobotPosition;

	/**
	 * Create a GoalPostCoverer LowLevelBehaviour
	 * @param robot the goalpostcoverer {@link Robot} in the model.
	 * @param paalPosition the position of the ball
	 */
	public GoalPostCoverer(Robot robot, FieldPoint paalPosition) {
		super(robot, paalPosition);
		this.paalPosition = paalPosition;
		enemyRobotPosition = null;
		this.role = RobotMode.GOALPOSTCOVERER;
		go = new GotoPosition(robot, null, null);
	}

	/**
	 * Update the GoalPostCoverer
	 * @param distanceToPole the distance the robot needs to keep to the pole
	 * @param goToKick set this to true if the robot needs to move towards the ball
	 * @param enemyRobotPosition the position of the enemy robot this goalpostcoverer is covering
	 * @param ballPosition the position of the ball
	 */
	public void update(int distanceToPole, boolean goToKick, FieldPoint enemyRobotPosition, FieldPoint ballPosition, double fieldWidth, double fieldHeight) {
		super.update(distanceToPole, goToKick, ballPosition, fieldWidth, fieldHeight);
		this.enemyRobotPosition = enemyRobotPosition;
		
	}

	@Override
	public void calculate() {
		FieldPoint newDestination = null;

		if (enemyRobotPosition != null)
			newDestination = getNewKeeperDestination(paalPosition, enemyRobotPosition, distanceToObject);

		changeDestination(newDestination, ballPosition);
	}
}
