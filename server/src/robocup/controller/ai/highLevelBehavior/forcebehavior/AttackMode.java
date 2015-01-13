/**
 * Use this class to control the low level behaviors in attack mode
 */
package robocup.controller.ai.highLevelBehavior.forcebehavior;

import java.util.ArrayList;

import robocup.controller.ai.lowLevelBehavior.Attacker;
import robocup.controller.ai.lowLevelBehavior.Blocker;
import robocup.controller.ai.lowLevelBehavior.Keeper;
import robocup.controller.ai.lowLevelBehavior.KeeperDefender;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.*;
import robocup.output.ComInterface;
import robocup.output.RobotCom;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class AttackMode extends Mode {

	private static final Point MID_GOAL_NEGATIVE = new Point(-(World.getInstance().getField().getLength() / 2), 0);
	private static final Point MID_GOAL_POSITIVE = new Point(World.getInstance().getField().getLength() / 2, 0);

	private Point offset = new Point(0, 0);
	private ArrayList<RobotExecuter> executers;
	private int lastCommandCounter = 0;

	private Robot penaltyRobot = null;

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

		// System.out.println("attackmode: updateExecuters");
		for (RobotExecuter executer : executers) {

			if (executer.getRobot().getRobotID() == 1) {
				// System.out.println(executer.getRobot().getRobotID()
				// + "  is nu keeper");
				updateExecuter(executer, roles.KEEPER, false);
			} else {
				// System.out.println(executer.getRobot().getRobotID()
				// + "  is nu defender");
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

		// System.out.println("Update");

		try {
			for (RobotExecuter executer : executers) {
				updateExecuter(executer, executer.getLowLevelBehavior().getRole(), true);
			}
		} catch (Exception e) {
			// System.out.println("Ik ben dood in attackmode");
			e.printStackTrace();
		}
	}

	@Override
	// Genereert EN update een executer met een lowlevel behaviour
	public void updateExecuter(RobotExecuter executer, roles type, boolean isUpdate) {

		Robot robot = executer.getRobot();
		Ball ball = world.getBall();
		int distanceToGoal = offset != null ? 1000 : 500;
		int keeperDistanceToGoal = 500;

		// new assigment, assign to robot
		// if(!isUpdate) {
		robot.setRole(type);
		// }

		// Can I move?
		if (isAllowedToMove(world, robot.getRobotID())) {
			executer.stop(false);
		} else {
			executer.stop(true);
		}

		// System.out.println(robot.getRobotID()
		// + " is forbidden to move an Inch");

		// !!TODO implement break and/or holding patterns for robots
		// If it time for a penalty? getClosestAttackerRobotToBall() and send
		// him to the ball

		// ! TODO implement a free-pass for the robot doing the kickoff or
		// penalty

		Referee ref = world.getReferee();
		String refCommand = "";
		if (ref != null) {
			if (ref.getCommand() != null) {
				refCommand = ref.getCommand().toString();

				switch (refCommand) {
				case "PREPARE_KICKOFF_BLUE":
				case "PREPARE_KICKOFF_YELLOW":
					break;
				case "PREPARE_PENALTY_BLUE":
				case "PREPARE_PENALTY_YELLOW":
					if (refCommand.equals(("PREPARE_KICKOFF_" + world.getAlly().getColor().toString()))
							&& penaltyRobot == null) {
						penaltyRobot = getClosestAllyRobotToBall(world);
					}

					if (robot != penaltyRobot) {
						return;
					}

					break;
				case "NORMAL_START":
					penaltyRobot = null;
					break;
				default:
					break;
				}
				// Does our team have to prepare for kickoff?
				if (refCommand.equals(("PREPARE_KICKOFF_" + world.getAlly().getColor().toString()))) {

					// Find robot closest to the ball, is it "me"?
					Robot kickoffRobot = getClosestAttackerRobotToBall(world);
					if (kickoffRobot.getRobotID() == robot.getRobotID()) {

						// Grab attacker???? and to ball position

					}
				}
			}

			// A new referee command was issued
			if (ref.getCommandCounter() > lastCommandCounter) {
				// System.out.println("Command received: " + refCommand);
			}
		}

		/* // Check for referee-updates / commands Referee ref =
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
		 * set flag for the next iteration
		 * 
		 * }
		 * 
		 * lastCommandCounter = ref.getCommandCounter() ; } */

		switch (type) {
		case KEEPER:
			handleKeeper(robot, ball, executer, isUpdate, keeperDistanceToGoal);
			break;
		case DEFENDER:
			handleDefender(robot, ball, executer, isUpdate, distanceToGoal);
			break;
		case ATTACKER:
			handleAttacker(robot, ball, executer, isUpdate);
			break;
		case BLOCKER: // fuckrobot
			handleBlocker(robot, ball, executer, isUpdate);
			break;
		}
	}

	private void handleAttacker(Robot robot, Ball ball, RobotExecuter executer, boolean isUpdate) {
		Point freePosition = getClosestAllyRobotToBall(world) == robot ? null : getFreePosition(null);
		int chipKick = 0;
		int shootDirection = 0;
		boolean dribble = false;

		// penalty mode
		if (penaltyRobot != null && penaltyRobot == robot) {
			// move to penalty area to get in range with the ball
			if (penaltyRobot.getPosition().getDeltaDistance(ball.getPosition()) > 100) {
				freePosition = ball.getPosition();
			} else {
				freePosition = null;
			}

			Robot keeper = world.getEnemy().getRobotByID(world.getEnemy().getGoalie());
			shootDirection = getPenaltyDirection(penaltyRobot, keeper, ball);

			// check if robot is able to shoot and if the angle towards the
			// ball is correct, shoot when possible
			if (robot.getPosition().getDeltaDistance(ball.getPosition()) < 100
					&& robot.getOrientation() + 10 > shootDirection && robot.getOrientation() - 10 < shootDirection)
				chipKick = 100;
		} else {

			if (freePosition == null) { // if robot has no free position then it
										// is closest.
				double dDistance = ball.getPosition().getDeltaDistance(robot.getPosition());
				if (dDistance < 150) {
					dribble = true;
				} else if (dDistance < 100) {
					chipKick = -100;
				}
				// robot.getPosition()
				// calculate best tactic, shoot, chip or pass
				// if robot has place free to shoot

				// determine if the robot has the ball, then determine if
				// the
				// robot has a good chance to chip or kick the ball to the
				// goal,
				// else to an ally
			}
		}

		if (isUpdate) {
			((Attacker) executer.getLowLevelBehavior()).update(freePosition, ball.getPosition(), chipKick, dribble,
					shootDirection);
		} else {
			executer.setLowLevelBehavior(new Attacker(robot, ComInterface.getInstance(RobotCom.class), freePosition,
					ball.getPosition(), chipKick, dribble, shootDirection));
		}
	}

	private void handleBlocker(Robot robot, Ball ball, RobotExecuter executer, boolean isUpdate) {
		// Determine closest robot who does not yet have a blocker
		Robot opponent = getClosestEnemyToRobot(robot, true, executers);

		int distanceToOpponent = 250;

		if (isUpdate) {
			((Blocker) executer.getLowLevelBehavior()).update(distanceToOpponent, ball.getPosition(),
					robot.getPosition(), opponent.getPosition(), opponent.getRobotID());
		} else {
			executer.setLowLevelBehavior(new Blocker(robot, ComInterface.getInstance(RobotCom.class),
					distanceToOpponent, ball.getPosition(), robot.getPosition(), opponent.getPosition(), opponent
							.getRobotID()));
		}
	}

	private void handleDefender(Robot robot, Ball ball, RobotExecuter executer, boolean isUpdate, int distanceToGoal) {
		if (isUpdate) {
			((KeeperDefender) executer.getLowLevelBehavior()).update(distanceToGoal, false, ball.getPosition(),
					robot.getPosition());
		} else {
			executer.setLowLevelBehavior(new KeeperDefender(robot, ComInterface.getInstance(RobotCom.class),
					distanceToGoal, false, ball.getPosition(), robot.getPosition(),
					robot.getPosition().getX() > 0 ? MID_GOAL_POSITIVE : MID_GOAL_NEGATIVE, offset, world.getField()
							.getWidth() / 2));
		}
	}

	private void handleKeeper(Robot robot, Ball ball, RobotExecuter executer, boolean isUpdate, int keeperDistanceToGoal) {
		if (isUpdate) {
			((Keeper) executer.getLowLevelBehavior()).update(keeperDistanceToGoal, false, ball.getPosition(),
					robot.getPosition());
		} else {

			executer.setLowLevelBehavior(new Keeper(robot, ComInterface.getInstance(RobotCom.class),
					keeperDistanceToGoal, false, ball.getPosition(), robot.getPosition(),
					robot.getPosition().getX() > 0 ? MID_GOAL_POSITIVE : MID_GOAL_NEGATIVE,
					world.getField().getWidth() / 2));
		}
	}
}
