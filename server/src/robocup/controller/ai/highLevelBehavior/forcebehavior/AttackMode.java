/**
 * Use this class to control the low level behaviors in attack mode
 */
package robocup.controller.ai.highLevelBehavior.forcebehavior;

import java.util.ArrayList;

import robocup.controller.ai.lowLevelBehavior.LowLevelBehavior;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class AttackMode extends Mode {

	@Override
	public void setFieldForce() {
		throw new NotImplementedException();
	}

	@Override
	public ArrayList<LowLevelBehavior> generateLowLevelBehaviors() {
		throw new NotImplementedException();
	}
}