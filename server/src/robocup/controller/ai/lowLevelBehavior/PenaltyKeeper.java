package robocup.controller.ai.lowLevelBehavior;

import robocup.controller.ai.movement.GotoPosition;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.enums.RobotMode;

public class PenaltyKeeper extends LowLevelBehavior {

	private FieldPoint ballPosition;
	private Robot enemy;
	private double maxX;

	/**
	 * Create a penalty keeper.
	 * The penalty keeper will drive towards a point on the goal line in an attempt to block a penalty.
	 * @param robot the penalty keeper {@link Robot} in the model.
	 * @param ballPosition the position of the ball
	 * @param enemy the enemy penalty taker
	 * @param fieldLength the length of the field, needed to calculate a point on the goal line
	 */
	public PenaltyKeeper(Robot robot, FieldPoint ballPosition, Robot enemy, double fieldLength) {
		super(robot);
		maxX = fieldLength / 2;
		this.ballPosition = ballPosition;
		this.enemy = enemy;
		this.role = RobotMode.PENALTYKEEPER;
		go = new GotoPosition(robot, robot.getPosition(), ballPosition, 1500);
	}

	@Override
	public void calculate() {
		FieldPoint newDestination = getNewKeeperDestination();

		go.setTarget(ballPosition);
		go.setDestination(newDestination);
		go.calculate();
	}
	
	/**
	 * Update the values for the keeper
	 * @param ballPosition
	 * @param enemy
	 */
	public void update(FieldPoint ballPosition, Robot enemy) {
		this.ballPosition = ballPosition;
		this.enemy = enemy;
	}

	/**
	 * Calculate where the penalty keeper needs to go.
	 * The point needs to be on the goal line, in the direction where the enemy robot can shoot.
	 * @return
	 */
	private FieldPoint getNewKeeperDestination() {
		FieldPoint newDestination = null;
		FieldPoint enemyPosition = enemy.getPosition();

		if (enemyPosition != null) {
			double angle = enemyPosition.getAngle(ballPosition);
			double dx = (enemyPosition.getX() > 0.0 ? maxX : -maxX) - enemyPosition.getX();
			double dy = Math.tan(angle) * dx;
			
			newDestination = new FieldPoint(enemyPosition.getX() + dx, enemyPosition.getY() + dy);
		}

		return newDestination;
	}
}
