package robocup.controller.ai.highLevelBehavior;

import java.util.ArrayList;

import robocup.controller.ai.highLevelBehavior.zoneBehavior.AttackMode;
import robocup.controller.ai.highLevelBehavior.zoneBehavior.DefenseMode;
import robocup.controller.ai.highLevelBehavior.zoneBehavior.Mode;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.World;

public class ZoneBehavior extends Behavior {

	private World world;
	private DefenseMode defenseMode;
	private AttackMode attackMode;
	private Mode currentMode;

	public ZoneBehavior(ArrayList<RobotExecuter> executers) {
		world = World.getInstance();

		defenseMode = new DefenseMode(executers);
		attackMode = new AttackMode(executers);
	}

	@Override
	public void execute(ArrayList<RobotExecuter> executers) {
		currentMode = determineMode();
		currentMode.execute(executers);
	}

	private Mode determineMode() {
		// TODO return attackmode when attacking, defensemode when defending
		return defenseMode;
	}

	@Override
	public void updateExecuters(ArrayList<RobotExecuter> executers) {
		// Use execute instead, this is deprecated
	}

}
