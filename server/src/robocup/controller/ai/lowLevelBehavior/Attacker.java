package robocup.controller.ai.lowLevelBehavior;

import robocup.model.Point;
import robocup.model.Robot;
import robocup.output.ComInterface;

public class Attacker extends LowLevelBehavior {

	private Point currentPosition;
	private Point ballPosition;
	private int kick;
	private int chip;
	private int shootDirection;

	public Attacker(Robot robot, ComInterface output, Point currentPosition, Point ballPosition, int kick, int chip, int shootDirection) {
		super(robot, output);
		this.currentPosition = currentPosition;
		this.ballPosition = ballPosition;
		this.kick = kick;
		this.chip = chip;
		this.shootDirection = shootDirection;
		go = new GotoPosition(robot, output, null, ballPosition);
	}
	
	public void update(Point currentPosition, Point ballPosition, int kick, int chip, int shootDirection) {
		this.currentPosition = currentPosition;
		this.ballPosition = ballPosition;
		this.kick = kick;
		this.chip = chip;
		this.shootDirection = shootDirection;
	}

	@Override
	public void calculate() {
		if(timeOutCheck()) {
			
		} else {
			Point newDestination = null;

			if(kick > 0)
				go.setKick(kick);
			else if(chip > 0)
				go.setKick(-chip);
			else {
				if(isClosestToBall())
					newDestination = getShootingPosition(shootDirection, ballPosition);
				else
					newDestination = getFreePosition();
			}

			changeDestination(newDestination);
		}
	}
	
	private Point getFreePosition() {
		return null;
	}

	private boolean isClosestToBall() {
		return true;
	}
	
	/**
	 * Change the destination of the robot
	 * @param newDestination the new destination
	 */
	private void changeDestination(Point newDestination) {
		if(newDestination != null) {
			if(isWithinRange(robot, newDestination, 10))
				go.setDestination(null);
			else
				go.setDestination(newDestination);

			go.setTarget(ballPosition);
			go.calculate();
		}
	}
}
