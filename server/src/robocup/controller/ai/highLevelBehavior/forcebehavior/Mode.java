/**
 * abstract class for all modes like attack or defence
 */
package robocup.controller.ai.highLevelBehavior.forcebehavior;

import java.util.ArrayList;

import robocup.controller.ai.highLevelBehavior.forcebehavior.forceCalculator.FieldForces;
import robocup.controller.ai.lowLevelBehavior.FuckRobot;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.Ball;
import robocup.model.Point;
import robocup.model.Referee;
import robocup.model.Robot;
import robocup.model.World;

public abstract class Mode {

	@SuppressWarnings("unused")
	private FieldForces fieldForces;

	@SuppressWarnings("unused")
	private RobotExecuter[] robotExcecuter;
	
	protected World world;
	
	public enum roles { KEEPER, DEFENDER, ATTACKER, BLOCKER };

	/**
	 * Let the calculator recalculate all forces
	 */
	public abstract void setFieldForce();

	/**
	 * Let force calculator determine forces
	 * then update or generate low level behaviors
	 * @param executers
	 */
	public abstract void execute(ArrayList<RobotExecuter> executers);
	
	/**
	 * Generate or Update a low level behavior for this executer
	 * @param executer execute the executer
	 * @param type type of the low level behavior
	 * @param isUpdate false if a new behavior should be created, true if update is requred
	 */
	public abstract void updateExecuter(RobotExecuter executer, roles type, boolean isUpdate);
	
	
	public abstract void updateExecuters(ArrayList<RobotExecuter> executers) ;
	
	
	
	
	/**
	 * Find a free position for the robot A position is free when the robot can
	 * get the ball passed
	 * 
	 * @param robot
	 *            the robot who needs a free position
	 * @return a free position
	 */
	protected Point getFreePosition(Robot robot) {
		return new Point(-500, 0);
	}

	/**
	 * Get the closest robot to the ball on our team
	 * 
	 * @return closest robot
	 */
	protected Robot getClosestAllyRobotToBall(World world) {
		Ball ball = world.getBall();
		ArrayList<Robot> robots = world.getAlly().getRobots();

		int minDistance = -1;
		Robot closestRobot = null;

		for (Robot r : robots) {
			if (minDistance == -1) {
				closestRobot = r;
				minDistance = (int) r.getPosition().getDeltaDistance(ball.getPosition());
			} else {
				int distance = (int) r.getPosition().getDeltaDistance(ball.getPosition());

				if (distance < minDistance) {
					closestRobot = r;
					minDistance = distance;
				}
			}
		}

		return closestRobot;
	}

	/**
	 * Calculate if ally team is closer to the ball
	 * 
	 * @return true when the ally team is closer
	 */
	protected boolean allyHasBall(World world) {
		ArrayList<Robot> allies = world.getAlly().getRobots();
		ArrayList<Robot> enemies = world.getEnemy().getRobots();

		int distanceAlly = getTeamDistanceToBall(world, allies);
		int distanceEnemy = getTeamDistanceToBall(world, enemies);

		return distanceAlly < distanceEnemy;
	}

	/**
	 * Get the distance from the closest robot in one team to the ball
	 * 
	 * @param robots
	 *            the team of robots
	 * @return the distance of the closest robot
	 */
	protected int getTeamDistanceToBall(World world, ArrayList<Robot> robots) {
		int minDistance = -1;
		Ball ball = world.getBall();

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

	/**
	 * Get the closest enemy robot to robot
	 * 
	 * @return closest robot
	 */
	protected Robot getClosestEnemyToRobot(Robot robot, boolean withoutBlocker, ArrayList<RobotExecuter> executers) {
		World world = World.getInstance();
		ArrayList<Robot> robots = world.getEnemy().getRobots();

		int minDistance = -1;
		Robot closestRobot = null;

		for (Robot r : robots) {
			if (minDistance == -1) {
				closestRobot = r;
				minDistance = (int) r.getPosition().getDeltaDistance(robot.getPosition());
			} else {
				int distance = (int) r.getPosition().getDeltaDistance(robot.getPosition());

				if (distance < minDistance) {
					if (withoutBlocker) {
						// Only continue of the robot has no blocker assigned
						if (!robotHasBlocker(r, executers)) {
							closestRobot = r;
							minDistance = distance;
						}
					} else {
						closestRobot = r;
						minDistance = distance;
					}
				}
			}
		}

		return closestRobot;
	}

	/**
	 * Find out if the robot has a blocker assigned to it
	 * 
	 * @param robot
	 * @return bool if true, a blocker is assigned
	 */
	protected boolean robotHasBlocker(Robot robot, ArrayList<RobotExecuter> executers) {

		for (RobotExecuter executer : executers) {
			if (executer.getLowLevelBehavior().getRole() == roles.BLOCKER) {

				if (robot.getRobotID() == ((FuckRobot) executer.getLowLevelBehavior()).getOpponentId()) {
					return true;
				}
			}
		}

		return false;
	}
	
	
	/**
	 * Helper function for referee commands, checks last command issued 
	 * 
	 * @param robotID
	 * @return bool indicating if movement is allowed
	 */
	protected boolean isAllowedToMove(World world, int robotID) {
		// Get last command
		Referee ref = world.getReferee();
		String refCommand = "";
		String refStage = "";
		if(ref != null) {
			if(ref.getCommand() != null) { 
				refCommand = ref.getCommand().toString();
				
			}
			if(ref.getStage() != null) {
				refStage = ref.getStage().toString();
			}
		}
 
		// Halt = all robots stop
		if(refCommand.equals("HALT")) return false;
		
		// Stop = keep 50cm from ball
		if(refCommand == "STOP") {
			//System.out.println("STOP!, HAMERZEIT. 50cm buffer zone from the ball");
			
			// if the distance to ball is less then 50cm, is so return false
			if((int) world.getAlly().getRobotByID(robotID).getPosition().getDeltaDistance(world.getBall().getPosition()) < 500) {
				//System.out.println("To close to the ball, access revoked");
				return false;
			}

		// Goal = Should be treated the same as STOP
		} else if(refCommand == "GOAL_YELLOW" || refCommand == "GOAL_BLUE") {
			//System.out.println("STOP! GOALZEIT!. A team has scored, should be treated as a STOP");
			return false;
		}

		//System.out.println("Movement approved based on command:" + refCommand + " and during stage: " + refStage);
		return true;
	}
}