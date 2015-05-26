package robocup.controller.ai;

import java.util.Observable;
import java.util.Observer;

import robocup.controller.ai.highLevelBehavior.Behavior;
import robocup.controller.ai.highLevelBehavior.ZoneBehavior;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.World;

public class Main implements Observer {

	private World world;
	private Behavior behavior;
	
	public Main() {
		world = World.getInstance();
		world.addObserver(this);

		behavior = new ZoneBehavior(world.getRobotExecuters());
//		behavior = new TestBehaviour();

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
				behavior.execute(world.getRobotExecuters());
			}
			else{
				for(RobotExecuter robot : world.getRobotExecuters()){
					robot.setLowLevelBehavior(null);
				}
			}
		}
	}
}

