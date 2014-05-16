package controller.ai.highLevelBehavior.forcebehavior;

import java.util.ArrayList;

import controller.ai.lowLevelBehavior.LowLevelBehavior;
import controller.ai.lowLevelBehavior.RobotExecuter;

public abstract class Mode {

	private FieldForces fieldForces;

	private RobotExecuter[] robotExcecuter;

	public abstract void setFieldForce();

	public abstract ArrayList<LowLevelBehavior> generateLowLevelBehaviors();

}
