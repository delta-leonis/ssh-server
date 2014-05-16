package controller.ai.highLevelBehavior.forcebehavior.forceCalculator;

import java.util.ArrayList;

import model.World;
import controller.ai.highLevelBehavior.Behavior;
import controller.ai.highLevelBehavior.forcebehavior.Mode;
import controller.ai.lowLevelBehavior.RobotExecuter;

public class Force extends Behavior {
	
	private ArrayList<Mode> modes;

	private Mode[] forceBehaviors;

	private Mode determineMode(World w) {
		return null;
	}

	@Override
	public void execute(ArrayList<RobotExecuter> executers) {
		// TODO Auto-generated method stub
		
	}

}
