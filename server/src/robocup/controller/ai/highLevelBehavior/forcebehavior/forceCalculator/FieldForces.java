/**
 * Use this class for all field forces
 */
package robocup.controller.ai.highLevelBehavior.forcebehavior.forceCalculator;

import java.util.ArrayList;

import robocup.controller.ai.highLevelBehavior.forcebehavior.forces.Force;

public class FieldForces {

	private ArrayList<Force> forces;

	public FieldForces() {
		forces = new ArrayList<Force>();
	}

	public void addForce(Force force) {
		forces.add(force);
	}

	// public RobotForce getForcesOnRobot(int robotId)

	// public Robot getRobotWithHighestForce()
}
