package robocup.controller.ai;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import robocup.controller.ai.highLevelBehavior.Behavior;
import robocup.controller.ai.highLevelBehavior.ZoneBehavior;
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
	private static Logger LOGGER = Logger.getLogger(Main.class.getName());


	public Main() {
		world = World.getInstance();
		world.addObserver(this);
		initExecutors();

		behavior = new ZoneBehavior(robotExecuters);

		// behaviors = new ArrayList<Behavior>();
		// behaviors.add(new TestKeepingBehavior(1, null));
		// behaviors.add(new TestKeepingBehavior(1, new Point(0, 0)));
		// behaviors.add(new TestKeepingBehavior(1, new Point(0, 100)));
		// behaviors.add(new TestAttackerBehavior(1, -100));
		// behaviors.add(new TestAttackerBehavior(3, 0));
		// robotExecuters = new ArrayList<RobotExecuter>();
		// initExecutors();
		// lowLevelBehaviors = new ArrayList<LowLevelBehavior>();

	}

	public void update(Observable o, Object arg) {
		// check if behavior isnt null, else program crash
		if (behavior == null) {
			return;
		}
		if ("detectionHandlerFinished".equals(arg)) {
			if(World.getInstance().getStart())
				behavior.execute(robotExecuters);
		}
	}

	/**
	 * Initializes the {@RobotExecuter executers} for the {@link Robot robots}, 
	 * whether they're on sight or not.
	 */
	private void initExecutors() {

		ArrayList<RobotExecuter> updatedRobotExecuters = new ArrayList<RobotExecuter>();

		Team team = world.getReferee().getAlly();

		for (Robot robot : team.getRobots()) {
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
