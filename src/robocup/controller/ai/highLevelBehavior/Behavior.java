/**
 * Abstract class behavior, when execute is called the behavior will execute the next step for all RobotExecuters
 */
package robocup.controller.ai.highLevelBehavior;

import java.util.ArrayList;

import robocup.controller.ai.lowLevelBehavior.RobotExecuter;

public abstract class Behavior {

	public abstract void execute(ArrayList<RobotExecuter> executers);

}
