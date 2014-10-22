package robocup.controller.ai.highLevelBehavior.testbehavior;

import java.util.ArrayList;

import robocup.controller.ai.highLevelBehavior.Behavior;
import robocup.controller.ai.lowLevelBehavior.Attacker;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.Ball;
import robocup.model.Point;
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

	/**
	 * Create a TestAttackerBehavior
	 * @param robotId id of the attacker
	 * @param shootDirection direction where the attacker will shoot
	 */
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
			Point freePosition = getClosestRobotToBall() == attacker ? null : getFreePosition(null);
			
			// Initialize executer for this robot
			if(executer == null) {
				executer = new RobotExecuter(attacker);
				executer.setLowLevelBehavior(new Attacker(attacker, ComInterface.getInstance(RobotCom.class), freePosition, 
						ball.getPosition(), 0, 0, shootDirection));
				new Thread(executer).start();
				executers.add(executer);
				System.out.println("executer created");
			} else {
				((Attacker)executer.getLowLevelBehavior()).update(freePosition, ball.getPosition(), 0, 0, shootDirection);
			}
		}
	}
	
	/**
	 * Find a free position for the robot
	 * A position is free when the robot can get the ball passed
	 * @param robot the robot who needs a free position
	 * @return a free position
	 */
	private Point getFreePosition(Robot robot) {
		return new Point(0, 0);
	}

	/**
	 * Get the closest robot to the ball on our team
	 * @return closest robot
	 */
	private Robot getClosestRobotToBall() {
		ArrayList<Robot> robots = world.getAlly().getRobots();
		
		int minDistance = -1;
		Robot closestRobot = null;
		
		for(Robot r : robots) {
			if(minDistance == -1) {
				closestRobot = r;
				minDistance = (int) r.getPosition().getDeltaDistance(ball.getPosition());
			} else {
				int distance = (int) r.getPosition().getDeltaDistance(ball.getPosition());
				
				if(distance < minDistance) {
					closestRobot = r;
					minDistance = distance;
				}
			}
		}
		
		return closestRobot;
	}
}
