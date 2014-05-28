package controller.ai.highLevelBehavior;

import java.util.ArrayList;

import controller.ai.lowLevelBehavior.RobotExecuter;

public abstract class Behavior {

	private RobotExecuter robotExcecuter;

	public abstract void execute(ArrayList<RobotExecuter> executers);

}
