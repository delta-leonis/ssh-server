package robocup.controller.ai.lowLevelBehavior;

import robocup.controller.ai.movement.GotoPosition;
import robocup.model.Ally;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.enums.RobotMode;
import robocup.output.ComInterface;

public class Attacker extends LowLevelBehavior {

	private FieldPoint ballPosition;
	private int chipKick;
	private double shootDirection;
	private Ally freeRobot;

	/**
	 * Create an attacker.
	 * The attacker will try to get into a shooting position for the given shoot direction.
	 * If no chipKick strength is given, try to pass to a free robot on the field.
	 * If no free robot is given, just chase the ball.
	 * @param robot the attacker {@link Robot} in the model.
	 * @param output Used to send data to the Robot
	 * @param shootDirection direction where the attacker needs to shoot, relative to the field. Values between -180 and 180. 0 degrees facing east. 90 degrees facing north.
	 * @param chipKick kick and chip strength in percentages. max kick = -100% , max chip = 100% . if chipKick = 0, do nothing
	 * @param freeRobot an ally robot which is able to receive the ball
	 * @param ballPosition the position of the ball
	 */
	public Attacker(Robot robot, ComInterface output, double shootDirection, int chipKick, Ally freeRobot,
			FieldPoint ballPosition) {
		super(robot, output);
		this.ballPosition = ballPosition;
		this.chipKick = chipKick;
		this.shootDirection = shootDirection;
		this.freeRobot = freeRobot;
		this.role = RobotMode.ATTACKER;
		go = new GotoPosition(robot, output, robot.getPosition(), ballPosition);
	}

	/**
	 * Update values
	 * @param shootDirection direction where the attacker needs to shoot, relative to the field. Values between -180 and 180. 0 degrees facing east. 90 degrees facing north.
	 * @param chipKick kick and chip strength in percentages. max kick = -100% , max chip = 100% . if chipKick = 0, do nothing
	 * @param freeRobot an ally robot which is able to receive the ball
	 * @param ballPosition the position of the ball
	 */
	public void update(double shootDirection, int chipKick, Ally freeRobot, FieldPoint ballPosition) {
		this.ballPosition = ballPosition;
		this.chipKick = chipKick;
		this.shootDirection = shootDirection;
		this.freeRobot = freeRobot;
	}

	@Override
	public void calculate() {
		FieldPoint newDestination = ballPosition;

		if (chipKick != 0) {
			// find a shooting position if the attacker needs to chip or kick
			newDestination = getShootingPosition(shootDirection, ballPosition);

			// kick or chip when the orientation is good and the attacker is close to the ball
			if (Math.abs(robot.getOrientation() - robot.getPosition().getAngle(ballPosition)) < 2.0
					&& robot.getPosition().getDeltaDistance(ballPosition) < 100)
				go.setKick(chipKick);
		} else {
			// if no chipKick strength is given the attacker tries to get into position to shoot to an allied player
			if (freeRobot != null) {
				newDestination = getShootingPosition(robot.getPosition().getAngle(freeRobot.getPosition()),
						ballPosition);

				if (Math.abs(robot.getOrientation() - robot.getPosition().getAngle(freeRobot.getPosition())) < 2.0
						&& robot.getPosition().getDeltaDistance(ballPosition) < 100)
					go.setKick(chipKick);
			}
		}

		changeDestination(newDestination);
	}

	/**
	 * Change the destination of the robot
	 * @param newDestination the new destination
	 */
	private void changeDestination(FieldPoint newDestination) {
		if (newDestination != null) {
			go.setDestination(newDestination);
			go.setTarget(ballPosition);
			go.calculate();
		}
	}
}
