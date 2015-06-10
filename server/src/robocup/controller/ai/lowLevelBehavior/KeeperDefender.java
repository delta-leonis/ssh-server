package robocup.controller.ai.lowLevelBehavior;

import robocup.controller.ai.movement.GotoPosition;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.World;
import robocup.model.enums.RobotMode;

public class KeeperDefender extends Keeper {

	private int offset;
	
	public KeeperDefender(Robot robot, FieldPoint centerGoalPosition) {
		super(robot, centerGoalPosition);
		offset = 0;
		this.role = RobotMode.KEEPERDEFENDER;
		go = new GotoPosition(robot, centerGoalPosition, ballPosition/*, 3000*/);
		go.setStartupSpeedVelocity(500);
	}
	
	@Override
	public void calculate() {
		// calculate position
		FieldPoint newDestination = getNewKeeperDestination(centerGoalPosition, ballPosition , distanceToObject);
		if(robot.getPosition().getDeltaDistance(newDestination) < 1000){
			robot.setIgnore(true);
		}
		else{
			robot.setIgnore(false);
		}
		// Change direction based on goToKick.
		// Move forward and kick if ball gets too close
		// Else, go to proper direction
		changeDestination(newDestination, ballPosition);
	}

	/**
	 * Update the values for the keeper
	 * @param distanceToGoal
	 * @param goToKick
	 * @param ballPosition
	 * @param offset the offset for this keeper defender in degrees
	 */
	public void update(int distanceToGoal, boolean goToKick, FieldPoint ballPosition, int offset, double fieldWidth, double fieldLength) {
		super.update(distanceToGoal, goToKick, ballPosition, fieldWidth, fieldLength);
		this.offset = offset;
	}

	@Override
	protected FieldPoint getNewKeeperDestination(FieldPoint objectPosition, FieldPoint subjectPosition, int distance) {
		return super.getNewKeeperDestination(objectPosition, cropFieldPosition(subjectPosition), distance, offset);
	}
}
