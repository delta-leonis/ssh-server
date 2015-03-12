package robocup.controller.ai.highLevelBehavior.zoneBehavior;

import java.util.ArrayList;

import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.enums.RobotMode;

public abstract class Mode {

	public void execute(ArrayList<RobotExecuter> executers) {
		try {
			for (RobotExecuter executer : executers) {
				updateExecuter(executer, determineRole());
			}
		} catch (Exception e) {
			System.out.println("Exception in Mode, please fix me :(");
			e.printStackTrace();
		}
	}

	private RobotMode determineRole() {
		// TODO determine role for a robot
		return RobotMode.ATTACKER;
	}

	public void updateExecuter(RobotExecuter executer, RobotMode role) {
		switch (role) {
		case ATTACKER:
			handleAttacker(executer);
			break;
		case BLOCKER:
			handleBlocker(executer);
			break;
		case DEFENDER:
			handleDefender(executer);
			break;
		case KEEPER:
			handleKeeper(executer);
			break;
		default:
			System.out.println("Unknown role in Mode, role: " + role);
		}
	}

	public abstract void handleAttacker(RobotExecuter executer);

	public abstract void handleBlocker(RobotExecuter executer);

	public abstract void handleDefender(RobotExecuter executer);

	public abstract void handleKeeper(RobotExecuter executer);
}
