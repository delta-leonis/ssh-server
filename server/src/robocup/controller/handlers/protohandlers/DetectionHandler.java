package robocup.controller.handlers.protohandlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import robocup.filter.BallUKF;
import robocup.filter.RobotUKF;
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

	//Max distance between ball and closest robot to be assigned to ballowner
	private static final int MAX_DISTANCE = 160;

	private World world;

	private Map<Integer, RobotUKF> allyFilter = new HashMap<Integer, RobotUKF>();
	private Map<Integer, RobotUKF> enemyFilter = new HashMap<Integer, RobotUKF>();
	private BallUKF ballFilter;

	/**
	 * Constructs DetectionHandler. Also initiates {@link Kalman} filter for the {@link Ball}
	 */
	public DetectionHandler() {
		world = World.getInstance();
		ballFilter = new BallUKF(new FieldPoint(0, 0));
	}

	private boolean isValidCameraId(FieldPoint measuredPosition, int camNo) {
		switch (camNo) {
		case 0:
			if (measuredPosition.getX() >= 0 || measuredPosition.getY() <= 0)
				return false;
			break;
		case 1:
			if (measuredPosition.getX() <= 0 || measuredPosition.getY() >= 0)
				return false;
			break;
		case 2:
			if (measuredPosition.getX() <= 0 || measuredPosition.getY() <= 0)
				return false;
			break;
		case 3:
			if (measuredPosition.getX() >= 0 || measuredPosition.getY() >= 0)
				return false;
			break;
		}

		return true;
	}

	/**
	 * Process a {@link SSL_DetectionFrame}, which is a message from the Robocup SSL Vision program.
	 */
	public void process(SSL_DetectionFrame message) {
		world.setLastTimestamp((long) message.getTCapture());

		processRobots(message.getRobotsBlueList(), message.getRobotsYellowList(), message.getTCapture(),
				message.getCameraId());
		processBalls(message.getBallsList(), message.getTCapture(), message.getCameraId());
		world.HandlerFinished("detection");
	}

	/**
	 * Process all balls given by the SSL Vision Program.
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
	 * @param balls The balls currently on the field
	 * @param time The time of detection.
	 * @param camNo The ID of the camera that detected the ball
	 */
	public void updateBall(SSL_DetectionBall ball, double time, int camNo) {
		FieldPoint measuredPosition = new FieldPoint(ball.getX(), ball.getY());

		if (!isValidCameraId(measuredPosition, camNo))
			return;

		ballFilter.run(measuredPosition);
		FieldPoint ballPosition = new FieldPoint(ballFilter.getX(), ballFilter.getY());
		double speed = Math.sqrt(Math.pow(ballFilter.getXVelocity(), 2) + Math.pow(ballFilter.getYVelocity(), 2));
		double direction = Math.toDegrees(Math.atan2(ballFilter.getYVelocity(), ballFilter.getXVelocity()));
		Robot closestRobot = world.getClosestRobotToBall();
		Robot owner = (closestRobot.getPosition().getDeltaDistance(ballPosition) < MAX_DISTANCE + Robot.DIAMETER ? closestRobot : null);
		world.getBall().update((long) time, ballPosition, ball.getZ(), speed,
				direction, owner);
	}

	/**
	 * Update every {@link Robot} in {@link World}
	 * @param blueList		list with every {@link SSL_DetectionRobot} in blue team
	 * @param yellowList	list with every {@link SSL_DetectionRobot} in yellow team
	 */
	public void processRobots(List<SSL_DetectionRobot> blueList, List<SSL_DetectionRobot> yellowList, double time,
			int camNo) {

		for (SSL_DetectionRobot robot : blueList)
			updateRobot(TeamColor.BLUE, robot, time, camNo);

		for (SSL_DetectionRobot robot : yellowList)
			updateRobot(TeamColor.YELLOW, robot, time, camNo);

		updateOnSight(yellowList, TeamColor.YELLOW);
		updateOnSight(blueList, TeamColor.BLUE);
	}

	/**
	 * Updates the ally {@link Team}'s {@link Robot robots} to be set "onSight"  or not onSight.
	 * Setting this variable to true in a {@link Robot} will allow the GUI to display it as "Online"
	 * @param robotList A list with the Detected Robots from the ally team.
	 */
	public void updateOnSight(List<SSL_DetectionRobot> robotList, TeamColor teamcolor) {
		ArrayList<Robot> team = (teamcolor.equals(world.getReferee().getAlly().getColor()) ? world.getReferee().getAlly().getRobots() : world.getReferee().getEnemy().getRobots());

		for (Robot robot : team)
			robot.setOnSight(world.getLastTimestamp() - robot.getLastUpdateTime() <= 2);
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
		Robot robot = t.getRobotByID(robotMessage.getRobotId());
		boolean isAlly = world.getReferee().getAllyTeamColor().equals(color);

		if (isAlly && !world.getValidAllyIDs().contains(robotMessage.getRobotId()) || !isAlly
				&& !world.getValidEnemyIDs().contains(robotMessage.getRobotId()))
			return;

		// create filter if this is the first detection for this robot
		if (isAlly && allyFilter.get(robotMessage.getRobotId()) == null)
			allyFilter.put(robotMessage.getRobotId(),
					new RobotUKF(new FieldPoint(robotMessage.getX(), robotMessage.getY())));
		else if (!isAlly && enemyFilter.get(robotMessage.getRobotId()) == null)
			enemyFilter.put(robotMessage.getRobotId(),
					new RobotUKF(new FieldPoint(robotMessage.getX(), robotMessage.getY())));

		FieldPoint measuredPosition = new FieldPoint(robotMessage.getX(), robotMessage.getY());

		if (!isValidCameraId(measuredPosition, camNo))
			return;

		if (robot != null) {
			RobotUKF filter = isAlly ? allyFilter.get(robotMessage.getRobotId()) : enemyFilter.get(robotMessage
					.getRobotId());

			filter.run(measuredPosition, robotMessage.getOrientation());

			FieldPoint newPosition = new FieldPoint(filter.getX(), filter.getY());
			double speed = Math.sqrt(Math.pow(filter.getXVelocity(), 2) + Math.pow(filter.getYVelocity(), 2));
			double direction = Math.toDegrees(Math.atan2(filter.getYVelocity(), filter.getXVelocity()));

			robot.update(newPosition, (long) updateTime, Math.toDegrees(filter.getOrientation()), speed, direction);
		}
	}
}
