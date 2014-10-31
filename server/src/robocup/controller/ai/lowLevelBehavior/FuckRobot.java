package robocup.controller.ai.lowLevelBehavior;

import robocup.controller.ai.highLevelBehavior.forcebehavior.Mode;
import robocup.controller.ai.movement.GotoPosition;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.output.ComInterface;

public class FuckRobot extends LowLevelBehavior {

	protected Point ballPosition;
	protected Point opponentPosition;
	protected Point defenderPosition;
	protected int distanceToOpponent;
	protected int opponentId;
	
	/**
	 * Create a defender (stands between "target" enemy and the ball)
	 * @param robot
	 * @param output
	 * @param distanceToOpponent The distance the defender keeps from the enemy (center)
	 * @param ballPosition current position of the ball
	 * @param defenderPosition current position of the defender (this robot)
	 * @param opponentPosition center position of the opponent / enemy
	 */
	public FuckRobot(Robot robot, ComInterface output, int distanceToOpponent, Point ballPosition,
			Point defenderPosition, Point opponentPosition, int opponentId) {
		super(robot, output);
		
		this.opponentPosition = opponentPosition;
		this.ballPosition = ballPosition;
		this.distanceToOpponent = distanceToOpponent;
		this.defenderPosition = defenderPosition;
		this.role = Mode.roles.BLOCKER;
		this.opponentId = opponentId;
		go = new GotoPosition(robot, output, defenderPosition, opponentPosition, 400);
	}
	
	
	/**
	 * Update
	 * @param distanceToOpponent
	 * @param ballPosition
	 * @param defenderPosition
	 * @param opponentPosition
	 */
	public void update(
			int distanceToOpponent, Point ballPosition, Point defenderPosition, 
			Point opponentPosition, int opponentId) {
		this.distanceToOpponent = distanceToOpponent;
		this.ballPosition = ballPosition;
		this.defenderPosition = defenderPosition;
		this.opponentPosition = opponentPosition;
		this.opponentId = opponentId;
	}
	
	@Override
	public void calculate() {
		// Only run if the robot isn't timed out
		if(!timeOutCheck()) {
			Point newDestination = getNewDestination();
			// If available, set the new destination
			if(newDestination != null) {
				go.setGoal(newDestination);
				go.setTarget(ballPosition);
				go.calculate();
			}
		}
	}
	
	/**
	 * Returns the opponent id
	 * @return
	 */
	public int getOpponentId() {
		return opponentId;
	}

	/**
	 * Get the destination
	 */
	private Point getNewDestination() {
		Point newDestination = null;
		
		// Ball has to be on the field
		if(ballPosition != null) {
			
			// Get angles from the opponent towards the ball
			int angle = Math.abs(opponentPosition.getAngle(ballPosition));
			int realAngle = angle > 90 ? 180 - angle : angle;
			
			// Calculate DX DY
			double dx = Math.sin(Math.toRadians(realAngle)) * distanceToOpponent;
			double dy = Math.sqrt(distanceToOpponent * distanceToOpponent - dx * dx);
			
			// Get opponent positions
			int opponentX = (int) opponentPosition.getX();
			int opponentY = (int) opponentPosition.getY();
			
			// Calculate target position X
			int destX = (int) (ballPosition.getX() > opponentPosition.getX() ? opponentX + dx : opponentX - dx);
			if(ballPosition.getX() < 0) {
				destX = (int) (ballPosition.getX() < opponentPosition.getX() ? opponentX - dx : opponentX + dx);
			}
			
			// Calculate target position Y
			int destY = (int) (ballPosition.getY() > opponentPosition.getY() ? opponentY + dy : opponentY - dy);
			if(ballPosition.getY() < 0) {
				destY = (int) (ballPosition.getY() < opponentPosition.getY() ? opponentY - dy : opponentY + dy);
			}
			
			// Set destination
			newDestination = new Point(destX, destY);
		}

		return newDestination;
	}
	
}
