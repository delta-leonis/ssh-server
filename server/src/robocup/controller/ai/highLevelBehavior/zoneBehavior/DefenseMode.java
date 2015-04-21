package robocup.controller.ai.highLevelBehavior.zoneBehavior;

import java.util.ArrayList;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.controller.ai.lowLevelBehavior.Attacker;
import robocup.controller.ai.lowLevelBehavior.Coverer;
import robocup.controller.ai.lowLevelBehavior.Keeper;
import robocup.controller.ai.lowLevelBehavior.KeeperDefender;
import robocup.controller.ai.lowLevelBehavior.PenaltyKeeper;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.FieldPoint;
import robocup.model.Robot;

public class DefenseMode extends Mode {

	public DefenseMode(Strategy strategy, ArrayList<RobotExecuter> executers) {
		super(strategy, executers);
	}

	@Override
	public void updateAttacker(RobotExecuter executer) {
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
		// TODO Update with normal values
		int distanceToSubject = 250;
		FieldPoint objectPosition = ball.getPosition();
		FieldPoint subjectPosition = null;
		int subjectId = 0;
		blocker.update(distanceToSubject, objectPosition, subjectPosition, subjectId);
	}

	@Override
	public void updateKeeperDefender(RobotExecuter executer) {
		KeeperDefender keeperDefender = (KeeperDefender) executer.getLowLevelBehavior();
		// TODO Update with normal values
		int distanceToGoal = 1200;
		boolean goToKick = false;
		FieldPoint ballPosition = ball.getPosition();
		FieldPoint offset = null;
		keeperDefender.update(distanceToGoal, goToKick, ballPosition, offset);
	}

	@Override
	public void updateKeeper(RobotExecuter executer) {
		Keeper keeper = (Keeper) executer.getLowLevelBehavior();

		int distanceToGoal = 500;
		// TODO check if keeper needs to move to the ball, if so, set goToKick to true
		boolean goToKick = false;
		FieldPoint ballPosition = ball.getPosition();
		keeper.update(distanceToGoal, goToKick, ballPosition);
	}

	@Override
	protected void updatePenaltyKeeper(RobotExecuter executer) {
		PenaltyKeeper penalKeeper = (PenaltyKeeper) executer.getLowLevelBehavior();
		// TODO Auto-generated method stub
		FieldPoint ballPosition = null;
		Robot enemy = null;
		penalKeeper.update(ballPosition, enemy);
	}

	@Override
	protected void updateGoalPostCoverer(RobotExecuter executer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void updateDisturber(RobotExecuter executer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void updateCounter(RobotExecuter executer) {
		// TODO Auto-generated method stub
		
	}
}
