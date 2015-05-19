package robocup.test.testBehaviors;

import java.util.ArrayList;
import robocup.controller.ai.highLevelBehavior.Behavior;
import robocup.controller.ai.highLevelBehavior.zoneBehavior.Mode;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;


public class TestBehaviour extends Behavior {
	private World world;
	private Mode currentMode; // AttackMode or DefenseMode

	/**
	 * Create a ZoneBehavior.
	 * DefenseMode and AttackMode will be created as well.
	 * 	Modes are currently contained within ArrayLists for testing purposes.
	 * @see {@link #chooseAttackStrategy(ArrayList)}
	 * @see {@link #chooseDefenseStrategy(ArrayList)}
	 * @param executers the list containing all RobotExecuters
	 */
	public TestBehaviour() {		
		// WARNING: TestMode overwrites execute. This means that the given
		// strategy doesn't matter at all.
		currentMode = new TestMode(new TestStrategy());

	}

	/**
	 * Execute the ZoneBehavior.
	 * Mode will be determined and executed based on ball and team positioning.
	 */
	@Override
	public void execute(ArrayList<RobotExecuter> executers) {
		currentMode.execute();
	}
}
