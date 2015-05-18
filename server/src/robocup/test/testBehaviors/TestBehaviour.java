package robocup.test.testBehaviors;

import java.util.ArrayList;

import robocup.controller.ai.highLevelBehavior.Behavior;
import robocup.controller.ai.highLevelBehavior.strategy.defense.BarricadeDefending;
import robocup.controller.ai.highLevelBehavior.zoneBehavior.Mode;
import robocup.controller.ai.lowLevelBehavior.Attacker;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.FieldPoint;
import robocup.model.World;

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
	public TestBehaviour(ArrayList<RobotExecuter> executers) {
		world = World.getInstance();
		
		// WARNING: TestMode overwrites execute. This means that the given
		// strategy doesn't matter at all.
		currentMode = new TestMode(new BarricadeDefending(), executers);
		
		RobotExecuter exec = world.getRobotExecuters().get(0);
		exec.setLowLevelBehavior(new Attacker(exec.getRobot()));
		exec.getRobot().setVisible(true);
		exec.getRobot().setPosition(new FieldPoint(0,0));
		exec.getLowLevelBehavior().calculate();
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
