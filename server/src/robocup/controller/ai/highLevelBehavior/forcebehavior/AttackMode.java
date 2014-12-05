/**
 * Use this class to control the low level behaviors in attack mode
 */
package robocup.controller.ai.highLevelBehavior.forcebehavior;

import java.util.ArrayList;

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

	private static final Point MID_GOAL_NEGATIVE = new Point(-(World
			.getInstance().getField().getLength() / 2), 0);
	private static final Point MID_GOAL_POSITIVE = new Point(World
			.getInstance().getField().getLength() / 2, 0);

	private Point offset = new Point(0, 0);
	private int shootDirection = -100;
	private ArrayList<RobotExecuter> executers;
	private int lastCommandCounter = 0;

	public AttackMode(ArrayList<RobotExecuter> executers) {
		world = World.getInstance();

		// Set all behaviors / assign

		// 1e executer = keeper
		// 2e + 3e = defender
		// rest is attacker
		updateExecuters(executers);
	}

	public void updateExecuters(ArrayList<RobotExecuter> executers) {
		this.executers = executers;
		// int executerCounter = 0;

		//System.out.println("attackmode: updateExecuters");
		for (RobotExecuter executer : executers) {

			if (executer.getRobot().getRobotID() == 1) {
				//System.out.println(executer.getRobot().getRobotID()
				//		+ "  is nu keeper");
				updateExecuter(executer, roles.KEEPER, false);
			} else {
				//System.out.println(executer.getRobot().getRobotID()
				//		+ "  is nu defender");
				updateExecuter(executer, roles.DEFENDER, false);
			}

			// // Keeper
			// System.out.println("rowid:  " +
			// executer.getRobot().getRobotID());
			// if (executerCounter == 0) {
			// System.out.println(" assign keepert");
			// updateExecuter(executer, roles.KEEPER, false);
			// // Defender
			// } else if (executerCounter <= 2) {
			//
			// System.out.println(" assign defender");
			// updateExecuter(executer, roles.DEFENDER, false);
			// // Attacker
			// } else {
			// System.out.println(" assign attacker");
			// updateExecuter(executer, roles.ATTACKER, false);
			// }
			//
			// executerCounter++;
		}
	}

	@Override
	public void setFieldForce() {
		throw new NotImplementedException();
	}

	@Override
	public void execute(ArrayList<RobotExecuter> executers) {

		try {
			for (RobotExecuter executer : executers) {
				updateExecuter(executer, executer.getLowLevelBehavior()
						.getRole(), true);
			}
		} catch (Exception e) {
			//System.out.println("Ik ben dood in attackmode");
			e.printStackTrace();
		}
	}

	@Override
	// Genereert EN update een executer met een lowlevel behaviour
	public void updateExecuter(RobotExecuter executer, roles type,
			boolean isUpdate) {

		Robot robot = executer.getRobot();
		Ball ball = world.getBall();
		int distanceToGoal = offset != null ? 1000 : 500;
		int keeperDistanceToGoal = 500;

		// new assigment, assign to robot
		// if(!isUpdate) {
		robot.setRole(type);
		// }

		// Can I move?
		if (isAllowedToMove(world, robot.getRobotID()) && isUpdate) {

			// Allowed to move
			//System.out.println(robot.getRobotID() + " is allowed to move");

		} else if (!isUpdate) {

			//System.out.println("assignment, no update");

		} else {
			//System.out.println(robot.getRobotID()
			//		+ " is forbidden to move an Inch");

			// !!TODO implement break and/or holding patterns for robots
			// If it time for a penalty? getClosestAllyRobotToBall() and send him to the ball
			
			// ! TODO implement a free-pass for the robot doing the kickoff or
			// penalty

		}

		Referee ref = world.getReferee();
		String refCommand = "";
		if(ref != null) {
			if(ref.getCommand() != null) {
				refCommand = ref.getCommand().toString();
				
				// Does our team have to prepare for kickoff?
				if(refCommand.equals(("PREPARE_KICKOFF_" + world.getAlly().getColor().toString()))) {

					// Find robot closest to the ball, is it "me"?
					Robot kickoffRobot = getClosestAllyRobotToBall(world);
					if(kickoffRobot.getRobotID() == robot.getRobotID()) {
						
						// Grab attacker???? and to ball position
						
					}
				}
			}
			
			
			// A new referee command was issued
			if(ref.getCommandCounter() > lastCommandCounter) {
				//System.out.println("Command received: " + refCommand);
			}
		}
		
		

		
		
		/* 
		 * // Check for referee-updates / commands Referee ref =
		 * world.getReferee();
		 * 
		 * // A new referee command was issued if(ref.getCommandCounter() >
		 * lastCommandCounter) {
		 * 
		 * String refCommand = ref.getCommand().toString(); String refStage =
		 * ref.getStage().toString(); System.out.println(refCommand);7
		 * System.out.println(refStage);
		 * 
		 * if(refCommand.equals("STOP")) { // About to receive another command,
		 * set fag for the next iteration
		 * 
		 * }
		 * 
		 * lastCommandCounter = ref.getCommandCounter() ; }
		 */

		switch (type) {
		case KEEPER:
 
			if (isUpdate) {
				((Keeper) executer.getLowLevelBehavior()).update(
						keeperDistanceToGoal, false, ball.getPosition(),
						robot.getPosition());
			} else {

				executer.setLowLevelBehavior(new Keeper(robot, ComInterface
						.getInstance(RobotCom.class), keeperDistanceToGoal,
						false, ball.getPosition(), robot.getPosition(), robot
								.getPosition().getX() > 0 ? MID_GOAL_POSITIVE
								: MID_GOAL_NEGATIVE, world.getField()
								.getWidth() / 2));
			}
			break;

		case DEFENDER:
			if (isUpdate) {
				((KeeperDefender) executer.getLowLevelBehavior()).update(
						distanceToGoal, false, ball.getPosition(),
						robot.getPosition());
			} else {
				executer.setLowLevelBehavior(new KeeperDefender(robot,
						ComInterface.getInstance(RobotCom.class),
						distanceToGoal, false, ball.getPosition(), robot
								.getPosition(),
						robot.getPosition().getX() > 0 ? MID_GOAL_POSITIVE
								: MID_GOAL_NEGATIVE, offset, world.getField()
								.getWidth() / 2));
			}
			break;

		case ATTACKER:
			Point freePosition = getClosestAllyRobotToBall(world) == robot ? null
					: getFreePosition(null);

			if (isUpdate) {
				((Attacker) executer.getLowLevelBehavior()).update(
						freePosition, ball.getPosition(), 0, 0, shootDirection);
			} else {
				executer.setLowLevelBehavior(new Attacker(robot, ComInterface
						.getInstance(RobotCom.class), freePosition, ball
						.getPosition(), 0, 0, shootDirection));
			}

			break;

		case BLOCKER: // fuckrobot
			// Determine closest robot who does not yet have a blocker
			Robot opponent = getClosestEnemyToRobot(robot, true, executers);

			int distanceToOpponent = 250;

			if (isUpdate) {
				((FuckRobot) executer.getLowLevelBehavior()).update(
						distanceToOpponent, ball.getPosition(),
						robot.getPosition(), opponent.getPosition(),
						opponent.getRobotID());
			} else {
				executer.setLowLevelBehavior(new FuckRobot(robot, ComInterface
						.getInstance(RobotCom.class), distanceToOpponent, ball
						.getPosition(), robot.getPosition(), opponent
						.getPosition(), opponent.getRobotID()));
			}
			break;
		}
	}
}