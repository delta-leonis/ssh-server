package robocup.controller.ai;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

import robocup.controller.ai.highLevelBehavior.Behavior;
import robocup.controller.ai.highLevelBehavior.testbehavior.TestAttackerBehavior;
import robocup.controller.ai.highLevelBehavior.testbehavior.TestKeepingBehavior;
import robocup.controller.ai.highLevelBehavior.testbehavior.TestKeepingOutsideGoalBehavior;
import robocup.controller.ai.highLevelBehavior.testbehavior.TestPositionBehavior;
import robocup.controller.ai.highLevelBehavior.testbehavior.TestPositionWithBallBehavior;
import robocup.controller.ai.lowLevelBehavior.LowLevelBehavior;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.model.Team;
import robocup.model.World;

public class Main implements Observer {

	private World world;
	private ArrayList<Behavior> behaviors;
	private ArrayList<RobotExecuter> robotExecuters;
	@SuppressWarnings("unused")
	private ArrayList<LowLevelBehavior> lowLevelBehaviors;
//	private ComInterface output;

	public Main() {
		// TODO high level initialize executers
		// TODO high level create new Force
		
		world = World.getInstance();
		world.addObserver(this);
		behaviors = new ArrayList<Behavior>();
//		behavior = new Force();
//		behavior = new TestPositionBehavior();
//		behavior = new DriveSquareBehavior();
//		behaviors.add(new TestKeepingBehavior(1, null));
//		behaviors.add(new TestKeepingBehavior(1, new Point(0, 0)));
//		behaviors.add(new TestKeepingBehavior(1, new Point(0, 100)));
//		behaviors.add(new TestPositionBehavior());
		behaviors.add(new TestAttackerBehavior(1, -100));
		//behaviors.add(new TestAttackerBehavior(3, 0));
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
		// TODO high level create executers
		
//		Team team = world.getAlly();
			/* 	Zet teamrollen
			   	1 keeper
			   	2 defenders
			   	3 attackers
			   
			   	Kan dynamisch gewisseld worden
		   		
		   		
		   		
		   */
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

	/**
	 * Remove all inactive robots from the team
	 * @param team 
	 */
	private void removeMissingRobots(Team team) {
		for(Robot r : team.getRobots())
			if(r.getLastUpdateTime() + 0.20 < Calendar.getInstance().getTimeInMillis() / 1000) {
				team.removeRobot(r.getRobotID()); // TODO make thread-safe or find other solution
				System.out.println("Robot with id: " + r.getRobotID() + " removed from team.");
			}
	}

	public void update(Observable o, Object arg) {
//		removeMissingRobots(world.getAlly());
//		removeMissingRobots(world.getEnemy());

		
		if ("detectionHandlerFinished".equals(arg)) {
			for(Behavior b : behaviors)
				b.execute(robotExecuters);
		} else if ("RobotAdded".equals(arg)) {
			createExecuters();
		}
	}
}
