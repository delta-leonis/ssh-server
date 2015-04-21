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
import robocup.model.Ally;
import robocup.model.Enemy;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.enums.FieldZone;

public class DefenseMode extends Mode {

	public DefenseMode(Strategy strategy, ArrayList<RobotExecuter> executers) {
		super(strategy, executers);
	}

	@Override
	public void updateAttacker(RobotExecuter executer) {
		// Unused in DefenseMode

		Attacker attacker = (Attacker) executer.getLowLevelBehavior();
		// TODO Update with normal values
		double shootDirection = 0.0;
		int chipKick = 0;
		FieldPoint ballPosition = ball.getPosition();
		attacker.update(shootDirection, chipKick, ballPosition);
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
				keeperDefenders.add((Ally) itExecuter.getRobot());

		FieldPoint offset = null;

		switch (keeperDefenders.size()) {
		case 2:
			if (robot.getPosition().getY() == Math.max(keeperDefenders.get(0).getPosition().getY(), keeperDefenders
					.get(1).getPosition().getY()))
				offset = new FieldPoint(0, 150);
			else
				offset = new FieldPoint(0, -150);
			break;
		case 3:
			if (robot.getPosition().getY() == Math.max(keeperDefenders.get(0).getPosition().getY(),
					Math.max(keeperDefenders.get(1).getPosition().getY(), keeperDefenders.get(2).getPosition().getY())))
				offset = new FieldPoint(0, 150);
			else if (robot.getPosition().getY() == Math.min(keeperDefenders.get(0).getPosition().getY(),
					Math.min(keeperDefenders.get(1).getPosition().getY(), keeperDefenders.get(2).getPosition().getY())))
				offset = new FieldPoint(0, -150);
			else
				offset = new FieldPoint(0, 0);
			break;
		default:
			break;
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

		int distanceToGoal = 1200;
		boolean goToKick = false;
		FieldPoint ballPosition = ball.getPosition();

		goalPostCoverer.update(distanceToGoal, goToKick, ballPosition);
	}

	@Override
	protected void updateDisturber(RobotExecuter executer) {
		Disturber disturber = (Disturber) executer.getLowLevelBehavior();

		int distanceToObject = 300;
		FieldPoint objectPosition = ball.getPosition();
		boolean goToKick = world.getClosestRobotToBall().getPosition().getDeltaDistance(objectPosition) > Robot.DIAMETER + 50;

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
