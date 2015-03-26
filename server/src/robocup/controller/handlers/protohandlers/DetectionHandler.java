package robocup.controller.handlers.protohandlers;

import java.util.ArrayList;
import java.util.List;

import robocup.filter.Kalman;
import robocup.input.protobuf.MessagesRobocupSslDetection.SSL_DetectionBall;
import robocup.input.protobuf.MessagesRobocupSslDetection.SSL_DetectionFrame;
import robocup.input.protobuf.MessagesRobocupSslDetection.SSL_DetectionRobot;
import robocup.model.Ball;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.model.Team;
import robocup.model.World;
import robocup.model.enums.TeamColor;

public class DetectionHandler {

	private World world;
	// TODO: read in validRobotId's from somewhere else / no more hardcoded id's
	int validRobotIDs[] = { 6 };
	int validEnemyRobotIDs[] = {  };
	Kalman allyFilter[] = new Kalman[12];
	Kalman enemyFilter[] = new Kalman[12];
	Kalman ballFilter;

	public DetectionHandler(World world) {
		this.world = world;
		Ball b = world.getBall();
		ballFilter = new Kalman(new Point(b.getPosition().getX(), b.getPosition().getY()), 0, 0);
	}

	/**
	 * Process a Detection frame
	 */
	public void process(SSL_DetectionFrame message) {
		processRobots(message.getRobotsBlueList(), message.getRobotsYellowList(), message.getTCapture(),
				message.getCameraId());
		processBalls(message.getBallsList(), message.getTCapture(), message.getCameraId());
		world.HandlerFinished("detection");

		World.getInstance().getGUI().update("robotContainer");
		World.getInstance().getGUI().update("widgetContainer");
	}

	/**
	 * process all balls
	 * 
	 * @param balls
	 */
	public void processBalls(List<SSL_DetectionBall> balls, double time, int camNo) {
		for (SSL_DetectionBall ball : balls) {
			updateBall(ball, time, camNo);
		}
	}

	/**
	 * setPosition of ball
	 * 
	 * @param ball
	 */
	public void updateBall(SSL_DetectionBall ball, double time, int camNo) {
		Point filterPoint = new Point(ball.getX(), ball.getY());
		int xSpeed = (int) (ball.getX() - ballFilter.getLastX());
		int ySpeed = (int) (ball.getY() - ballFilter.getLastY());
		Point filteredPoint = ballFilter.filterPoint(filterPoint, xSpeed, ySpeed);

		if (ball.hasZ()) {
			world.getBall().update(time, filteredPoint, ball.getZ(), camNo);
		} else {
			world.getBall().update(filteredPoint, time, camNo);
		}
	}

	/**
	 * call update for every robot in the message
	 * 
	 * @param blueList
	 * @param yellowList
	 */
	public void processRobots(List<SSL_DetectionRobot> blueList, List<SSL_DetectionRobot> yellowList, double time,
			int camNo) {

		for (SSL_DetectionRobot robot : blueList) {
			for (int id : validEnemyRobotIDs) {
				if (robot.getRobotId() == id) {
					updateRobot(TeamColor.BLUE, robot, time, camNo);
				}
			}
		}

		for (SSL_DetectionRobot robot : yellowList) {
			for (int id : validRobotIDs) {
				if (robot.getRobotId() == id) {
					updateRobot(TeamColor.YELLOW, robot, time, camNo);
				}
			}
		}
		
		if(World.getInstance().getReferee().getOwnTeamColor()== TeamColor.YELLOW){
			updateOnSight(yellowList);
		}
		else{
			updateOnSight(blueList);
		}
	}
	
	/**
	 * Updates the ally team's robots to be set "onSight"  or not onSight.
	 * Setting this variable to true in a robot will allow the GUI to display it as "Online"
	 * @param robotList A list with the Detected Robots from the ally team.
	 */
	public void updateOnSight(List<SSL_DetectionRobot> robotList){
		ArrayList<Robot> team = World.getInstance().getAllRobots();
		for(Robot ally : team){
			ally.setOnSight(false);
			for(SSL_DetectionRobot allyMsg: robotList){
				if(ally.getRobotId() == allyMsg.getRobotId()){
					ally.setOnSight(true);
					break;
				}
			}
		}
	}

	/**
	 * Updates position of existing robot or creates it.
	 * 
	 * @param color
	 *            of the robot, to determine team.
	 * @param robotMessage
	 *            the actual message
	 * @param updateTime
	 *            time of update
	 */
	public void updateRobot(TeamColor color, SSL_DetectionRobot robotMessage, double updateTime, int camNo) {
		Team t = world.getTeamByColor(color);
		// No team or robotID set. Reject data.
		if (t == null || robotMessage.hasRobotId() == false) {
			return;
		}

//		boolean robotAdded = false;

		Robot robot = t.getRobotByID(robotMessage.getRobotId());

// 		TODO remove this section, 
// 		 is deprecated because team members are initialized at the start,
// 		 and aren' t removed anymore
 		 
 		 
		if (allyFilter[robotMessage.getRobotId()] == null) { // Create robot object
			if (world.getReferee().getOwnTeamColor().equals(color)) {
				for (int id : validRobotIDs) {
					// filter out all robots that should not be available
					if (robotMessage.getRobotId() == id) {
						// if the robot is validated add it to the ally's list
						Point p = new Point(robotMessage.getX(), robotMessage.getY());
						allyFilter[id] = new Kalman(p, 0, 0);
					}
				}
			} else {
				enemyFilter[robotMessage.getRobotId()] = new Kalman(
						new Point(robotMessage.getX(), robotMessage.getY()), 0, 0);
			}
		}

		robot = t.getRobotByID(robotMessage.getRobotId());
		if (robot != null) {
			Kalman filter;
			if (world.getReferee().getOwnTeamColor().equals(color)) {
				filter = allyFilter[robot.getRobotId()];
			} else {
				filter = enemyFilter[robot.getRobotId()];
			}

			Point filterPoint = new Point(robotMessage.getX(), robotMessage.getY());

			// deltadistance between predicted and measured point
			double deltaDistance = filter.getPredictPoint().getDeltaDistance(filterPoint);
			int xSpeed = (int) (robotMessage.getX() - filter.getLastX());
			int ySpeed = (int) (robotMessage.getY() - filter.getLastY());

			// Point data after Kalman filtering
			Point filteredPoint = filter.filterPoint(filterPoint, xSpeed, ySpeed);
			filter.predictPoint(); // predict new point for next iteration

			// if predicted and measured points are close update position data
			if (deltaDistance < 20) {
				if (robotMessage.hasOrientation()) {
					int degrees = (int) Math.toDegrees(robotMessage.getOrientation());
					robot.update(new Point(filteredPoint.getX(), filteredPoint.getY()), updateTime, degrees, camNo);
				} else {
					robot.update(new Point(filteredPoint.getX(), filteredPoint.getY()), updateTime, camNo);
				}
			}
		}
		else
			System.err.printf("DetectionHandler: Could not find robot with ID %d\n", robotMessage.getRobotId());

		//if (robotAdded) {
		//	world.RobotAdded();
		//}
	}
}
