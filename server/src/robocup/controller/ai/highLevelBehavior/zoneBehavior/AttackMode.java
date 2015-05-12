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
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

public class AttackMode extends Mode {

	public AttackMode(Strategy strategy, ArrayList<RobotExecuter> executers) {
		super(strategy, executers);
	}

	@Override
	public void updateAttacker(RobotExecuter executer) {
		Attacker attacker = (Attacker) executer.getLowLevelBehavior();
		int chipKick = 40;
		FieldPoint ballPosition = ball.getPosition();
		FieldPoint freeShot = world.hasFreeShot();

		if (freeShot != null) {
			double shootDirection = ballPosition.getAngle(freeShot);
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

		switch (strategy.getClass().getSimpleName()) {
		case "CornerToCornerAttack":
			// TODO assign positions
			return new FieldPoint(0, 0);
		case "FreeShotRoundPlay":
			FieldPoint topRunner = new FieldPoint(robot.getPosition().getX(), robot.getPosition().getY()
					+ Robot.DIAMETER / 2);
			FieldPoint bottomRunner = new FieldPoint(robot.getPosition().getX(), robot.getPosition().getY()
					- Robot.DIAMETER / 2);
			FieldPoint topBaller = new FieldPoint(world.getClosestRobotToBall().getPosition().getX(), world
					.getClosestRobotToBall().getPosition().getY()
					+ Robot.DIAMETER / 2);
			FieldPoint bottomBaller = new FieldPoint(world.getClosestRobotToBall().getPosition().getX(), world
					.getClosestRobotToBall().getPosition().getY()
					- Robot.DIAMETER / 2);
			if (world.getAllRobotsInArea(topRunner, topBaller, bottomBaller, bottomRunner).size() > 0) {
				return robot.getPosition();
			} else {
				if (robot.getPreferredZone().getCenterPoint().getX() < robot.getPosition().getX()) {
					return new FieldPoint(robot.getPosition().getX() + Robot.DIAMETER, robot.getPosition().getY());
				} else {
					return new FieldPoint(robot.getPosition().getX() - Robot.DIAMETER, robot.getPosition().getY());
				}

			}
		case "PenaltyAreaKickIn":
			// TODO assign positions
			return new FieldPoint(0, 0);
		case "SecondPostKickIn":
			// TODO assign positions
			return new FieldPoint(0, 0);
		default:
			LOGGER.severe("Unknown strategy used in AttackMode. Strategy: " + strategy.getClass().getSimpleName());

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

		FieldPoint offset = null;

		if (robot.getPosition() != null) {
			switch (keeperDefenders.size()) {
			case 2:
				if (robot.getPosition().getY() == Math.max(keeperDefenders.get(0).getPosition().getY(), keeperDefenders
						.get(1).getPosition().getY()))
					offset = new FieldPoint(0, 150);
				else
					offset = new FieldPoint(0, -150);
				break;
			case 3:
				if (robot.getPosition().getY() == Math.max(
						keeperDefenders.get(0).getPosition().getY(),
						Math.max(keeperDefenders.get(1).getPosition().getY(), keeperDefenders.get(2).getPosition()
								.getY())))
					offset = new FieldPoint(0, 150);
				else if (robot.getPosition().getY() == Math.min(
						keeperDefenders.get(0).getPosition().getY(),
						Math.min(keeperDefenders.get(1).getPosition().getY(), keeperDefenders.get(2).getPosition()
								.getY())))
					offset = new FieldPoint(0, -150);
				else
					offset = new FieldPoint(0, 0);
				break;
			default:
				break;
			}
		}

		keeperDefender.update(distanceToGoal, goToKick, ballPosition, offset);
	}

	@Override
	public void updateKeeper(RobotExecuter executer) {
		Keeper keeper = (Keeper) executer.getLowLevelBehavior();

		int distanceToGoal = (int) world.getField().getEastGoal().getWidth() / 2;
		boolean goToKick = world.getClosestRobotToBall().equals(executer.getRobot());
		FieldPoint ballPosition = ball.getPosition();
		keeper.update(distanceToGoal, goToKick, ballPosition);
	}

	@Override
	protected void updatePenaltyKeeper(RobotExecuter executer) {
		PenaltyKeeper penalKeeper = (PenaltyKeeper) executer.getLowLevelBehavior();
		FieldPoint ballPosition = ball.getPosition();
		Robot enemy = world.getClosestRobotToBall();
		penalKeeper.update(ballPosition, enemy);
	}

	@Override
	protected void updateGoalPostCoverer(RobotExecuter executer) {
		GoalPostCoverer goalPostCoverer = (GoalPostCoverer) executer.getLowLevelBehavior();

		int distanceToPole = world.getField().getDefenceRadius() + world.getField().getDefenceStretch() / 2 + 50;
		boolean goToKick = false;

		double XPoint = world.getReferee().getEastTeam().equals(world.getReferee().getAlly()) ? world.getField()
				.getLength() / 2 : -world.getField().getLength() / 2;
		double YPoint = ball.getPosition().getY() / Math.abs(ball.getPosition().getY())
				* world.getField().getEastGoal().getWidth() / 4 * -1;
		Robot enemyRobot = world.getClosestEnemyRobotToPoint(new FieldPoint(XPoint, YPoint));
		FieldPoint ballPosition = ball.getPosition();

		goalPostCoverer.update(distanceToPole, goToKick, enemyRobot == null ? ((Ally) executer.getRobot())
				.getPreferredZone().getCenterPoint() : enemyRobot.getPosition(), ballPosition);
	}

	@Override
	protected void updateDisturber(RobotExecuter executer) {
		Disturber disturber = (Disturber) executer.getLowLevelBehavior();

		int distanceToObject = 300;
		FieldPoint objectPosition = ball.getPosition();
		boolean goToKick = world.getClosestRobotToBall().getPosition().getDeltaDistance(objectPosition) > Robot.DIAMETER;

		disturber.update(distanceToObject, goToKick, objectPosition);
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
