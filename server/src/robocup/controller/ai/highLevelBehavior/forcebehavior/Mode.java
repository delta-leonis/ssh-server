/**
 * abstract class for all modes like attack or defence
 */
package robocup.controller.ai.highLevelBehavior.forcebehavior;

import java.util.ArrayList;

import robocup.controller.ai.lowLevelBehavior.LowLevelBehavior;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;

public abstract class Mode {

	@SuppressWarnings("unused")
	private FieldForces fieldForces;

	@SuppressWarnings("unused")
	private RobotExecuter[] robotExcecuter;

	public abstract void setFieldForce();

	public abstract ArrayList<LowLevelBehavior> generateLowLevelBehaviors();
}