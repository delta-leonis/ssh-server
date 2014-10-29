/**
 * abstract class for all modes like attack or defence
 */
package robocup.controller.ai.highLevelBehavior.forcebehavior;

import java.util.ArrayList;

import robocup.controller.ai.lowLevelBehavior.RobotExecuter;

public abstract class Mode {

	@SuppressWarnings("unused")
	private FieldForces fieldForces;

	@SuppressWarnings("unused")
	private RobotExecuter[] robotExcecuter;

	/**
	 * Let the calculator recalculate all forces
	 */
	public abstract void setFieldForce();

	/**
	 * Let force calculator determine forces
	 * then update or generate low level behaviors
	 * @param executers
	 */
	public abstract void execute(ArrayList<RobotExecuter> executers);
	
	/**
	 * Update the values of the low level behavior from the executer
	 * @param executer the executer
	 * @param type type of the low level behavior
	 */
	public abstract void updateLowLevelBehavior(RobotExecuter executer, String type);
	
	/**
	 * Generate a new low level behavior for this executer
	 * @param executer the executer
	 * @param type type of the low level behavior
	 */
	public abstract void generateLowLevelBehavior(RobotExecuter executer, String type);
}