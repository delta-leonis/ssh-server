/**
 * Use this class for all field forces
 */
package robocup.controller.ai.highLevelBehavior.forcebehavior;

public class FieldForces {

	private RobotForces robotForces;
	private StubForces stubForces;

	public void addRobotForces(RobotForces rForces) {
		robotForces = rForces;
	}

	public void addStubForces(StubForces sForces) {
		stubForces = sForces;
	}

	/**
	 * @return the robotForces
	 */
	public RobotForces getRobotForces() {
		return robotForces;
	}

	/**
	 * @return the stubForces
	 */
	public StubForces getStubForces() {
		return stubForces;
	}
}
