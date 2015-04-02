package robocup.controller.ai.lowLevelBehavior;

import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.output.ComInterface;


public class PenaltyKeeper extends Keeper {

	public PenaltyKeeper(Robot robot, ComInterface output, int distanceToGoal, boolean goToKick,
			FieldPoint ballPosition, FieldPoint keeperPosition, FieldPoint centerGoalPosition, int yMax) {
		super(robot, output, distanceToGoal, goToKick, ballPosition, keeperPosition, centerGoalPosition, yMax);
		// TODO Auto-generated constructor stub
	}
}
