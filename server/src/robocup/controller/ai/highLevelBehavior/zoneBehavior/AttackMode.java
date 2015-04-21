package robocup.controller.ai.highLevelBehavior.zoneBehavior;

import java.util.ArrayList;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.controller.ai.lowLevelBehavior.Attacker;
import robocup.controller.ai.lowLevelBehavior.Coverer;
import robocup.controller.ai.lowLevelBehavior.Keeper;
import robocup.controller.ai.lowLevelBehavior.KeeperDefender;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;

public class AttackMode extends Mode {

	public AttackMode(Strategy strategy, ArrayList<RobotExecuter> executers) {
		super(strategy, executers);
	}

	@Override
	public void updateAttacker(RobotExecuter executer) {
		Attacker attacker = (Attacker) executer.getLowLevelBehavior();
		// TODO Update with normal values
		attacker.update(0.0, 0, ball.getPosition());
	}

	@Override
	public void updateCoverer(RobotExecuter executer) {
		Coverer blocker = (Coverer) executer.getLowLevelBehavior();
		// TODO Update with normal values
		blocker.update(250, ball.getPosition(), null, 0);
	}

	@Override
	public void updateKeeperDefender(RobotExecuter executer) {
		KeeperDefender keeperDefender = (KeeperDefender) executer.getLowLevelBehavior();
		// TODO Update with normal values
		keeperDefender.update(1200, false, ball.getPosition(), executer.getRobot().getPosition());
	}

	@Override
	public void updateKeeper(RobotExecuter executer) {
		Keeper keeper = (Keeper) executer.getLowLevelBehavior();

		int distanceToGoal = 700;
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
