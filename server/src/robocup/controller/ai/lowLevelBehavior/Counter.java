package robocup.controller.ai.lowLevelBehavior;

import robocup.controller.ai.movement.GotoPosition;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;
import robocup.output.ComInterface;

public class Counter extends LowLevelBehavior {

	private FieldZone zone;
	private FieldPoint ballPosition;
	private FieldPoint freePosition;

	/**
	 * Create Counter LowLevelBevahior
	 * A counter tries to move to a given free position,
	 * if no free position is given try to move to the center point of his assigned zone 
	 * @param robot the Counter {@link Robot} in the model
	 * @param output Used to send data to the Robot
	 * @param ballPosition the position of the ball
	 * @param freePosition a free position within the zone
	 */
	public Counter(Robot robot, ComInterface output, FieldZone zone, FieldPoint ballPosition, FieldPoint freePosition) {
		super(robot);
		this.zone = zone;
		this.ballPosition = ballPosition;
		this.freePosition = freePosition;
		this.role = RobotMode.COUNTER;
		go = new GotoPosition(robot, output, null, ballPosition);
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
			go.calculate();
	}
}
