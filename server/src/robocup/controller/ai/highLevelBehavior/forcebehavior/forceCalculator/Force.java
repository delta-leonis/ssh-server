package robocup.controller.ai.highLevelBehavior.forcebehavior.forceCalculator;

import java.util.ArrayList;

import robocup.Main;
import robocup.model.World;
import robocup.output.ComInterface;
import robocup.output.RobotCom;
import robocup.controller.ai.highLevelBehavior.Behavior;
import robocup.controller.ai.highLevelBehavior.forcebehavior.Mode;
import robocup.controller.ai.lowLevelBehavior.FuckRobot;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Force extends Behavior {
	
	private World world;
	
	@SuppressWarnings("unused")
	private ArrayList<Mode> modes;

	@SuppressWarnings("unused")
	private Mode[] forceBehaviors;
	
	public Force() {
		world = World.getInstance();
	}
	
	@SuppressWarnings("unused")
	private Mode determineMode(World w) {
		return null;
		// TODO high level determine who has the ball, if we got it go for attack, otherwise defense
		
		
		//calculate the most effective mode to play in, being either attack or defensive playstyles 
		
		//why attack and defense
	}

	@Override
	public void execute(ArrayList<RobotExecuter> executers) {
		// TODO high level determine mode
		// TODO high level let mode generate or update low level behaviors
	}
}