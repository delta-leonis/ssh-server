package robocup.controller.ai.lowLevelBehavior;

import robocup.controller.ai.movement.GotoPosition;
import robocup.model.FieldPoint;
import robocup.model.Goal;
import robocup.model.Robot;
import robocup.model.enums.RobotMode;

public class PenaltyKeeper extends LowLevelBehavior {

	private FieldPoint ballPosition;
	private Robot enemy;
	private Goal goal;

	/**
	 * Create a penalty keeper.
	 * The penalty keeper will drive towards a point on the goal line in an attempt to block a penalty.
	 * @param robot the penalty keeper {@link Robot} in the model.
	 * @param goal goal to defend
	 */
	public PenaltyKeeper(Robot robot, Goal goal) {
		super(robot);
		ballPosition = null;
		this.goal = goal;
		enemy = null;
		this.role = RobotMode.PENALTYKEEPER;
		go = new GotoPosition(robot, robot.getPosition(), ballPosition);
		go.setMaxRotationSpeed(600);
	}

	@Override
	public void calculate() {
		FieldPoint newDestination = getNewKeeperDestination();

		go.setTarget(ballPosition);
		go.setDestination(newDestination);
		go.calculate(false, true);
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

		if(enemy == null || enemy.getPosition() == null)
			return newDestination;

		FieldPoint enemyPosition = enemy.getPosition();
		//double angle = Math.toRadians(enemyPosition.getAngle(ballPosition));
		double angle = Math.toRadians(Math.abs(enemy.getOrientation()));
		double offset = (goal.getFrontNorth().getX() > 0 ? Robot.DIAMETER : -Robot.DIAMETER); //should be divided by 2, isn't being devided currelty because of lack of precision 
		double newY =  enemyPosition.getY() + (goal.getFrontNorth().getX() - enemyPosition.getX() - offset) * Math.tan(angle);

		newDestination = new FieldPoint(goal.getFrontNorth().getX() - offset/2, Math.min(goal.getFrontNorth().getY() - Robot.DIAMETER/2 ,Math.max(goal.getFrontSouth().getY() + Robot.DIAMETER/2, newY)));

		return newDestination;
	}
}
