package robocup.controller.ai;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import robocup.controller.ai.highLevelBehavior.Behavior;
import robocup.controller.ai.highLevelBehavior.forcebehavior.forceCalculator.Force;
import robocup.controller.ai.highLevelBehavior.testbehavior.TestAttackerBehavior;
import robocup.controller.ai.highLevelBehavior.testbehavior.TestKeepingBehavior;
import robocup.controller.ai.highLevelBehavior.testbehavior.TestKeepingOutsideGoalBehavior;
import robocup.controller.ai.highLevelBehavior.testbehavior.TestPositionBehavior;
import robocup.controller.ai.highLevelBehavior.testbehavior.TestPositionWithBallBehavior;
import robocup.controller.ai.lowLevelBehavior.Keeper;
import robocup.controller.ai.lowLevelBehavior.KeeperDefender;
import robocup.controller.ai.lowLevelBehavior.LowLevelBehavior;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.filter.Kalman;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.model.Team;
import robocup.model.World;

public class Main implements Observer {

	private World world;
	private ArrayList<Behavior> behaviors;
	private Behavior behavior;
	private ArrayList<RobotExecuter> robotExecuters;
	@SuppressWarnings("unused")
	private ArrayList<LowLevelBehavior> lowLevelBehaviors;


	public Main() {
		world = World.getInstance();
		world.addObserver(this);


		initExecutors();
		behavior = new Force(robotExecuters);


		// behaviors = new ArrayList<Behavior>();
		// behaviors.add(new TestKeepingBehavior(1, null));
		// behaviors.add(new TestKeepingBehavior(1, new Point(0, 0)));
		// behaviors.add(new TestKeepingBehavior(1, new Point(0, 100)));
		// behaviors.add(new TestAttackerBehavior(1, -100));
		// behaviors.add(new TestAttackerBehavior(3, 0));
		// robotExecuters = new ArrayList<RobotExecuter>();
		// createExecuters();
		// lowLevelBehaviors = new ArrayList<LowLevelBehavior>();

	}

	public void update(Observable o, Object arg) {
		if ("detectionHandlerFinished".equals(arg)) {
			behavior.execute(robotExecuters);
		} else if ("RobotAdded".equals(arg)) {
			initExecutors();
			behavior.updateExecuters(robotExecuters);
		}
	}

	private void initExecutors() {

		ArrayList<RobotExecuter> updatedRobotExecuters = new ArrayList<RobotExecuter>();

		Team team = world.getAlly();
		for (Robot robot : team.getRobots()) {
			boolean executerFound = false;
			for (RobotExecuter exec : robotExecuters) {
				if (exec.getRobot().getRobotID() == robot.getRobotID()) {
					updatedRobotExecuters.add(exec);
					executerFound = true;
				}
			}
			
			if (!executerFound) {
				RobotExecuter executer = new RobotExecuter(robot);
				new Thread(executer).start();
				updatedRobotExecuters.add(executer);
			}
		}
		robotExecuters = updatedRobotExecuters;
	}
}
