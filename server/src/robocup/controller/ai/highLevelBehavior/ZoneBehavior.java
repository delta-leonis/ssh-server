package robocup.controller.ai.highLevelBehavior;

import java.util.ArrayList;

import robocup.controller.ai.highLevelBehavior.strategy.defense.ExampleStrategy;
import robocup.controller.ai.highLevelBehavior.zoneBehavior.AttackMode;
import robocup.controller.ai.highLevelBehavior.zoneBehavior.DefenseMode;
import robocup.controller.ai.highLevelBehavior.zoneBehavior.Mode;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.Ball;
import robocup.model.Robot;
import robocup.model.World;

public class ZoneBehavior extends Behavior {

	private World world;
	private DefenseMode defenseMode;
	private AttackMode attackMode;
	private Mode currentMode;
	private Ball ball;

	/**
	 * Create the a ZoneBehavior.
	 * DefenseMode and AttackMode will be created as well.
	 * @param executers the list containing all RobotExecuters
	 */
	public ZoneBehavior(ArrayList<RobotExecuter> executers) {
		world = World.getInstance();
		ball = world.getBall();

		defenseMode = new DefenseMode(new ExampleStrategy(), executers);
		attackMode = new AttackMode(new ExampleStrategy(), executers);
	}

	/**
	 * Execute the ZoneBehavior.
	 * Mode will be determined and executed based on ball and team positioning.
	 */
	@Override
	public void execute(ArrayList<RobotExecuter> executers) {
		if (eventHappened())
			currentMode = determineMode();

		currentMode.execute(executers);
	}

	/**
	 * Event based system, using events like:
	 * referee command
	 * ball changing owner
	 * ball changing field half
	 * @return true when event has happened
	 */
	private boolean eventHappened() {
		// TODO calculate if event has happened
		return true;
	}

	/**
	 * Determine which Mode needs to be used.
	 * @return AttackMode when our team is closer to the ball. DefenseMode when the enemy team is closer to the ball.
	 */
	private Mode determineMode() {
		// TODO create a new mode and determine strategy instead of using standard examplestrategy
		return allyHasBall() ? attackMode : defenseMode;
	}

	/**
	 * @deprecated use execute instead.
	 */
	@Override
	public void updateExecuters(ArrayList<RobotExecuter> executers) {
		// Use execute instead, this is deprecated
	}

	/**
	 * Calculate if ally team is closer to the ball
	 * @return true when the ally team is closer
	 */
	private boolean allyHasBall() {
		ArrayList<Robot> allies = world.getReferee().getAlly().getRobots();
		ArrayList<Robot> enemies = world.getReferee().getEnemy().getRobots();

		int distanceAlly = getTeamDistanceToBall(allies);
		int distanceEnemy = getTeamDistanceToBall(enemies);

		return distanceAlly <= distanceEnemy;
	}

	/**
	 * Get the distance from the closest robot in one team to the ball
	 * @param robots the team of robots
	 * @return the distance of the closest robot
	 */
	private int getTeamDistanceToBall(ArrayList<Robot> robots) {
		if (ball == null)
			return Integer.MAX_VALUE;

		int minDistance = -1;

		for (Robot r : robots) {
			if (minDistance == -1)
				minDistance = (int) r.getPosition().getDeltaDistance(ball.getPosition());
			else {
				int distance = (int) r.getPosition().getDeltaDistance(ball.getPosition());

				if (distance < minDistance)
					minDistance = distance;
			}
		}

		return minDistance;
	}
}
