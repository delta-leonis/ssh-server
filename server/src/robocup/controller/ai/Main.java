package robocup.controller.ai;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import robocup.controller.ai.highLevelBehavior.Behavior;
import robocup.controller.ai.highLevelBehavior.forcebehavior.ForceBehavior;
import robocup.controller.ai.lowLevelBehavior.LowLevelBehavior;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.Robot;
import robocup.model.Team;
import robocup.model.World;

public class Main implements Observer {

	private World world;
	@SuppressWarnings("unused")
	private ArrayList<Behavior> behaviors;
	private Behavior behavior;
	private ArrayList<RobotExecuter> robotExecuters = new ArrayList<RobotExecuter>();
	@SuppressWarnings("unused")
	private ArrayList<LowLevelBehavior> lowLevelBehaviors;

	public Main() {
		world = World.getInstance();
		world.addObserver(this);

		initExecutors();

		behavior = new ForceBehavior(robotExecuters);

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
		// check if behavior isnt null, else program crash
		if (behavior == null) {
			return;
		}
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
		for (Robot robot : team.getRobotsOnSight()) {
			boolean executerFound = false;

			if (robotExecuters != null) {
				for (RobotExecuter exec : robotExecuters) {
					if (exec.getRobot().getRobotId() == robot.getRobotId()) {
						updatedRobotExecuters.add(exec);
						executerFound = true;
					}
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
