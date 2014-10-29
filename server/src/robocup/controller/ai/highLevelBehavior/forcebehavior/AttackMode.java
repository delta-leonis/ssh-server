/**
 * Use this class to control the low level behaviors in attack mode
 */
package robocup.controller.ai.highLevelBehavior.forcebehavior;

import java.util.ArrayList;

import robocup.Main;
import robocup.controller.ai.lowLevelBehavior.Attacker;
import robocup.controller.ai.lowLevelBehavior.FuckRobot;
import robocup.controller.ai.lowLevelBehavior.Keeper;
import robocup.controller.ai.lowLevelBehavior.KeeperDefender;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.*;
import robocup.output.ComInterface;
import robocup.output.RobotCom;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class AttackMode extends Mode {

	private static final Point MID_GOAL_NEGATIVE = 
		new Point(-(World.getInstance().getField().getLength() / 2), 0);
	private static final Point MID_GOAL_POSITIVE = 
		new Point(World.getInstance().getField().getLength() / 2, 0);
	
	private Point offset = new Point(0, 0);
	private World world;
	private int shootDirection = -100;
	
	public AttackMode() {
		world = World.getInstance();
	}
	
	@Override
	public void setFieldForce() {
		throw new NotImplementedException();
	}

	@Override
	public void execute(ArrayList<RobotExecuter> executers) {
		for(RobotExecuter executer : executers) {
			updateExecuter(executer, executer.getLowLevelBehavior().getRole(), true);
		}

	}

	@Override
	// Genereert EN update een executer met een lowlevel behaviour
	public void updateExecuter(RobotExecuter executer, roles type, boolean isUpdate) {
		//	Todo Assign behaviour
		
		Robot robot = executer.getRobot();
		Ball ball = world.getBall();
		int distanceToGoal = offset != null ? 1000 : 500;
		
		switch(type) {
			case KEEPER:
				if(isUpdate) {
					((Keeper)executer.getLowLevelBehavior()).update(
						distanceToGoal, false, ball.getPosition(), robot.getPosition()
					);
				} else {
					executer.setLowLevelBehavior(
						new Keeper( robot, ComInterface.getInstance(RobotCom.class), 
							distanceToGoal, false, 
							ball.getPosition(), robot.getPosition(), 
							robot.getPosition().getX() > 0 ? MID_GOAL_POSITIVE : MID_GOAL_NEGATIVE, 
							world.getField().getWidth() / 2)
					);
				}
				break;
				
			case DEFENDER:
				if(isUpdate) {
					((KeeperDefender)executer.getLowLevelBehavior()).update(
						distanceToGoal, false, ball.getPosition(), robot.getPosition()
					);
				} else {
					executer.setLowLevelBehavior(
						new KeeperDefender( robot, ComInterface.getInstance(RobotCom.class), 
							distanceToGoal, false, ball.getPosition(), robot.getPosition(),
							robot.getPosition().getX() > 0 ? MID_GOAL_POSITIVE : MID_GOAL_NEGATIVE,
							offset, world.getField().getWidth() / 2)
					);
				}
				break;
				
			case ATTACKER:
				Point freePosition = 
					getClosestAllyRobotToBall() == robot ? null : getFreePosition(null);
				
				if(isUpdate) {
					((Attacker)executer.getLowLevelBehavior()).update(
						freePosition, ball.getPosition(), 0, 0, shootDirection
					);
				} else {
					executer.setLowLevelBehavior(
						new Attacker( robot, ComInterface.getInstance(RobotCom.class), 
							freePosition, ball.getPosition(), 0, 0, shootDirection)
					);
				}
					
				
				break;
				
			case BLOCKER: //fuckrobot
				// Determine closest robot who does not yet have a blocker
				Robot opponent = getClosestEnemyToRobot(robot, true);
				
				int distanceToOpponent = 250;
				
				if(isUpdate) {
					((FuckRobot)executer.getLowLevelBehavior()).update(
						distanceToOpponent, ball.getPosition(), 
						robot.getPosition(), opponent.getPosition()
					);
				} else {
					executer.setLowLevelBehavior(
						new FuckRobot( robot, ComInterface.getInstance(RobotCom.class), 
							distanceToOpponent, ball.getPosition(), 
							robot.getPosition(), opponent.getPosition())
					);
				}
				break;
		}
	}
	
	
	
	
	
	/**
	 * Find a free position for the robot
	 * A position is free when the robot can get the ball passed
	 * @param robot the robot who needs a free position
	 * @return a free position
	 */
	private Point getFreePosition(Robot robot) {
		return new Point(-500, 0);
	}

	
	/**
	 * Get the closest robot to the ball on our team
	 * @return closest robot
	 */
	private Robot getClosestAllyRobotToBall() {
		Ball ball = world.getBall();
		ArrayList<Robot> robots = world.getAlly().getRobots();

		int minDistance = -1;
		Robot closestRobot = null;
		
		for(Robot r : robots) {
			if(minDistance == -1) {
				closestRobot = r;
				minDistance = (int) r.getPosition().getDeltaDistance(ball.getPosition());
			} else {
				int distance = (int) r.getPosition().getDeltaDistance(ball.getPosition());
				
				if(distance < minDistance) {
					closestRobot = r;
					minDistance = distance;
				}
			}
		}
		
		return closestRobot;
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
		Ball ball = world.getBall();
		
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
	
	
	/**
	 * Get the closest enemy robot to robot
	 * @return closest robot
	 */
	private Robot getClosestEnemyToRobot(Robot robot, boolean withoutBlocker) {
		World world = World.getInstance();
		ArrayList<Robot> robots = world.getEnemy().getRobots();

		int minDistance = -1;
		Robot closestRobot = null;
		
		for(Robot r : robots) {
			if(minDistance == -1) {
				closestRobot = r;
				minDistance = (int) r.getPosition().getDeltaDistance(robot.getPosition());
			} else {
				int distance = (int) r.getPosition().getDeltaDistance(robot.getPosition());
				
				if(distance < minDistance) {
					if(withoutBlocker) {
						// If robot already has a blocker, search for another one
						
						
						//!TODO check if enemy has blocker, if not, asign him
						/*
						 * closestRobot = r;
						minDistance = distance;
						 */
						
					} else {
						closestRobot = r;
						minDistance = distance;
					}
				}
			}
		}
		
		return closestRobot;
	}
}