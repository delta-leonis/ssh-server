package robocup.controller.ai.highLevelBehavior.forcebehavior.forceCalculator;

import java.util.ArrayList;

import robocup.model.Ball;
import robocup.model.Robot;
import robocup.model.World;
import robocup.controller.ai.highLevelBehavior.Behavior;
import robocup.controller.ai.highLevelBehavior.forcebehavior.AttackMode;
import robocup.controller.ai.highLevelBehavior.forcebehavior.DefenceMode;
import robocup.controller.ai.highLevelBehavior.forcebehavior.Mode;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;

public class Force extends Behavior {
	
	private World world;
	
	private ArrayList<Mode> modes;
	private Mode currentMode;

//	private Mode[] forceBehaviors;

	private Ball ball;
	
	public Force() {
		world = World.getInstance();
		ball = world.getBall();
		
		modes.add(new DefenceMode());
		modes.add(new AttackMode());
	}
	
	private Mode determineMode() {
		//						defence			attack
		return allyHasBall() ? modes.get(0) : modes.get(1);
		
		
		//calculate the most effective mode to play in, being either attack or defensive playstyles 
		
		//why attack and defense
	}

	@Override
	public void execute(ArrayList<RobotExecuter> executers) {
		// determine current mode and execute it
		currentMode = determineMode();
		currentMode.execute(executers);
	}
	
	/**
	 * Calculate if ally team is closer to the ball
	 * @return true when the ally team is closer
	 */
	private boolean allyHasBall() {
		ArrayList<Robot> allies = world.getAlly().getRobots();
		ArrayList<Robot> enemies = world.getEnemy().getRobots();
		
		int distanceAlly = getTeamDistanceToBall(allies);
		int distanceEnemy = getTeamDistanceToBall(enemies);

		return distanceAlly < distanceEnemy;
	}

	/**
	 * Get the distance from the closest robot in one team to the ball
	 * @param robots the team of robots
	 * @return the distance of the closest robot
	 */
	private int getTeamDistanceToBall(ArrayList<Robot> robots) {
		int minDistance = -1;
		
		for(Robot r : robots) {
			if(minDistance == -1)
				minDistance = (int) r.getPosition().getDeltaDistance(ball.getPosition());
			else {
				int distance = (int) r.getPosition().getDeltaDistance(ball.getPosition());
				
				if(distance < minDistance) {
					minDistance = distance;
				}
			}
		}
		
		return minDistance;
	}
}