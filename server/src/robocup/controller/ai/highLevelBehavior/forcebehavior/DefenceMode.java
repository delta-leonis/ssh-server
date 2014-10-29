/**
 * Use this class to control the low level behaviors in defence mode
 */
package robocup.controller.ai.highLevelBehavior.forcebehavior;

import java.util.ArrayList;

import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class DefenceMode extends Mode {

	@Override
	public void setFieldForce() {
		throw new NotImplementedException();
	}

	@Override
	public void execute(ArrayList<RobotExecuter> executers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateLowLevelBehavior(RobotExecuter executer, String type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void generateLowLevelBehavior(RobotExecuter executer, String type) {
		// TODO Auto-generated method stub
		
	}
}