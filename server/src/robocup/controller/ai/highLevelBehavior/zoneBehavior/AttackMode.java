package robocup.controller.ai.highLevelBehavior.zoneBehavior;

import java.util.ArrayList;

import robocup.controller.ai.lowLevelBehavior.Attacker;
import robocup.controller.ai.lowLevelBehavior.Keeper;
import robocup.controller.ai.lowLevelBehavior.KeeperDefender;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.controller.ai.lowLevelBehavior.Stoorder;
import robocup.model.enums.RobotMode;

public class AttackMode extends Mode {

	public AttackMode(ArrayList<RobotExecuter> executers) {
		super(executers);
	}

	@Override
	public RobotMode determineRole(RobotExecuter executer) {
		// TODO Determine role based on positions and zones on the field
		return RobotMode.ATTACKER;
	}

	@Override
	public void updateAttacker(RobotExecuter executer) {
		Attacker attacker = (Attacker) executer.getLowLevelBehavior();
		// TODO Update with normal values
		attacker.update(null, null, 0, false, 0);
	}

	@Override
	public void updateBlocker(RobotExecuter executer) {
		Stoorder blocker = (Stoorder) executer.getLowLevelBehavior();
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
		// TODO Update with normal values
		keeper.update(0, false, null, null);
	}
}
