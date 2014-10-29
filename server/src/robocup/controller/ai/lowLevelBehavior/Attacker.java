package robocup.controller.ai.lowLevelBehavior;

import robocup.controller.ai.highLevelBehavior.forcebehavior.Mode;
import robocup.controller.ai.movement.GotoPosition;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.output.ComInterface;

public class Attacker extends LowLevelBehavior {

	private Point freePosition;
	private Point ballPosition;
	private int kick;
	private int chip;
	private int shootDirection;

	/**
	 * Create an attacker
	 * @param robot the attacker
	 * @param output 
	 * @param freePosition a free position on the field
	 * @param ballPosition the position of the ball
	 * @param kick kick strength in percentages
	 * @param chip chip strength in percentages
	 * @param shootDirection direction where the attacker needs to shoot
	 */
	public Attacker(Robot robot, ComInterface output, Point freePosition, Point ballPosition, int kick, int chip, int shootDirection) {
		super(robot, output);
		this.freePosition = freePosition;
		this.ballPosition = ballPosition;
		this.kick = kick;
		this.chip = chip;
		this.shootDirection = shootDirection;
		this.role = Mode.roles.ATTACKER;
		go = new GotoPosition(robot, output, null, ballPosition);
	}
	
	/**
	 * Update
	 * @param freePosition
	 * @param ballPosition
	 * @param kick
	 * @param chip
	 * @param shootDirection
	 */
	public void update(Point freePosition, Point ballPosition, int kick, int chip, int shootDirection) {
		this.freePosition = freePosition;
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

			// Kick or chip if the values are higher than 0
			if(kick > 0)
				go.setKick(kick);
			else if(chip > 0)
				go.setKick(-chip);
			else {
				// Move towards a free position when given
				if(freePosition != null)
					newDestination = freePosition;
				// Move towards a shooting position behind the ball
				else
					newDestination = getShootingPosition(shootDirection, ballPosition);
			}

			changeDestination(newDestination);
		}
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
