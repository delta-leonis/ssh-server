package robocup.controller.ai.lowLevelBehavior;

import robocup.controller.ai.movement.GotoPosition;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.enums.RobotMode;
import robocup.output.ComInterface;

public class Runner extends LowLevelBehavior {

	private FieldPoint ballPosition;
	private FieldPoint freePosition;

	/**
	 * Create Runner lowlevel behavior
	 * A runner will try to move to a free position given by the high level behavior
	 * @param robot the Runner {@link Robot} in the model
	 * @param output Used to send data to the Robot
	 * @param ballPosition the position of the ball
	 * @param freePosition a free position where the runner needs to go
	 */
	public Runner(Robot robot, ComInterface output, FieldPoint ballPosition, FieldPoint freePosition) {
		super(robot);
		this.ballPosition = ballPosition;
		this.freePosition = freePosition;
		this.role = RobotMode.RUNNER;
		go = new GotoPosition(robot, output, freePosition, ballPosition);
	}

	/**
	 * Update
	 * @param ballPosition the position of the ball
	 * @param freePosition a free position where the robot needs to go
	 */
	public void update(FieldPoint ballPosition, FieldPoint freePosition) {
		this.ballPosition = ballPosition;
		this.freePosition = freePosition;
	}

	@Override
	public void calculate() {
		go.setTarget(ballPosition);
		go.setDestination(freePosition);
		go.calculate();
	}
}
