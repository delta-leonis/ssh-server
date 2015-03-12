
package robocup.controller.ai.highLevelBehavior.forcebehavior;

import java.util.ArrayList;

import robocup.controller.ai.lowLevelBehavior.Attacker;
import robocup.controller.ai.lowLevelBehavior.Coverer;
import robocup.controller.ai.lowLevelBehavior.Keeper;
import robocup.controller.ai.lowLevelBehavior.KeeperDefender;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.Ally;
import robocup.model.Ball;
import robocup.model.Point;
import robocup.model.Referee;
import robocup.model.Robot;
import robocup.model.World;
import robocup.model.enums.Command;
import robocup.model.enums.RobotMode;
import robocup.output.ComInterface;
import robocup.output.RobotCom;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Use this class to control the low level behaviors in attack mode
 */
public class AttackMode extends Mode {
	/** Co-ordinates of the goal on the left side of the field */
	private static final Point MID_GOAL_NEGATIVE = new Point(-(World.getInstance().getField().getLength() / 2), 0);
	/** Co-ordinates of the goal on the right side of the field */
	private static final Point MID_GOAL_POSITIVE = new Point(World.getInstance().getField().getLength() / 2, 0);

	private Point offset = new Point(0, 0);
	private ArrayList<RobotExecuter> executers;
	private int lastCommandCounter = 0;

	private Robot penaltyRobot = null;

	/**
	 * Sets up the AttackMode. 
	 * @param executers
	 */
	public AttackMode(ArrayList<RobotExecuter> executers) {
		world = World.getInstance();

		// Set all behaviors / assign
		updateExecuters(executers);
	}

	public void updateExecuters(ArrayList<RobotExecuter> executers) {
		this.executers = executers;

		// Go through executer lists and update / create the lowlevel behaviors
		for (RobotExecuter executer : executers) {
			if (executer.getRobot().getRobotId() == world.getReferee().getAlly().getGoalie()) {
				if (executer.getLowLevelBehavior() instanceof Keeper) {
					// lowlevel behavior already keeper, update values
					updateExecuter(executer, RobotMode.KEEPER, true);
				} else {
					// lowlevel behavior not a keeper yet,
					// create lowlevel behavior and update
					updateExecuter(executer, RobotMode.KEEPER, false);
				}
			} else {
				if (executer.getLowLevelBehavior() instanceof Attacker) {
					// lowlevel behavior already attacker, update values
					updateExecuter(executer, RobotMode.ATTACKER, true);
				} else {
					// lowlevel behavior not a attacker yet,
					// create lowlevel behavior and update
					updateExecuter(executer, RobotMode.ATTACKER, false);
				}
			}
			// TODO update executer with defender
			// and blocker roles when necessary
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
				updateExecuter(executer, executer.getLowLevelBehavior().getRole(), true);
			}
		} catch (Exception e) {
			// System.out.println("Ik ben dood in attackmode");
			e.printStackTrace();
		}
	}

	@Override
	// Genereert EN update een executer met een lowlevel behaviour
	public void updateExecuter(RobotExecuter executer, RobotMode type, boolean isUpdate) {
		Robot robot = executer.getRobot();
		Ball ball = world.getBall();
		int distanceToGoal = offset != null ? 500 : 500;	//TODO: Always returns 500

		// no need to check the role first, there is no "on role changed behavior"
		((Ally)robot).setRole(type);
		
		// Can I move?
		if (world.robotMayMove(robot.getRobotId())) {
			executer.stop(false);
		} else {
			executer.stop(true);
		}

		// !!TODO implement break and/or holding patterns for robots
		// TODO ^ done? robots only allowed to move when penaltyRobot != null
		// If it time for a penalty? getClosestAttackerRobotToBall() and send
		// him to the ball

		// ! TODO implement a free-pass for the robot doing the kickoff or
		// penalty

		Referee ref = world.getReferee();
		Command refCommand = ref.getCommand();

		switch (refCommand) {
				case PREPARE_KICKOFF_BLUE: break;
				case PREPARE_KICKOFF_YELLOW: break;
				case PREPARE_PENALTY_BLUE: break;
				
				case PREPARE_PENALTY_YELLOW: 
					if (refCommand.equals(("PREPARE_KICKOFF_" + world.getReferee().getAlly().getColor().toString()))
							&& penaltyRobot == null) {
						//TODO: This if-statement will never be called, since refCommand is tested on PREPARE_KICKOFF_x, while refCommand already PREPARE_PENALTY_YELLOW
						penaltyRobot = getClosestAllyRobotToBall(world);
					}

					if (robot != penaltyRobot) {
						return;
					}
					break;
					
				case NORMAL_START:
					penaltyRobot = null;
					break;
					
				default:
					break;
			}


			// A new referee command was issued
			if (ref.getCommandCounter() > lastCommandCounter) {
				// System.out.println("Command received: " + refCommand);
			}
		//}

		// TODO cleanup comments
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
			handleKeeper(robot, ball, executer, isUpdate, distanceToGoal);
			break;
		case KEEPERDEFENDER:
			handleDefender(robot, ball, executer, isUpdate, distanceToGoal);
			break;
		case ATTACKER:
			handleAttacker(robot, ball, executer, isUpdate);
			break;
		case COVERER: // fuckrobot
			handleBlocker(robot, ball, executer, isUpdate);
			break;
		default:
			
			break;
		}
	}

	@Override
	public void handleAttacker(Robot robot, Ball ball, RobotExecuter executer, boolean isUpdate) {
		Point freePosition = getClosestAllyRobotToBall(world) == robot ? null : getFreePosition(robot); //robot was null
		int chipKick = 0;
		int shootDirection = 0;
		boolean dribble = false;

		// penalty mode
		if (penaltyRobot != null && penaltyRobot == robot) { //TODO: Remove "penaltyRobot != null"
			// move to penalty area to get in range with the ball
			if (penaltyRobot.getPosition().getDeltaDistance(ball.getPosition()) > 100) {
				freePosition = ball.getPosition();
			} else {
				freePosition = null;
			}

			Robot keeper = world.getReferee().getEnemy().getRobotByID(world.getReferee().getEnemy().getGoalie());
			shootDirection = getPenaltyDirection(penaltyRobot, keeper, ball);

			// check if robot is able to shoot and if the angle towards the
			// ball is correct, shoot when possible
			if (robot.getPosition().getDeltaDistance(ball.getPosition()) < 100
					&& robot.getOrientation() + 10 > shootDirection && robot.getOrientation() - 10 < shootDirection)
				chipKick = -100;
		} else {
			if (freePosition == null) { // if robot has no free position then it
										// is closest.
				double dDistance = ball.getPosition().getDeltaDistance(robot.getPosition());
				if (dDistance < 150) {
					dribble = true;
				}
				
				if (robot.getRobotId() == 1)
					shootDirection = getShootingDirection(world.getReferee().getAlly().getRobotByID(3), ball);
				else
					shootDirection = getShootingDirection(world.getReferee().getAlly().getRobotByID(1), ball);
				
				if (dDistance < 150 && dribble && Math.abs(shootDirection - (int)robot.getOrientation()) <2){
					chipKick = -100;
				}
				// robot.getPosition()
				// calculate best tactic, shoot, chip or pass
				// if robot has place free to shoot

				// determine if the robot has the ball, then determine if
				// the robot has a good chance to chip or kick the ball to
				// the goal, else to an ally
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

	@Override
	public void handleBlocker(Robot robot, Ball ball, RobotExecuter executer, boolean isUpdate) {
		// Determine closest robot who does not yet have a blocker
		Robot opponent = getClosestEnemyToRobot(robot, true, executers);
		int distanceToOpponent = 250;

		if (isUpdate) {
			((Coverer) executer.getLowLevelBehavior()).update(distanceToOpponent, ball.getPosition(),
					robot.getPosition(), opponent.getPosition(), opponent.getRobotId());
		} else {
			executer.setLowLevelBehavior(new Coverer(robot, ComInterface.getInstance(RobotCom.class),
					distanceToOpponent, ball.getPosition(), robot.getPosition(), opponent.getPosition(), opponent
							.getRobotId()));
		}
	}

	@Override
	public void handleDefender(Robot robot, Ball ball, RobotExecuter executer, boolean isUpdate, int distanceToGoal) {
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

	@Override
	public void handleKeeper(Robot robot, Ball ball, RobotExecuter executer, boolean isUpdate, int distanceToGoal) {
		if (isUpdate) {
			((Keeper) executer.getLowLevelBehavior()).update(distanceToGoal, false, ball.getPosition(),
					robot.getPosition());
		} else {
			executer.setLowLevelBehavior(new Keeper(robot, ComInterface.getInstance(RobotCom.class), distanceToGoal,
					false, ball.getPosition(), robot.getPosition(), robot.getPosition().getX() > 0 ? MID_GOAL_POSITIVE
							: MID_GOAL_NEGATIVE, world.getField().getWidth() / 2));
		}
	}
}
