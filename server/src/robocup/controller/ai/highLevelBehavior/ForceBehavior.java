package robocup.controller.ai.highLevelBehavior;

import java.util.ArrayList;

import robocup.model.Ball;
import robocup.model.Robot;
import robocup.model.World;
import robocup.controller.ai.highLevelBehavior.forcebehavior.AttackMode;
import robocup.controller.ai.highLevelBehavior.forcebehavior.DefenceMode;
import robocup.controller.ai.highLevelBehavior.forcebehavior.Mode;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;

public class ForceBehavior extends Behavior {

	private World world;

	private AttackMode attackMode;
	@SuppressWarnings("unused")
	private DefenceMode defenceMode;

	private Mode currentMode;

	// private Mode[] forceBehaviors;

	private Ball ball;

	public ForceBehavior(ArrayList<RobotExecuter> executers) {
		world = World.getInstance();
		ball = world.getBall();

		defenceMode = new DefenceMode(executers);
		attackMode = new AttackMode(executers);
	}

	private Mode determineMode() {

		// !TODO smerige test hack totdat defence ook bestaat
		return attackMode;

		/* if(allyHasBall()) { // Attack return attackMode; } else { // Defence
		 * return defenceMode; } */

		// calculate the most effective mode to play in, being either attack or
		// defensive playstyles

		// why attack and defense
	}

	@Override
	public void execute(ArrayList<RobotExecuter> executers) {
		// determine current mode and execute it
		currentMode = determineMode();
		currentMode.execute(executers);
	}

	// !TODO Mooiere manier verzinnen
	public void updateExecuters(ArrayList<RobotExecuter> executers) {
		currentMode = determineMode();
		currentMode.updateExecuters(executers);
	}

	/**
	 * Calculate if ally team is closer to the ball
	 * 
	 * @return true when the ally team is closer
	 */
	@SuppressWarnings("unused")
	private boolean allyHasBall() {
		ArrayList<Robot> allies = world.getReferee().getAlly().getRobotsOnSight();
		ArrayList<Robot> enemies = world.getReferee().getEnemy().getRobotsOnSight();

		int distanceAlly = getTeamDistanceToBall(allies);
		int distanceEnemy = getTeamDistanceToBall(enemies);

		return distanceAlly < distanceEnemy;
	}

	/**
	 * Get the distance from the closest robot in one team to the ball
	 * 
	 * @param robots
	 *            the team of robots
	 * @return the distance of the closest robot
	 */
	private int getTeamDistanceToBall(ArrayList<Robot> robots) {
		int minDistance = -1;

		for (Robot r : robots) {
			if (minDistance == -1)
				minDistance = (int) r.getPosition().getDeltaDistance(ball.getPosition());
			else {
				int distance = (int) r.getPosition().getDeltaDistance(ball.getPosition());

				if (distance < minDistance) {
					minDistance = distance;
				}
			}
		}

		return minDistance;
	}
}
