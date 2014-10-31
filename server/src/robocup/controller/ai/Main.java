package robocup.controller.ai;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import robocup.controller.ai.highLevelBehavior.Behavior;
import robocup.controller.ai.highLevelBehavior.testbehavior.TestAttackerBehavior;
import robocup.controller.ai.highLevelBehavior.testbehavior.TestKeepingBehavior;
import robocup.controller.ai.highLevelBehavior.testbehavior.TestKeepingOutsideGoalBehavior;
import robocup.controller.ai.highLevelBehavior.testbehavior.TestPositionBehavior;
import robocup.controller.ai.highLevelBehavior.testbehavior.TestPositionWithBallBehavior;
import robocup.controller.ai.lowLevelBehavior.Keeper;
import robocup.controller.ai.lowLevelBehavior.KeeperDefender;
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
		
		/*// 'Robs mode test init'
		 * createExecuters();
		 * behavior = new Force(robotExecuters);
		 */
		
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
//		createExecuters();
		lowLevelBehaviors = new ArrayList<LowLevelBehavior>();
		
	}


	private void createExecuters() {
		// TODO high level create executers
		
		// Clear current executers
		robotExecuters = new ArrayList<RobotExecuter>();
		
		// Create executer for each robot
		Team team = world.getAlly();
		for(Robot robot : team.getRobots()) {
			RobotExecuter executer = new RobotExecuter(robot);
			new Thread(executer).start();
			robotExecuters.add(executer);
		}
		
	}

	public void update(Observable o, Object arg) {
//		removeMissingRobots(world.getAlly());
//		removeMissingRobots(world.getEnemy());

		
		if ("detectionHandlerFinished".equals(arg)) {
			for(Behavior b : behaviors)
				b.execute(robotExecuters);
		} else if ("RobotAdded".equals(arg)) {
//			createExecuters();
		}
	}
}
