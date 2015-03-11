package robocup.controller.ai.lowLevelBehavior;

import robocup.controller.ai.movement.GotoPosition;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.model.enums.RobotMode;
import robocup.output.ComInterface;

/**
 * Paaldekker
 * Bezet lijn tussen tegenstander en midden van eigen goal, blijft binnen zone �2ePaal� staan. De af te dekken 
 * tegenstander is de tegenstander die het dichts bij is waarbij een lijn door de zone naar de goal mogelijk is.
 * De klasse hoeft alleen maar te weten tussen welke tegenstander en paal hij moet staan.
 * 
 * De positie van de bal is niet nodig. Deze krijgt hij pas als zijn rol veranderd naar bijvoorbeeld {@link Counter}
 * TODO: English-fy
 * TODO: use config to determine position of goal posts.
 */
public class Paaldekker extends LowLevelBehavior {
	
	public static final int distanceFromGoal = 1000;
	public static final int distanceFromMid = 250;
	

	private Robot enemyRobot;
	private Point paalPosition;
	private boolean dribble = false;

	/**
	 * Create a Paaldekker LowLevelBehaviour
	 * @param robot the paaldekker {@link Robot} in the model.
	 * @param output Used to send data to the Robot
	 * @param enemyRobot the Robot that matters most to this {@link LowLevelBehaviour}
	 * @param paalPosition the position of the ball
	 * @param dribble enable dribbler
	 */
	public Paaldekker(Robot robot, ComInterface output, Robot enemyRobot, Point paalPosition, 
			boolean dribble) {
		super(robot, output);
		this.enemyRobot = enemyRobot;
		this.paalPosition = paalPosition;
		this.dribble = dribble;
		this.role = RobotMode.PAALDEKKER;
		go = new GotoPosition(robot, output, null, enemyRobot.getPosition());
	}

	/**
	 * Update
	 * @param enemyRobot the robot we're trying to block
	 * @param paalPosition the position of the paal
	 * @param chipKick kick and chip strength in percentages.  max kick = -100% , max chip = 100% . if chipKick = 0, do nothing
	 * @param dribble enable dribbler
	 * @param shootDirection direction where the attacker needs to shoot, relative to the field. Values between -180 and 180. 0 degrees facing east. 90 degrees facing north. 
	 */
	public void update(Robot enemyRobot, Point paalPosition, boolean dribble) {
		this.enemyRobot = enemyRobot;
		this.paalPosition = paalPosition;
		this.dribble = dribble;
	}

	@Override
	public void calculate() {
		if (timeOutCheck()) {

		} else {
			Point newDestination = null;

			go.setDribble(dribble);

			// Move towards a free position when given
			if (enemyRobot != null)
				newDestination = calculateBestPosition();			

			changeDestination(newDestination);
		}
	}
	
	/**
	 * TODO: Test. Testtesttest. I don't trust the getAngle function. 
	 * @returns best position to block the opposing robot.
	 */
	public Point calculateBestPosition(){
		// point on the aftraplijn (1 meter radius from 25cm from each pole) between the goal and the enemy robot.
		// get 25cm point from goal
		Point goalPoint = null;
		// check if it's even a goal.. post .. thing.
		if(		paalPosition.equals(new Point(-3000, -500)) ||
				paalPosition.equals(new Point(-3000, 500)) ||
				paalPosition.equals(new Point(3000, -500)) ||
				paalPosition.equals(new Point(3000, 500))){
			goalPoint = new Point(paalPosition.getX(),
					paalPosition.getY() < 0 ? paalPosition.getY() + distanceFromMid : paalPosition.getY() -distanceFromMid);
		}
		// get direction
		int direction = goalPoint.getAngle(enemyRobot.getPosition());
		// tan(direction) * 1000mm = point.
		
		
		return new Point((int)(goalPoint.getX() + Math.cos(direction) * distanceFromGoal), (int)(goalPoint.getY() + Math.sin(direction) * distanceFromGoal));
	}

	/**
	 * Change the destination of the robot
	 * @param newDestination the new destination
	 */
	private void changeDestination(Point newDestination) {
		if (newDestination != null) {
			go.setDestination(newDestination);
			go.setTarget(enemyRobot.getPosition());
			go.calculate();
		}
	}
}