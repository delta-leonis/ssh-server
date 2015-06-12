package robocup.controller.ai;

import robocup.controller.ai.highLevelBehavior.Behavior;
import robocup.controller.ai.highLevelBehavior.ZoneBehavior;
import robocup.model.World;

public class AiExecuter extends Thread {

	private World world;
	static public Behavior behavior;
	
	public AiExecuter() {
		world = World.getInstance();
		behavior = new ZoneBehavior(world.getRobotExecuters());
//		behavior = new TestBehaviour();
	}

	public void run() {
		while (true) {
			if (behavior != null && world.getStart()) {
				behavior.execute(world.getRobotExecuters());
			}
			
			try {
				Thread.sleep(25);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

