/**
 * This class will be used to calculate the field forces when the AI is in Attack modus.
 */
package robocup.controller.ai.highLevelBehavior.forcebehavior.forceCalculator;

import robocup.controller.ai.highLevelBehavior.forcebehavior.forces.BallForce;
import robocup.controller.ai.highLevelBehavior.forcebehavior.forces.FieldEdgeForce;
import robocup.controller.ai.highLevelBehavior.forcebehavior.forces.GoalForce;
import robocup.controller.ai.highLevelBehavior.forcebehavior.forces.RobotForce;
import robocup.model.Robot;

public class AttackCalculator extends ForceCalculator {

	/**
	 * Create a ForceCalculator which can be used while in AttackMode
	 */
	public AttackCalculator() {
		super();
	}

	/**
	 * Create the different stub forces:
	 * 	BallForce,
	 * 	GoalForce,
	 * 	FieldEdgeForce
	 */
	protected void calculateStubForces() {
		fieldForces.addForce(new FieldEdgeForce(-50, 100));
		fieldForces.addForce(new BallForce(world.getBall(), 200, 300));
		fieldForces.addForce(new GoalForce(-100, 500));
	}

	/**
	 * Create the Forces around Robots
	 */
	protected void calculateRobotForces() {
		for (Robot r : world.getReferee().getAlly().getRobotsOnSight()) {
			fieldForces.addForce(new RobotForce(r, -50, 250));
		}

		for (Robot r : world.getReferee().getEnemy().getRobotsOnSight()) {
			fieldForces.addForce(new RobotForce(r, -50, 250));
		}
	}
}
