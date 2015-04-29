package robocup.controller.ai;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import robocup.controller.ai.highLevelBehavior.Behavior;
import robocup.controller.ai.highLevelBehavior.ZoneBehavior;
import robocup.controller.ai.lowLevelBehavior.LowLevelBehavior;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.World;
import robocup.test.testBehaviors.TestBehaviour;

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
		robotExecuters = world.getRobotExecuters();

//		behavior = new ZoneBehavior(robotExecuters);
		behavior = new TestBehaviour(robotExecuters);
		
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
			if(World.getInstance().getStart()){
				behavior.execute(robotExecuters);
			}
			else{
				for(RobotExecuter robot : robotExecuters){
					robot.setLowLevelBehavior(null);
				}
			}
		}
	}

}
