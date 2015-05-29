package robocup.controller.ai.highLevelBehavior.events;

import robocup.model.Ally;
import robocup.model.Ball;
import robocup.model.Enemy;
import robocup.model.FieldPoint;
import robocup.model.Referee;
import robocup.model.Robot;
import robocup.model.World;
import robocup.model.enums.Command;
import robocup.model.enums.Event;
import robocup.model.enums.GameState;

/**
 * Class used to determine events which happen on the field
 * See tactics documentation for an explanation on every event
 * All possible events are declared in Event.java
 */
public class EventSystem {

	private World world;
	private Referee referee;
	private Ball ball;

	private GameState previousGameState = null;
	private GameState currentGameState = null;
	private Robot previousBallOwner = null;
	private Robot currentBallOwner = null;
	private Command previousCommand = null;
	private Command currentCommand = null;
	private FieldPoint previousBallPosition = null;
	private FieldPoint currentBallPosition = null;
	private int previousEnemyCountOnAttackingHalf = 0;
	private int currentEnemyCountOnAttackingHalf = 0;

	/**
	 * Create an event system.
	 */
	public EventSystem() {
		world = World.getInstance();
		referee = world.getReferee();
		ball = world.getBall();

		currentGameState = world.getGameState();
		currentBallOwner = world.getClosestRobotToBall();
		currentCommand = referee.getCommand();
		currentBallPosition = ball.getPosition();

		currentEnemyCountOnAttackingHalf = world.getAttackingEnemiesCount();
	}

	/**
	 * Get the current event. Returns null when no event has happened.
	 */
	public Event getNewEvent() {
		previousBallPosition = currentBallPosition;
		currentBallPosition = ball.getPosition();

		previousGameState = currentGameState;
		currentGameState = world.getGameState();

		previousCommand = currentCommand;
		currentCommand = referee.getCommand();

		previousEnemyCountOnAttackingHalf = currentEnemyCountOnAttackingHalf;
		currentEnemyCountOnAttackingHalf = world.getAttackingEnemiesCount();

		if (currentCommand != previousCommand)
			return Event.REFEREE_NEWCOMMAND;

		if (currentGameState != previousGameState)
			return Event.GAMESTATE_CHANGED;

		if (ball.getSpeed() < 100.0) {
			previousBallOwner = currentBallOwner;
			currentBallOwner = world.getClosestRobotToBall();
			if (previousBallOwner == null) {
				previousBallOwner = currentBallOwner;
			}
			if (previousBallOwner instanceof Ally) {
				if (currentBallOwner instanceof Enemy) //Previous Ally, current Enemy
					return Event.BALL_ENEMY_CAPTURE;

				else if (previousBallOwner.getRobotId() != currentBallOwner.getRobotId()) //Ally retains possession of ball
					return Event.BALL_ALLY_CHANGEOWNER;
			} else { // previousBallOwner instanceof Enemy
				if (currentBallOwner instanceof Ally) //Previous Enemy, current Ally
					return Event.BALL_ALLY_CAPTURE;

				else if (previousBallOwner != null && currentBallOwner != null && previousBallOwner.getRobotId() != currentBallOwner.getRobotId()) //Enemy retains possession of ball
					return Event.BALL_ENEMY_CHANGEOWNER;
			}
		}

		if (previousBallPosition.getX() < 0 && currentBallPosition.getX() > 0 || previousBallPosition.getX() > 0
				&& currentBallPosition.getX() < 0)
			return Event.BALL_MOVESPAST_MIDLINE;

		if (previousBallPosition.getY() < 0 && currentBallPosition.getY() > 0 || previousBallPosition.getY() > 0
				&& currentBallPosition.getY() < 0)
			return Event.BALL_MOVESPAST_NORTHSOUTH;

		if (previousEnemyCountOnAttackingHalf != currentEnemyCountOnAttackingHalf)
			return Event.ROBOT_ENEMY_ATTACKCOUNT_CHANGE;

		return null;
	}
}
