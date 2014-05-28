package controller.ai;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import output.ComInterface;
import output.RobotCom;
import model.FieldObject;
import model.Point;
import model.Robot;
import model.Team;
import model.World;
import model.enums.Command;
import controller.ai.highLevelBehavior.Behavior;
import controller.ai.highLevelBehavior.forcebehavior.forceCalculator.Force;
import controller.ai.highLevelBehavior.testbehavior.TestBehavior;
import controller.ai.lowLevelBehavior.GotoPosition;
import controller.ai.lowLevelBehavior.LowLevelBehavior;
import controller.ai.lowLevelBehavior.RobotExecuter;

public class Main implements Observer {

	private World world;
	private Behavior behavior;
	private ArrayList<RobotExecuter> robotExecuters;
	private ArrayList<LowLevelBehavior> lowLevelBehaviors;
//	private ComInterface output;

	public Main() {
		world = World.getInstance();
		world.addObserver(this);
//		behavior = new Force();
		behavior = new TestBehavior();
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
		
		if (arg.equals(new String("detectionHandlerFinished"))) {
			behavior.execute(robotExecuters);
			// System.out.println("World updated");
		} else if (arg.equals("RobotAdded")) {
			createExecuters();
		}
	}
}
