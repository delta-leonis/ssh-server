package robocup.controller.ai.highLevelBehavior.zoneBehavior;

import java.util.ArrayList;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.controller.ai.lowLevelBehavior.Attacker;
import robocup.controller.ai.lowLevelBehavior.Counter;
import robocup.controller.ai.lowLevelBehavior.Coverer;
import robocup.controller.ai.lowLevelBehavior.Disturber;
import robocup.controller.ai.lowLevelBehavior.GoalPostCoverer;
import robocup.controller.ai.lowLevelBehavior.Keeper;
import robocup.controller.ai.lowLevelBehavior.KeeperDefender;
import robocup.controller.ai.lowLevelBehavior.PenaltyKeeper;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.controller.ai.lowLevelBehavior.Runner;
import robocup.model.Ally;
import robocup.model.Enemy;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.enums.Command;
import robocup.model.enums.FieldZone;
import robocup.model.enums.GameState;
import robocup.model.enums.RobotMode;

public class StandardMode extends Mode {

	private double penaltyDirection;

	public StandardMode(Strategy strategy, ArrayList<RobotExecuter> executers) {
		super(strategy, executers);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void updateAttacker(RobotExecuter executer) {
		Attacker attacker = (Attacker) executer.getLowLevelBehavior();
		int chipKick = 0;
		FieldPoint ballPosition = ball.getPosition();

		if (world.getReferee().getCommand() == Command.PREPARE_PENALTY_YELLOW
				|| world.getReferee().getCommand() == Command.PREPARE_PENALTY_BLUE) {
			FieldPoint freeShot = world.hasFreeShot();
			double shootDirection = freeShot != null ? ballPosition.getAngle(world.hasFreeShot()) : world.getReferee()
					.isEastTeamColor(world.getReferee().getAllyTeamColor()) ? 180.0 : 0.0;
			penaltyDirection = shootDirection;
			attacker.update(shootDirection, chipKick, ballPosition);
		} else if (world.getGameState() == GameState.TAKING_KICKOFF
				&& (world.getReferee().getPreviousCommand() == Command.PREPARE_PENALTY_YELLOW || world.getReferee()
						.getPreviousCommand() == Command.PREPARE_PENALTY_BLUE)) {
			chipKick = -100;
			attacker.update(penaltyDirection, chipKick, ballPosition);
		} else if (world.getGameState() == GameState.TAKING_KICKOFF
				&& (world.getReferee().getPreviousCommand() == Command.PREPARE_KICKOFF_BLUE || world.getReferee()
						.getPreviousCommand() == Command.PREPARE_KICKOFF_YELLOW)) {
			ArrayList<Ally> runners = new ArrayList<Ally>();

			for (RobotExecuter itExecuter : executers) {
				Ally robot = (Ally) itExecuter.getRobot();

				if (robot.getRole() == RobotMode.RUNNER)
					runners.add(robot);

				if (runners.size() > 0 && runners.get(0).getPosition() != null) {
					double shootDirection = ballPosition.getAngle(runners.get(0).getPosition());
					chipKick = -40;
					attacker.update(shootDirection, chipKick, ballPosition);
				}
			}
		} else if (world.getReferee().getCommand() == Command.PREPARE_KICKOFF_BLUE
				|| world.getReferee().getCommand() == Command.PREPARE_KICKOFF_YELLOW) {
			ArrayList<Ally> runners = new ArrayList<Ally>();

			for (RobotExecuter itExecuter : executers) {
				Ally robot = (Ally) itExecuter.getRobot();

				if (robot.getRole() == RobotMode.RUNNER)
					runners.add(robot);
			}

			if (runners.size() > 0 && runners.get(0).getPosition() != null) {
				double shootDirection = ballPosition.getAngle(runners.get(0).getPosition());
				chipKick = 0;
				attacker.update(shootDirection, chipKick, ballPosition);
			}
		} else if (world.getReferee().getCommand() == Command.DIRECT_FREE_BLUE
				|| world.getReferee().getCommand() == Command.DIRECT_FREE_YELLOW) {
			FieldPoint freeShot = world.hasFreeShot();

			if (freeShot != null) {
				double shootDirection = ballPosition.getAngle(freeShot);
				chipKick = -100;
				attacker.update(shootDirection, chipKick, ballPosition);
			} else {
				ArrayList<Ally> runners = new ArrayList<Ally>();

				for (RobotExecuter itExecuter : executers) {
					Ally robot = (Ally) itExecuter.getRobot();

					if (robot.getRole() == RobotMode.RUNNER)
						runners.add(robot);
				}

				if (runners.size() > 0 && runners.get(0).getPosition() != null) {
					double shootDirection = ballPosition.getAngle(runners.get(0).getPosition());
					chipKick = -60;
					attacker.update(shootDirection, chipKick, ballPosition);
				}
			}
		} else if (world.getReferee().getCommand() == Command.INDIRECT_FREE_BLUE
				|| world.getReferee().getCommand() == Command.INDIRECT_FREE_YELLOW) {
			ArrayList<Ally> runners = new ArrayList<Ally>();

			for (RobotExecuter itExecuter : executers) {
				Ally robot = (Ally) itExecuter.getRobot();

				if (robot.getRole() == RobotMode.RUNNER)
					runners.add(robot);
			}

			if (runners.size() > 0 && runners.get(0).getPosition() != null) {
				double shootDirection = ballPosition.getAngle(runners.get(0).getPosition());
				chipKick = -60;
				attacker.update(shootDirection, chipKick, ballPosition);
			}
		}
	}

	@Override
	public void updateRunner(RobotExecuter executer) {
		Runner runner = (Runner) executer.getLowLevelBehavior();

		Ally robot = (Ally) executer.getRobot();

		FieldPoint ballPosition = ball.getPosition();
		FieldPoint freePosition = robot.getPreferredZone() != null ? robot.getPreferredZone().getCenterPoint()
				: findFreePosition(robot);

		runner.update(ballPosition, freePosition);
	}

	private FieldPoint findFreePosition(Ally robot) {
		ArrayList<Ally> runners = new ArrayList<Ally>();

		for (RobotExecuter executer : executers) {
			Ally ally = (Ally) executer.getRobot();

			if (ally.getRole() == RobotMode.RUNNER)
				runners.add(ally);
		}

		boolean isEastTeam = world.getReferee().getEastTeam().equals(world.getReferee().getAlly());

		switch (strategy.getClass().getSimpleName()) {
		case "DirectFreeKickDefense":
			return null;
		case "DirectFreeKickAttack":
			// TODO handle situation where a keeper takes the free kick
			if (runners.size() == 2) {
				double maxY = Math.max(runners.get(0).getPosition().getY(), runners.get(1).getPosition().getY());

				if (ball.getPosition().getY() > FieldZone.FieldPointPaletteZones.h.getY()) {
					// ball in north zone
					if (isEastTeam) {
						if (robot.getPosition().getY() == maxY)
							return FieldZone.WEST_MIDDLE.getCenterPoint();
						else
							return FieldZone.WEST_SOUTH_SECONDPOST.getCenterPoint();
					} else {
						if (robot.getPosition().getY() == maxY)
							return FieldZone.EAST_MIDDLE.getCenterPoint();
						else
							return FieldZone.EAST_SOUTH_SECONDPOST.getCenterPoint();
					}
				} else if (ball.getPosition().getY() < -FieldZone.FieldPointPaletteZones.h.getY()) {
					// ball in south zone
					if (isEastTeam) {
						if (robot.getPosition().getY() == maxY)
							return FieldZone.WEST_NORTH_SECONDPOST.getCenterPoint();
						else
							return FieldZone.WEST_MIDDLE.getCenterPoint();
					} else {
						if (robot.getPosition().getY() == maxY)
							return FieldZone.EAST_NORTH_SECONDPOST.getCenterPoint();
						else
							return FieldZone.EAST_MIDDLE.getCenterPoint();
					}
				} else {
					// ball in mid zone
					if (isEastTeam) {
						if (robot.getPosition().getY() == maxY)
							return FieldZone.WEST_NORTH_SECONDPOST.getCenterPoint();
						else
							return FieldZone.WEST_SOUTH_SECONDPOST.getCenterPoint();
					} else {
						if (robot.getPosition().getY() == maxY)
							return FieldZone.EAST_NORTH_SECONDPOST.getCenterPoint();
						else
							return FieldZone.EAST_SOUTH_SECONDPOST.getCenterPoint();
					}
				}
			} else
				return new FieldPoint(0, 0);
		case "IndirectFreeKickDefense":
		case "IndirectFreeKickAttack":
		case "KickOffDefense":
		case "KickOffAttack":
			return null;
		case "PenaltyDefense":
			double maxY = Math.max(runners.get(0).getPosition().getY(), runners.get(1).getPosition().getY());

			if (isEastTeam) {
				if (robot.getPosition().getY() == maxY) {
					FieldPoint point = FieldZone.WEST_NORTH_SECONDPOST.getCenterPoint();

					point.setX(1500);

					return point;
				} else {
					FieldPoint point = FieldZone.WEST_SOUTH_SECONDPOST.getCenterPoint();

					point.setX(1500);

					return point;
				}
			} else {
				if (robot.getPosition().getY() == maxY) {
					FieldPoint point = FieldZone.EAST_NORTH_SECONDPOST.getCenterPoint();

					point.setX(-1500);

					return point;
				} else {
					FieldPoint point = FieldZone.EAST_SOUTH_SECONDPOST.getCenterPoint();

					point.setX(-1500);

					return point;
				}
			}
		case "PenaltyAttack":
			maxY = Math.max(runners.get(0).getPosition().getY(), runners.get(1).getPosition().getY());

			if (isEastTeam) {
				if (robot.getPosition().getY() == maxY) {
					FieldPoint point = FieldZone.WEST_NORTH_SECONDPOST.getCenterPoint();

					point.setX(-1500);

					return point;
				} else {
					FieldPoint point = FieldZone.WEST_SOUTH_SECONDPOST.getCenterPoint();

					point.setX(-1500);

					return point;
				}
			} else {
				if (robot.getPosition().getY() == maxY) {
					FieldPoint point = FieldZone.EAST_NORTH_SECONDPOST.getCenterPoint();

					point.setX(1500);

					return point;
				} else {
					FieldPoint point = FieldZone.EAST_SOUTH_SECONDPOST.getCenterPoint();

					point.setX(1500);

					return point;
				}
			}
		case "Stop":
			return null;
		case "TimeOut":
			if (isEastTeam)
				return new FieldPoint(robot.getRobotId() * (Robot.DIAMETER + 50), -1800);
			else
				return new FieldPoint(-(robot.getRobotId() + 1) * (Robot.DIAMETER - 50), -1800);
		default:
			LOGGER.severe("Unknown strategy used in StandardMode. Strategy: " + strategy.getClass().getSimpleName());
			return new FieldPoint(0, 0);
		}
	}

	@Override
	public void updateCoverer(RobotExecuter executer) {
		Coverer blocker = (Coverer) executer.getLowLevelBehavior();
		Ally allyRobot = (Ally) executer.getRobot();

		int distanceToSubject = 250;
		FieldPoint objectPosition = ball.getPosition();
		FieldPoint subjectPosition = null;
		int subjectId = 0;

		ArrayList<Enemy> enemyRobots = world.getEnemyRobotsInZone(allyRobot.getPreferredZone());

		if (enemyRobots.size() > 0) {
			Enemy enemyRobot = enemyRobots.get(0);
			subjectPosition = enemyRobot == null ? null : enemyRobot.getPosition();
			subjectId = enemyRobot == null ? 0 : enemyRobot.getRobotId();
		} else {
			if (allyRobot.getPreferredZone() != null)
				subjectPosition = allyRobot.getPreferredZone().getCenterPoint();
		}

		blocker.update(distanceToSubject, objectPosition, subjectPosition, subjectId);
	}

	@Override
	public void updateKeeperDefender(RobotExecuter executer) {
		KeeperDefender keeperDefender = (KeeperDefender) executer.getLowLevelBehavior();

		int distanceToGoal = world.getField().getDefenceRadius() + world.getField().getDefenceStretch() / 2 + 50;
		boolean goToKick = false;
		FieldPoint ballPosition = ball.getPosition();

		Ally robot = (Ally) executer.getRobot();
		ArrayList<Ally> keeperDefenders = new ArrayList<Ally>();

		for (RobotExecuter itExecuter : executers)
			if (itExecuter.getLowLevelBehavior() instanceof KeeperDefender)
				if (itExecuter.getRobot().getPosition() != null)
					keeperDefenders.add((Ally) itExecuter.getRobot());

		int offset = 0;

		if (robot.getPosition() != null) {
			switch (keeperDefenders.size()) {
			case 2:
				if (robot.getPosition().getY() == Math.max(keeperDefenders.get(0).getPosition().getY(), keeperDefenders
						.get(1).getPosition().getY()))
					offset = -15;
				else
					offset = 15;
				break;
			case 3:
				if (robot.getPosition().getY() == Math.max(
						keeperDefenders.get(0).getPosition().getY(),
						Math.max(keeperDefenders.get(1).getPosition().getY(), keeperDefenders.get(2).getPosition()
								.getY())))
					offset = -15;
				else if (robot.getPosition().getY() == Math.min(
						keeperDefenders.get(0).getPosition().getY(),
						Math.min(keeperDefenders.get(1).getPosition().getY(), keeperDefenders.get(2).getPosition()
								.getY())))
					offset = 15;
				else
					offset = 0;
				break;
			default:
				offset = executer.getRobot().getRobotId() * 15 - 60;
				break;
			}

			// Invert offset when on the left side of the field.
			// This is done because the offset moves the other way on this side. 
			if (robot.getPosition().getX() < 0)
				offset = -offset;

			keeperDefender.update(distanceToGoal, goToKick, ballPosition, offset, world.getField().getWidth(), world
					.getField().getLength());
		}
	}

	@Override
	public void updateKeeper(RobotExecuter executer) {
		Keeper keeper = (Keeper) executer.getLowLevelBehavior();

		int distanceToGoal = (int) world.getField().getEastGoal().getWidth() / 2;

		FieldPoint ballPosition = ball.getPosition();

		boolean goToKick = world.getReferee().getWestTeam() == world.getReferee().getAlly() ? FieldZone.WEST_NORTH_GOAL
				.contains(ballPosition) || FieldZone.WEST_SOUTH_GOAL.contains(ballPosition) : FieldZone.EAST_NORTH_GOAL
				.contains(ballPosition) || FieldZone.EAST_SOUTH_GOAL.contains(ballPosition);

		keeper.update(distanceToGoal, goToKick, ballPosition, world.getField().getWidth(), world.getField().getLength());
	}

	@Override
	protected void updatePenaltyKeeper(RobotExecuter executer) {
		PenaltyKeeper penalKeeper = (PenaltyKeeper) executer.getLowLevelBehavior();
		FieldPoint ballPosition = ball.getPosition();
		Robot enemy = world.getClosestRobotToBall();
		penalKeeper.update(ballPosition, enemy, ballPosition);
	}

	@Override
	protected void updateGoalPostCoverer(RobotExecuter executer) {
		GoalPostCoverer goalPostCoverer = (GoalPostCoverer) executer.getLowLevelBehavior();

		int distanceToPole = world.getField().getDefenceRadius() + world.getField().getDefenceStretch() / 2 + 200;
		boolean goToKick = false;

		double XPoint = world.getReferee().getEastTeam().equals(world.getReferee().getAlly()) ? world.getField()
				.getLength() / 2 : -world.getField().getLength() / 2;
		double YPoint = ball.getPosition().getY() / Math.abs(ball.getPosition().getY())
				* world.getField().getEastGoal().getWidth() / 4 * -1;
		Robot enemyRobot = world.getClosestEnemyRobotToPoint(new FieldPoint(XPoint, YPoint));
		FieldPoint ballPosition = ball.getPosition();

		goalPostCoverer.update(
				new FieldPoint(XPoint, YPoint),
				distanceToPole,
				goToKick,
				enemyRobot == null ? ((Ally) executer.getRobot()).getPreferredZone().getCenterPoint() : enemyRobot
						.getPosition(), ballPosition, world.getField().getWidth(), world.getField().getLength());
	}

	@Override
	protected void updateDisturber(RobotExecuter executer) {
		Disturber disturber = (Disturber) executer.getLowLevelBehavior();

		Ally robot = (Ally) executer.getRobot();
		ArrayList<Ally> disturbers = new ArrayList<Ally>();

		for (RobotExecuter itExecuter : executers)
			if (itExecuter.getLowLevelBehavior() instanceof Disturber)
				if (itExecuter.getRobot().getPosition() != null)
					disturbers.add((Ally) itExecuter.getRobot());

		int offset = 0;

		if (disturbers.size() == 2) {
			if (robot.getPosition().getY() == Math.max(disturbers.get(0).getPosition().getY(), disturbers.get(1)
					.getPosition().getY()))
				offset = -10;
			else
				offset = 10;
		}

		int distanceToObject = 700; // keep longer distance from ball in standardmode, as we are probably in GameState.STOPPED
		FieldPoint objectPosition = ball.getPosition();
		boolean goToKick = false;//world.getClosestRobotToBall().getPosition().getDeltaDistance(objectPosition) > Robot.DIAMETER;

		// Invert offset when on the left side of the field.
		disturber.update(distanceToObject, goToKick, objectPosition, world.getField().getWidth(), world.getField()
				.getLength());
		// This is done because the offset moves the other way on this side. 
		if (robot.getPosition().getX() < 0)
			offset = -offset;

		disturber.update(distanceToObject, goToKick, objectPosition, offset, world.getField().getWidth(), world
				.getField().getLength());
	}

	@Override
	protected void updateCounter(RobotExecuter executer) {
		Counter counter = (Counter) executer.getLowLevelBehavior();

		FieldZone zone = world.getReferee().getAlly().equals(world.getReferee().getEastTeam()) ? FieldZone.WEST_MIDDLE
				: FieldZone.EAST_MIDDLE;
		FieldPoint ballPosition = ball.getPosition();
		FieldPoint freePosition = null;

		counter.update(zone, ballPosition, freePosition);
	}
}
