package robocup.controller.ai.highLevelBehavior.forcebehavior.forceCalculator;

import robocup.model.World;


public abstract class ForceCalculator {

	protected FieldForces fieldForces;
	protected World world;
	
	protected ForceCalculator() {
		fieldForces = new FieldForces();
		world = World.getInstance();
	}
	
	/**
	 * Calculate robot and stub forces and throw them all together into fieldForces
	 * @return all fieldforces
	 */
	protected FieldForces calculate() {
		calculateStubForces();
		calculateRobotForces();

		return fieldForces;
	}

	/**
	 * Calculate forces from everything else
	 */
	protected abstract void calculateStubForces();

	/**
	 * Calculate forces on every robot
	 */
	protected abstract void calculateRobotForces();
}
