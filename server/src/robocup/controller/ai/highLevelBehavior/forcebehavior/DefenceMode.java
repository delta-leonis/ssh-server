/**
 * Use this class to control the low level behaviors in defence mode
 */
package robocup.controller.ai.highLevelBehavior.forcebehavior;

import java.util.ArrayList;

import robocup.model.enums.RobotMode;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.Ball;
import robocup.model.Robot;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class DefenceMode extends Mode {

	public DefenceMode(ArrayList<RobotExecuter> executers) {

	}

	@Override
	public void setFieldForce() {
		throw new NotImplementedException();
	}

	@Override
	public void execute(ArrayList<RobotExecuter> executers) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateExecuter(RobotExecuter executer, RobotMode type, boolean isUpdate) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateExecuters(ArrayList<RobotExecuter> executers) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleAttacker(Robot robot, Ball ball, RobotExecuter executer, boolean isUpdate) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleBlocker(Robot robot, Ball ball, RobotExecuter executer, boolean isUpdate) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDefender(Robot robot, Ball ball, RobotExecuter executer, boolean isUpdate, int distanceToGoal) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleKeeper(Robot robot, Ball ball, RobotExecuter executer, boolean isUpdate, int distanceToGoal) {
		// TODO Auto-generated method stub

	}
}
