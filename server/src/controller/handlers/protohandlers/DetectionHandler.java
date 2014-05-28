package controller.handlers.protohandlers;

import input.protobuf.MessagesRobocupSslDetection.SSL_DetectionBall;
import input.protobuf.MessagesRobocupSslDetection.SSL_DetectionFrame;
import input.protobuf.MessagesRobocupSslDetection.SSL_DetectionRobot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import model.Ally;
import model.Enemy;
import model.Point;
import model.Robot;
import model.Team;
import model.World;
import model.enums.Color;

public class DetectionHandler {

	private World world;

	public DetectionHandler(World world) {
		this.world = world;
	}

	/**
	 * Process a Detection frame
	 */
	public void process(SSL_DetectionFrame message) {
		processRobots(message.getRobotsBlueList(), message.getRobotsYellowList(), message.getTCapture(),
				message.getCameraId());
		processBalls(message.getBallsList(), message.getTCapture(), message.getCameraId());
		world.HandlerFinished("detection");

//		System.out.println("DATA...");
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
		Point p = new Point((int) ball.getY(), (int) ball.getX());
		if (ball.hasZ()) {
			world.getBall().update(time, p, ball.getZ(), camNo);
		} else {
			world.getBall().update(p, time, camNo);
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
			updateRobot(Color.BLUE, robot, time, camNo);
		}
		for (SSL_DetectionRobot robot : yellowList) {
			updateRobot(Color.YELLOW, robot, time, camNo);
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
	public void updateRobot(Color color, SSL_DetectionRobot robotMessage, double updateTime, int camNo) {
		Team t = world.getTeamByColor(color);
		// No team or robotID set. Reject data.
		if (t == null || robotMessage.hasRobotId() == false) {
			return;
		}

		Robot robot = t.getRobotByID(robotMessage.getRobotId());
		if (robot == null) { // Create robot object
			if (world.getOwnTeamColor().equals(color)) {
				// TODO: How to set/determine channel of robot.
				// TODO: What to do with diameter.
				t.addRobot(new Ally(robotMessage.getRobotId(), false, robotMessage.getHeight(), 10.0, t, 1));
			} else {
				t.addRobot(new Enemy(robotMessage.getRobotId(), false, robotMessage.getHeight(), 10.0, t));
			}
			world.RobotAdded();
		}

		robot = t.getRobotByID(robotMessage.getRobotId());
		if (robotMessage.hasOrientation()) {
			int degrees = (int) Math.toDegrees(robotMessage.getOrientation());
			robot.update(new Point(robotMessage.getX(), robotMessage.getY()), updateTime, degrees, camNo);
		} else {
			robot.update(new Point(robotMessage.getX(), robotMessage.getY()), updateTime, camNo);
		}

		// Log naar CSV
		if (robot.getRobotID() == 9 && robot.getTeam().getColor().equals(Color.YELLOW)) {
			int degrees = 666;
			degrees = (int) Math.toDegrees(robotMessage.getOrientation());
			logToCSV(robotMessage.getX(), robotMessage.getY(), robot.getSpeed(), robotMessage.getConfidence(), degrees,
					updateTime, camNo);
		}

	}


	public void logToCSV(float x, float y, double speed, float confidence, int degrees, double updateTime, int camNo) {
		BufferedWriter writer = null;
		FileWriter fw;
		try {
			File file = new File("log/log.log");
			if (!file.exists()) {
				file.createNewFile();
			}
			fw = new FileWriter(file, true);
			writer = new BufferedWriter(fw);
			writer.write(x + "," + y + "," + speed + "," + confidence + "," + degrees + "," + updateTime + "," + camNo);
			writer.newLine();
			writer.flush();
			writer.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Deze fout is ontstaan door een falende actie van Gerbrand.");
			e.printStackTrace();
		}
	}
}