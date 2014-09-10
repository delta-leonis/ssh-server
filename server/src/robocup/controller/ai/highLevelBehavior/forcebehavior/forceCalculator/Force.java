package robocup.controller.ai.highLevelBehavior.forcebehavior.forceCalculator;

import java.util.ArrayList;

import robocup.model.World;
import robocup.controller.ai.highLevelBehavior.Behavior;
import robocup.controller.ai.highLevelBehavior.forcebehavior.Mode;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Force extends Behavior {
	
	@SuppressWarnings("unused")
	private ArrayList<Mode> modes;

	@SuppressWarnings("unused")
	private Mode[] forceBehaviors;

	@SuppressWarnings("unused")
	private Mode determineMode(World w) {
		//calculate the most effective mode to play in, being either attack or defensive playstyles 
		
		//why attack and defense
		throw new NotImplementedException();
	}

	@Override
	public void execute(ArrayList<RobotExecuter> executers) {
		// We zitten bv in defence, alle executers moeten defence dingen doen.
		throw new NotImplementedException();
	}
}