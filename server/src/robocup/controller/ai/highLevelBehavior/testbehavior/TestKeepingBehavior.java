package robocup.controller.ai.highLevelBehavior.testbehavior;

import java.util.ArrayList;

import robocup.Main;
import robocup.controller.ai.highLevelBehavior.Behavior;
import robocup.controller.ai.lowLevelBehavior.Keeping;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.Robot;
import robocup.model.World;
import robocup.output.ComInterface;
import robocup.output.RobotCom;

public class TestKeepingBehavior extends Behavior {

	@Override
	public void execute(ArrayList<RobotExecuter> executers) {
		World world = World.getInstance();
		Robot keeper = world.getAlly().getRobotByID(Main.KEEPER_ROBOT_ID);
		
		if(keeper != null) {
			RobotExecuter executer = findExecuter(Main.KEEPER_ROBOT_ID, executers);
			
			if(executer == null) {
				executer = new RobotExecuter(keeper);
				executer.setLowLevelBehavior(new Keeping(keeper, ComInterface.getInstance(RobotCom.class)));
				new Thread(executer).start();
				executers.add(executer);
			}
		}
	}
}
