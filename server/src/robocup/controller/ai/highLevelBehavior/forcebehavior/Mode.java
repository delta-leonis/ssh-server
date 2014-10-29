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
	
	public enum roles { KEEPER, DEFENDER, ATTACKER, BLOCKER };

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
	 * Generate or Update a low level behavior for this executer
	 * @param executer execute the executer
	 * @param type type of the low level behavior
	 * @param isUpdate false if a new behavior should be created, true if update is requred
	 */
	public abstract void updateExecuter(RobotExecuter executer, roles type, boolean isUpdate);
}