/**
 * Use this class for all field forces
 */
package robocup.controller.ai.highLevelBehavior.forcebehavior.forceCalculator;

import java.util.ArrayList;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import robocup.controller.ai.highLevelBehavior.forcebehavior.forces.Force;
import robocup.controller.ai.highLevelBehavior.forcebehavior.forces.RobotForce;
import robocup.model.Robot;

public class FieldForces {

	private ArrayList<Force> forces;

	/**
	 * Create FieldForces, a class which contains all forces on the field
	 */
	public FieldForces() {
		forces = new ArrayList<Force>();
	}

	/**
	 * Add a force to the list of all forces
	 * @param force the force to add
	 */
	public void addForce(Force force) {
		forces.add(force);
	}

	/**
	 * Get the sum of all forces working on a robot
	 * @param robot the robot
	 * @return the sum of all forces working on the robot
	 */
	public Vector2D getTotalForceOnRobot(Robot robot) {
		Vector2D vector = new Vector2D(0, 0);

		for (Force force : forces) {
			if (force.affectsPoint(robot.getPosition())
					&& !(force instanceof RobotForce && ((RobotForce) force).getRobot().getRobotID() == robot
							.getRobotID())) {
				int direction = force.getDirection(robot.getPosition());
				int power = force.getPower();
				int x = (int) (Math.sin(Math.toRadians(direction)) * power);
				int y = (int) (Math.cos(Math.toRadians(direction)) * power);
				vector.add(new Vector2D(x, y));
			}
		}

		return vector;
	}
}
