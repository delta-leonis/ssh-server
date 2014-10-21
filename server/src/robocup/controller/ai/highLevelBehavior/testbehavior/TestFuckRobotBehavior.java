package robocup.controller.ai.highLevelBehavior.testbehavior;

import java.util.ArrayList;

import robocup.Main;
import robocup.controller.ai.highLevelBehavior.Behavior;
import robocup.controller.ai.lowLevelBehavior.FuckRobot;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.Ball;
import robocup.model.FieldObject;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.model.World;
import robocup.output.ComInterface;
import robocup.output.RobotCom;

public class TestFuckRobotBehavior extends Behavior {
	

	
	private Ball ball;
	private World world;
	private Robot defender;
	private Robot opponent;
	
	public TestFuckRobotBehavior() {
		world = World.getInstance();
	}
	
	@Override
	public void execute(ArrayList<RobotExecuter> executers) {
		defender = world.getAlly().getRobotByID(Main.TEST_ROBOT_ID);
		opponent = world.getEnemy().getRobotByID(Main.TEST_FUCK_ROBOT_ID);
		
		
		ball = world.getBall();
		int distanceToOpponent = 250;
		
		if(defender != null && ball != null) {
			RobotExecuter executer = findExecuter(Main.TEST_ROBOT_ID, executers);
			
			// Initialize executer for this robot
			if(executer == null) {
				executer = new RobotExecuter(defender);
				executer.setLowLevelBehavior(new FuckRobot(defender, ComInterface.getInstance(RobotCom.class), distanceToOpponent, 
						ball.getPosition(), defender.getPosition(), opponent.getPosition()));
				new Thread(executer).start();
				executers.add(executer);
			} else {
				((FuckRobot)executer.getLowLevelBehavior()).update(distanceToOpponent, ball.getPosition(), defender.getPosition(), opponent.getPosition());
			}
		}
	}
	
}