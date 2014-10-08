package robocup.controller.ai.highLevelBehavior.testbehavior;

import java.util.ArrayList;

import robocup.controller.ai.highLevelBehavior.Behavior;
import robocup.controller.ai.lowLevelBehavior.Attacker;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.Ball;
import robocup.model.Robot;
import robocup.model.World;
import robocup.output.ComInterface;
import robocup.output.RobotCom;

public class TestAttackerBehavior extends Behavior {

	private int robotId;
	private World world;
	private Robot attacker;
	private Ball ball;
	private int shootDirection;

	public TestAttackerBehavior(int robotId, int shootDirection) {
		world = World.getInstance();
		this.robotId = robotId;
		this.shootDirection = shootDirection;
	}

	@Override
	public void execute(ArrayList<RobotExecuter> executers) {
		attacker = world.getAlly().getRobotByID(robotId);
		ball = world.getBall();
		
		if(attacker != null && ball != null) {
			RobotExecuter executer = findExecuter(robotId, executers);
			
			// Initialize executer for this robot
			if(executer == null) {
				executer = new RobotExecuter(attacker);
				executer.setLowLevelBehavior(new Attacker(attacker, ComInterface.getInstance(RobotCom.class), attacker.getPosition(), 
						ball.getPosition(), 0, 0, shootDirection));
				new Thread(executer).start();
				executers.add(executer);
			} else {
				((Attacker)executer.getLowLevelBehavior()).update(attacker.getPosition(), ball.getPosition(), 0, 0, shootDirection);
			}
		}
	}
}
