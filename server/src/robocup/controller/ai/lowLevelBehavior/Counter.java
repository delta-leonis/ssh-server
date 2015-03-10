package robocup.controller.ai.lowLevelBehavior;

import robocup.controller.ai.movement.GotoPosition;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.model.enums.RobotMode;
import robocup.output.ComInterface;

/**
 * Counter
 * Houdt vrije lijn tussen bal en zichzelf binnen zone ‘middenTegenstander’
 * 
 * Dekker
 * Bezet lijn tussen tegenstander en bal in zone met volgende prioriteit: 1. ‘2ePaal 2. ‘Hoek 3. ‘Zijkant’
 * 
 * DekkerStoorder	//TODO: Sub-klasse.
 * Blijft binnen zone ‘Zijkant’. Bezet de lijn tussen tegenstander in zijn zone als de bal aan de andere breedte van het veld is en er een tegenstander is.
 * Bezet de lijn tussen bal en eigen goal als deze in de zone is.
 * 
 * TODO: Rename.
 * TODO: English-fy
 * TODO: Implements zones.
 */
public class Counter extends LowLevelBehavior {

	private Point zone;
	private Point ballPosition;
	private boolean dribble = false;

	/**
	 * Create Counter LowLevelBevahiour
	 * @param robot the Counter {@link Robot} in the model.
	 * @param output Used to send data to the Robot
	 * @param TODO: vrije zone.
	 * @param ballPosition the position of the ball
	 * @param TODO: lijst met robots/obstakels
	 * @param dribble enable dribbler
	 */
	public Counter(Robot robot, ComInterface output, Point zone, Point ballPosition,
			boolean dribble) {
		super(robot, output);
		this.zone = zone;
		this.ballPosition = ballPosition;
		this.dribble = dribble;
		this.role = RobotMode.ATTACKER;
		go = new GotoPosition(robot, output, null, ballPosition);
	}

	/**
	 * Update
	 * @param zone a free position on the field. If not null, the Robot should go here
	 * @param ballPosition the position of the ball
	 * @param dribble enable dribbler
	 */
	public void update(Point zone, Point ballPosition, boolean dribble) {
		this.zone = zone;
		this.ballPosition = ballPosition;
		this.dribble = dribble;
	}

	@Override
	public void calculate() {
		if (timeOutCheck()) {

		} else {
			Point newDestination = null;
			go.setTarget(ballPosition);
			go.setDribble(dribble);

			
			// Move towards a free position when given
			if (zone != null){
				// TODO
				// Calculate a free path from ball towards zone, based on the robots on the field.
				// If there is no free path, move to middle of zone.
			}
			go.setDestination(null);
		}
		go.calculate();
	}
}