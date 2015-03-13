package robocup.controller.ai.highLevelBehavior.zoneBehavior;

import java.util.ArrayList;

import robocup.controller.ai.lowLevelBehavior.Attacker;
import robocup.controller.ai.lowLevelBehavior.Coverer;
import robocup.controller.ai.lowLevelBehavior.Keeper;
import robocup.controller.ai.lowLevelBehavior.KeeperDefender;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.enums.RobotMode;

public class DefenseMode extends Mode {

	public DefenseMode(ArrayList<RobotExecuter> executers) {
		super(executers);
	}

	@Override
	public RobotMode determineRole(RobotExecuter executer) {
		if (executer.getRobot().getRobotId() == world.getReferee().getAlly().getGoalie())
			return RobotMode.KEEPER;

		// TODO Determine role based on positions and zones on the field
		return RobotMode.KEEPERDEFENDER;
	}

	@Override
	public void updateAttacker(RobotExecuter executer) {
		Attacker attacker = (Attacker) executer.getLowLevelBehavior();
		// TODO Update with normal values
		attacker.update(null, null, 0, false, 0);
	}

	@Override
	public void updateCoverer(RobotExecuter executer) {
		Coverer blocker = (Coverer) executer.getLowLevelBehavior();
		// TODO Update with normal values
		blocker.update(250, null, null, null, 0);
	}

	@Override
	public void updateKeeperDefender(RobotExecuter executer) {
		KeeperDefender keeperDefender = (KeeperDefender) executer.getLowLevelBehavior();
		// TODO Update with normal values
		keeperDefender.update(0, false, null, null);
	}

	@Override
	public void updateKeeper(RobotExecuter executer) {
		Keeper keeper = (Keeper) executer.getLowLevelBehavior();
		// TODO check if keeper needs to move to the ball, if so, set goToKick to true
		boolean goToKick = false;

		keeper.update(500, goToKick, ball.getPosition(), executer.getRobot().getPosition());
	}
}
