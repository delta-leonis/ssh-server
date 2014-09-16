package robocup.controller.ai;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import robocup.controller.ai.highLevelBehavior.Behavior;
import robocup.controller.ai.highLevelBehavior.testbehavior.TestKeepingBehavior;
import robocup.controller.ai.highLevelBehavior.testbehavior.TestPositionBehavior;
import robocup.controller.ai.highLevelBehavior.testbehavior.TestPositionWithBallBehavior;
import robocup.controller.ai.lowLevelBehavior.LowLevelBehavior;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.World;

public class Main implements Observer {

	private World world;
	private Behavior behavior;
	private ArrayList<RobotExecuter> robotExecuters;
	@SuppressWarnings("unused")
	private ArrayList<LowLevelBehavior> lowLevelBehaviors;
//	private ComInterface output;

	public Main() {
		world = World.getInstance();
		world.addObserver(this);
//		behavior = new Force();
		behavior = new TestPositionBehavior();
//		behavior = new DriveSquareBehavior();
//		behavior = new TestKeepingBehavior();
//		behavior = new TestPositionWithBallBehavior();
		robotExecuters = new ArrayList<RobotExecuter>();
		createExecuters();
		lowLevelBehaviors = new ArrayList<LowLevelBehavior>();
	}

//	private void determineBehavior() {
//		behavior.execute(robotExecuters);
//	}
//
//	private boolean isBallWithEnemy() {
//		return false;
//	}
//
//	private boolean checkIfExecuterExist(Robot r) {
//		for (RobotExecuter e : robotExecuters) {
//			if (e.getRobot().equals(r))
//				return true;
//		}
//		return false;
//	}

	private void createExecuters() {
		
		
		
//		Team t = world.getTeamByColor(world.getOwnTeamColor());
//		for (Robot r : t.getRobots()) {
//			if (!checkIfExecuterExist(r)) {
//				RobotExecuter e = new RobotExecuter(r);
//				new Thread(e).start();
//				robotExecuters.add(e);
//				if (r.getRobotID() == 0xB) {
//					Point point = new Point(0, 0);
//					LowLevelBehavior lowLevelBehavior = new GotoPosition(r, output, point);
//					e.setLowLevelBehavior(lowLevelBehavior);
//				}
//				if (r.getRobotID() == 0x3) {
//					Point point = new Point(0, 0);
//					LowLevelBehavior lowLevelBehavior = new GotoPosition(r, output, point);
//					e.setLowLevelBehavior(lowLevelBehavior);
//				}
//			}
//		}
	}

	public void update(Observable o, Object arg) {
		// CHECK 
		//IF ROBOT OP GOEDE POSITIE
			//GA NAAR NIEUWE POSITIE
		
		if ("detectionHandlerFinished".equals(arg)) {
			behavior.execute(robotExecuters);
		} else if ("RobotAdded".equals(arg)) {
			createExecuters();
		}
	}
}
