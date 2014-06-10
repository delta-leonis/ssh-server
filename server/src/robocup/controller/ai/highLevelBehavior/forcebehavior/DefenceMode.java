/**
 * Use this class to control the low level behaviors in defence mode
 */
package robocup.controller.ai.highLevelBehavior.forcebehavior;

import java.util.ArrayList;

import robocup.controller.ai.lowLevelBehavior.LowLevelBehavior;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class DefenceMode extends Mode {

	@Override
	public void setFieldForce() {
		throw new NotImplementedException();
	}

	@Override
	public ArrayList<LowLevelBehavior> generateLowLevelBehaviors() {
		throw new NotImplementedException();
	}
}