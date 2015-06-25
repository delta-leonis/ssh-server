package robocup.controller.ai.lowLevelBehavior;

import robocup.controller.ai.movement.GotoPosition;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.World;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

public class Counter extends LowLevelBehavior {

	private FieldZone zone;
	private FieldPoint ballPosition;
	private FieldPoint freePosition;

	/**
	 * Create Counter LowLevelBevahior
	 * A counter tries to move to a given free position,
	 * if no free position is given try to move to the center point of his assigned zone 
	 * @param robot the Counter {@link Robot} in the model
	 */
	public Counter(Robot robot) {
		super(robot);
		zone = null;
		ballPosition = null;
		freePosition = null;

		this.role = RobotMode.COUNTER;
		go = new GotoPosition(robot, null, ballPosition);
		go.setStartupSpeedVelocity(200);
		go.setMaxVelocity(2500);
		go.setDistanceToSlowDown(300);
		go.setMaxRotationSpeed(1200);
		go.setStartupSpeedRotation(200);
	}

	/**
	 * Update
	 * @param zone a free position on the field. If not null, the Robot should go here
	 * @param ballPosition the position of the ball
	 * @param freePosition a free position within the zone
	 */
	public void update(FieldZone zone, FieldPoint ballPosition, FieldPoint freePosition) {
		this.zone = zone;
		this.ballPosition = ballPosition;
		this.freePosition = freePosition;
	}

	@Override
	public void calculate() {
		go.setTarget(ballPosition);

		if (freePosition != null)
			go.setDestination(freePosition);
		else if (zone != null)
			// try to go to center point if no free position is given
			go.setDestination(zone.getCenterPoint());

		if (robot.getPosition() != null)
			go.calculate(false, true);
	}
}
