package robocup.controller.ai.highLevelBehavior.testbehavior;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
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

		if (attacker != null && ball != null) {
			RobotExecuter executer = findExecuter(robotId, executers);
			Point freePosition = getClosestAllyRobotToBall() == attacker ? null : getFreePosition(null);

			// Initialize executer for this robot
			if (executer == null) {
				executer = new RobotExecuter(attacker);
				executer.setLowLevelBehavior(new Attacker(attacker, ComInterface.getInstance(RobotCom.class),
						freePosition, ball.getPosition(), 0, false, shootDirection));
				new Thread(executer).start();
				executers.add(executer);
				System.out.println("executer created");
			} else {
				getShootDirection(attacker);
				((Attacker) executer.getLowLevelBehavior()).update(freePosition, ball.getPosition(), 0, false,
						shootDirection);
			}
		}
	}

	/**
	 * Get a shoot direction for a robot
	 * @param robot
	 * @return
	 */
	private int getShootDirection(Robot robot) {
		int[] shootDirections = getPossibleShotsAtGoal(robot);

		// System.out.println(shootDirections.length +
		// " possible shooting directions located at:");
		// for(int i = 0; i < shootDirections.length; i++)
		// System.out.print(shootDirections[i] + ", ");
		// System.out.print("\n");

		if (shootDirections.length >= 4)
			return shootDirections[shootDirections.length / 2];

		return 0;
	}

	/**
	 * Calculate possible shot count at the goal for a robot
	 * @param robot the robot
	 * @return possible shot count for the robot
	 */
	private int[] getPossibleShotsAtGoal(Robot robot) {
		int possibleShotCount = 0;
		int possibleShots[] = new int[10];

		// check at 10 different points in the goal, 60 mm from each other
		for (int i = 0; i < 10; i++) {
			Point goalPoint = new Point(world.getField().getLength() / 2, -300 + i * 60);
			Point currentPosition = robot.getPosition();

			Line2D line = new Line2D.Float(currentPosition.getX(), currentPosition.getY(), goalPoint.getX(),
					goalPoint.getY());

			if (!lineIntersectsObject(line, robot.getRobotId())) {
				possibleShots[possibleShotCount] = -300 + i * 60;
				possibleShotCount++;
			}
		}

		int[] toRet = new int[possibleShotCount];
		for (int i = 0; i < possibleShotCount; i++)
			toRet[i] = possibleShots[i];

		return toRet;
	}

	/**
	 * Check if one of the robots intersects the line on which the robot is
	 * going to travel
	 * 
	 * @param lineline
	 *            A Line2D is needed to check if there is an intersection with a
	 *            robot object
	 * @return true if the line intersects an object
	 */
	public boolean lineIntersectsObject(Line2D line, int robotId) {
		Rectangle2D rect = null;
		ArrayList<Robot> objects = new ArrayList<Robot>();
		objects.addAll(world.getEnemy().getRobotsOnSight());
		objects.addAll(world.getAlly().getRobotsOnSight());

		for (Robot r : objects) {
			rect = new Rectangle2D.Float(r.getPosition().getX(), r.getPosition().getY(), 180, 180);
			if (line.intersects(rect) && r.getRobotId() != robotId) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Find a free position for the robot
	 * A position is free when the robot can get the ball passed
	 * @param robot the robot who needs a free position
	 * @return a free position
	 */
	private Point getFreePosition(Robot robot) {
		return new Point(-500, 0);
	}

	/**
	 * Get the closest robot to the ball on our team
	 * @return closest robot
	 */
	private Robot getClosestAllyRobotToBall() {
		ArrayList<Robot> robots = world.getAlly().getRobotsOnSight();
		// System.out.println(robots.size());

		int minDistance = -1;
		Robot closestRobot = null;

		for (Robot r : robots) {
			if (minDistance == -1) {
				closestRobot = r;
				minDistance = (int) r.getPosition().getDeltaDistance(ball.getPosition());
			} else {
				int distance = (int) r.getPosition().getDeltaDistance(ball.getPosition());

				if (distance < minDistance) {
					closestRobot = r;
					minDistance = distance;
				}
			}
		}

		return closestRobot;
	}

	@Override
	public void updateExecuters(ArrayList<RobotExecuter> executers) {
		// TODO Auto-generated method stub

	}
}
