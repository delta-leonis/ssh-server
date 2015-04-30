package robocup.controller.handlers.protohandlers;

import java.util.ArrayList;
import java.util.List;

import robocup.filter.Kalman;
import robocup.input.protobuf.MessagesRobocupSslDetection.SSL_DetectionBall;
import robocup.input.protobuf.MessagesRobocupSslDetection.SSL_DetectionFrame;
import robocup.input.protobuf.MessagesRobocupSslDetection.SSL_DetectionRobot;
import robocup.model.Ball;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.Team;
import robocup.model.World;
import robocup.model.enums.TeamColor;
/**
 * Handler for {@link SSL_DetectionFrame} messages, will process all {@link Robot Robots} and {@link Ball Balls}
 */
public class DetectionHandler {

	private World world;
	// TODO: read in validRobotId's from somewhere else / no more hardcoded id's
//	int validRobotIDs[] = { 6 };
//	int validEnemyRobotIDs[] = {  };

	Kalman allyFilter[] = new Kalman[12];
	Kalman enemyFilter[] = new Kalman[12];
	Kalman ballFilter;

	/**
	 * Constructs DetectionHandler. Also initiates {@link Kalman} filter for the {@link Ball}
	 */
	public DetectionHandler() {
		world = World.getInstance();
		Ball b = world.getBall();
		ballFilter = new Kalman(new FieldPoint(b.getPosition().getX(), b.getPosition().getY()), 0, 0);
	}

	/**
	 * Process a {@link SSL_DetectionFrame}, which is a message from the Robocup SSL Vision program.
	 */
	public void process(SSL_DetectionFrame message) {
		processRobots(message.getRobotsBlueList(), message.getRobotsYellowList(), message.getTCapture(),
				message.getCameraId());
		processBalls(message.getBallsList(), message.getTCapture(), message.getCameraId());
		world.HandlerFinished("detection");
	}

	/**
	 * Process all balls given by the SSL Vision Program.
	 * 
	 * @param balls The balls currently on the field
	 * @param time The time of detection.
	 * @param camNo The ID of the camera that detected the ball
	 */
	public void processBalls(List<SSL_DetectionBall> balls, double time, int camNo) {
		for (SSL_DetectionBall ball : balls) {
			updateBall(ball, time, camNo);
		}
	}

	/**
	 * Updates position of the {@link Ball} in the {@link World}
	 * 
	 * @param balls The balls currently on the field
	 * @param time The time of detection.
	 * @param camNo The ID of the camera that detected the ball
	 */
	public void updateBall(SSL_DetectionBall ball, double time, int camNo) {
		FieldPoint filterPoint = new FieldPoint(ball.getX(), ball.getY());
		double xSpeed = (ball.getX() - ballFilter.getLastX());
		double ySpeed = (ball.getY() - ballFilter.getLastY());
		FieldPoint filteredPoint = ballFilter.filterPoint(filterPoint, xSpeed, ySpeed);

		if (ball.hasZ()) {
			world.getBall().update(time, filteredPoint, ball.getZ(), camNo);
		} else {
			world.getBall().update(filteredPoint, time, camNo);
		}
	}

	/**
	 * Update every {@link Robot} in {@link World} 
	 * 
	 * @param blueList		list with every {@link SSL_DetectionRobot} in blue team
	 * @param yellowList	list with every {@link SSL_DetectionRobot} in yellow team
	 */
	public void processRobots(List<SSL_DetectionRobot> blueList, List<SSL_DetectionRobot> yellowList, double time,
			int camNo) {

		for (SSL_DetectionRobot robot : blueList) {
			for (int id : World.getInstance().getValidRobotIDs()) {
				if (robot.getRobotId() == id) {
					updateRobot(TeamColor.BLUE, robot, time, camNo);
				}
			}
		}

		for (SSL_DetectionRobot robot : yellowList) {
			for (int id : World.getInstance().getValidRobotIDs()) {
				if (robot.getRobotId() == id) {
					updateRobot(TeamColor.YELLOW, robot, time, camNo);
				}
			}
		}
		
		if(World.getInstance().getReferee().getAllyTeamColor()== TeamColor.YELLOW){
			updateOnSight(yellowList);
		}
		else{
			updateOnSight(blueList);
		}
	}
	
	/**
	 * Updates the ally {@link Team}'s {@link Robot robots} to be set "onSight"  or not onSight.
	 * Setting this variable to true in a {@link Robot} will allow the GUI to display it as "Online"
	 * @param robotList A list with the Detected Robots from the ally team.
	 */
	public void updateOnSight(List<SSL_DetectionRobot> robotList){
		ArrayList<Robot> team = World.getInstance().getReferee().getAlly().getRobots();
		for(Robot ally : team){
			ally.setOnSight(false);
			for(SSL_DetectionRobot allyMsg: robotList){
				if(ally.getRobotId() == allyMsg.getRobotId()){
//					System.out.println("On sight ID: " + allyMsg.getRobotId());
					ally.setOnSight(true);
					break;
				}
			}
		}
	}

	/**
	 * Updates position of existing {@link Robot} in the {@link World}
	 * 
	 * @param color {@link TeamColor} of the robot to determine to which {@link Team} it belongs.    
	 * @param robotMessage The {@link SSL_DetectionRobot} message sent by the SSL Vision Program.
	 * @param updateTime time of update in seconds.
	 */
	public void updateRobot(TeamColor color, SSL_DetectionRobot robotMessage, double updateTime, int camNo) {
		Team t = world.getTeamByColor(color);
		// No team or robotID set. Reject data.
		if (t == null || robotMessage.hasRobotId() == false) {
			return;
		}

		Robot robot = t.getRobotByID(robotMessage.getRobotId());

		//add robot to filter if not so already
		if (allyFilter[robotMessage.getRobotId()] == null) { // Create robot object
			if (world.getReferee().getAllyTeamColor().equals(color)) {
				for (int id : World.getInstance().getValidRobotIDs()) {
					// filter out all robots that should not be available
					if (robotMessage.getRobotId() == id) {
						// if the robot is validated add it to the ally's list
						FieldPoint p = new FieldPoint(robotMessage.getX(), robotMessage.getY());
						allyFilter[id] = new Kalman(p, 0, 0);
						break;
					}
				}
			} else {
				enemyFilter[robotMessage.getRobotId()] = new Kalman(
						new FieldPoint(robotMessage.getX(), robotMessage.getY()), 0, 0);
			}
		}

		robot = t.getRobotByID(robotMessage.getRobotId());
		if (robot != null) {
			Kalman filter;
			if (world.getReferee().getAllyTeamColor().equals(color)) {
				filter = allyFilter[robot.getRobotId()];
			} else {
				filter = enemyFilter[robot.getRobotId()];
			}

			FieldPoint filterPoint = new FieldPoint(robotMessage.getX(), robotMessage.getY());

			// deltadistance between predicted and measured point
			double deltaDistance = filter.getPredictPoint().getDeltaDistance(filterPoint);
			double xSpeed = (robotMessage.getX() - filter.getLastX());
			double ySpeed = (robotMessage.getY() - filter.getLastY());

			// Point data after Kalman filtering
			FieldPoint filteredPoint = filter.filterPoint(filterPoint, xSpeed, ySpeed);
			filter.predictPoint(); // predict new point for next iteration

			// if predicted and measured points are close update position data
			if (deltaDistance < 20) {
				if (robotMessage.hasOrientation()) {
					double degrees = Math.toDegrees(robotMessage.getOrientation());
					robot.update(new FieldPoint(filteredPoint.getX(), filteredPoint.getY()), updateTime, degrees, camNo);
				} else {
					robot.update(new FieldPoint(filteredPoint.getX(), filteredPoint.getY()), updateTime, camNo);
				}
			}
		}
		else
			System.err.printf("DetectionHandler: Could not find robot with ID %d\n", robotMessage.getRobotId());
	}
}
