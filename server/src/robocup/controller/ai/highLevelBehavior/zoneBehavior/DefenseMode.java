package robocup.controller.ai.highLevelBehavior.zoneBehavior;

import java.util.ArrayList;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.controller.ai.lowLevelBehavior.Attacker;
import robocup.controller.ai.lowLevelBehavior.Coverer;
import robocup.controller.ai.lowLevelBehavior.Keeper;
import robocup.controller.ai.lowLevelBehavior.KeeperDefender;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.FieldPoint;

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
		distanceToGoal
		goToKick
		ballPosition
		offset
		keeperDefender.update(1200/*distanceToGoal*/, false/*goToKick*/, ball.getPosition()/*ballPosition*/, executer.getRobot().getPosition()/*offset*/);
	}

	@Override
	public void updateKeeper(RobotExecuter executer) {
		Keeper keeper = (Keeper) executer.getLowLevelBehavior();

		int distanceToGoal = 500;
		// TODO check if keeper needs to move to the ball, if so, set goToKick to true
		boolean goToKick = false;

		keeper.update(distanceToGoal, goToKick, ball.getPosition());
	}

	@Override
	protected void updatePenaltyKeeper(RobotExecuter executer) {
		// TODO Auto-generated method stub
		
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
