package robocup.controller.ai.highLevelBehavior.zoneBehavior;

import java.util.ArrayList;

import robocup.controller.ai.lowLevelBehavior.Attacker;
import robocup.controller.ai.lowLevelBehavior.Coverer;
import robocup.controller.ai.lowLevelBehavior.Keeper;
import robocup.controller.ai.lowLevelBehavior.KeeperDefender;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.Ally;
import robocup.model.enums.RobotMode;

public class AttackMode extends Mode {

	public AttackMode() {
		super();
	}

	@Override
	public void setRoles(ArrayList<RobotExecuter> executers) {
		for (RobotExecuter executer : executers) {
			if (executer.getRobot().getRobotId() == world.getReferee().getAlly().getGoalie())
				((Ally) executer.getRobot()).setRole(RobotMode.KEEPER);

			// TODO Determine role based on positions and zones on the field
		}
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

		int distanceToGoal = 700;
		// TODO check if keeper needs to move to the ball, if so, set goToKick to true
		boolean goToKick = false;

		keeper.update(distanceToGoal, goToKick, ball.getPosition(), executer.getRobot().getPosition());
	}
}
