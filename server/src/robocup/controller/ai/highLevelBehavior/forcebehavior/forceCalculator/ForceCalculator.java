package robocup.controller.ai.highLevelBehavior.forcebehavior.forceCalculator;

import robocup.controller.ai.highLevelBehavior.forcebehavior.FieldForces;
import robocup.controller.ai.highLevelBehavior.forcebehavior.RobotForces;
import robocup.controller.ai.highLevelBehavior.forcebehavior.StubForces;

public abstract class ForceCalculator {

	protected FieldForces fieldForces;
	protected RobotForces rForces;
	protected StubForces sForces;
	
	protected ForceCalculator() {
		fieldForces = new FieldForces();
		rForces = new RobotForces();
		sForces = new StubForces();
	}
	
	/**
	 * Calculate robot and stub forces and throw them all together into fieldForces
	 * @return all fieldforces
	 */
	protected FieldForces calculate() {
		fieldForces.addRobotForces(rForces);
		fieldForces.addStubForces(sForces);

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
