/**
 * This class will be used to calculate the field forces when the AI is in Attack modus.
 */
package robocup.controller.ai.highLevelBehavior.forcebehavior.forceCalculator;

import robocup.controller.ai.highLevelBehavior.forcebehavior.forces.BallForce;
import robocup.controller.ai.highLevelBehavior.forcebehavior.forces.FieldEdgeForce;
import robocup.controller.ai.highLevelBehavior.forcebehavior.forces.GoalForce;
import robocup.controller.ai.highLevelBehavior.forcebehavior.forces.RobotForce;
import robocup.model.Robot;

public class DefenceCalculator extends ForceCalculator {

	public DefenceCalculator() {
		super();
	}

	protected void calculateStubForces() {
		fieldForces.addForce(new FieldEdgeForce(-100, 100));
		fieldForces.addForce(new BallForce(world.getBall(), 75, 300));
		fieldForces.addForce(new GoalForce(100, 500));
	}

	protected void calculateRobotForces() {
		for (Robot r : world.getAlly().getRobots()) {
			fieldForces.addForce(new RobotForce(r, -50, 250));
		}

		for (Robot r : world.getEnemy().getRobots()) {
			fieldForces.addForce(new RobotForce(r, -50, 250));
		}
	}
}
