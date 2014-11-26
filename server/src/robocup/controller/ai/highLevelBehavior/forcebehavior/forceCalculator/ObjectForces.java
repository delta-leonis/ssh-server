/**
 * all forces are ObjectForces
 */
package robocup.controller.ai.highLevelBehavior.forcebehavior.forceCalculator;

import java.util.ArrayList;

import robocup.model.Vector;

public abstract class ObjectForces {

	@SuppressWarnings("unused")
	private ArrayList<Vector> vectoren;
	public abstract void calculate();
}
