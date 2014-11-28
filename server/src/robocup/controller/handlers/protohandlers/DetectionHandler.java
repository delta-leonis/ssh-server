package robocup.controller.handlers.protohandlers;

import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.filter.Kalman;
import robocup.input.protobuf.MessagesRobocupSslDetection.SSL_DetectionBall;
import robocup.input.protobuf.MessagesRobocupSslDetection.SSL_DetectionFrame;
import robocup.input.protobuf.MessagesRobocupSslDetection.SSL_DetectionRobot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import robocup.model.Ally;
import robocup.model.Ball;
import robocup.model.Enemy;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.model.Team;
import robocup.model.World;
import robocup.model.enums.Color;

public class DetectionHandler {

	private World world;
	// TODO: read in validRobotId's from somewhere else / no more hardcoded id's
	int validRobotIDs[] = { 1, 3 };
	Kalman allyFilter[] = new Kalman[12];
	Kalman enemyFilter[] = new Kalman[12];
	Kalman ballFilter;

	public DetectionHandler(World world) {
		this.world = world;
		Ball b = world.getBall();
		ballFilter = new Kalman(new Point(b.getPosition().getX(),b.getPosition().getY()),0,0);
	}

	/**
	 * Process a Detection frame
	 */
	public void process(SSL_DetectionFrame message) {
		processRobots(message.getRobotsBlueList(), message.getRobotsYellowList(), message.getTCapture(), message.getCameraId());
		processBalls(message.getBallsList(), message.getTCapture(), message.getCameraId());
		world.HandlerFinished("detection");
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
	public void processRobots(List<SSL_DetectionRobot> blueList, List<SSL_DetectionRobot> yellowList, double time, int camNo) {

		for (SSL_DetectionRobot robot : blueList) {
			updateRobot(Color.BLUE, robot, time, camNo);
		}

		for (SSL_DetectionRobot robot : yellowList) {
			updateRobot(Color.YELLOW, robot, time, camNo);
		}
	}

	/**
	 * Remove all inactive robots from the team
	 * 
	 * @param team
	 */
	private void removeMissingRobots(Team team) {
		Iterator<Robot> it = team.getRobots().iterator();

		while (it.hasNext())
			// if(it.next().getLastUpdateTime() + 0.05 <
			// Calendar.getInstance().getTimeInMillis() / 1000)
			// it.remove();

			if (it.next().getLastUpdateTime() + 0.02 < Calendar.getInstance().getTimeInMillis() / 1000)
				it.remove();
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

		boolean robotAdded = false;

		// TODO: make this method better/more efficient.

		Robot robot = t.getRobotByID(robotMessage.getRobotId());

		if (robot == null) { // Create robot object
			if (world.getOwnTeamColor().equals(color)) {
				for (int id : validRobotIDs) {
					// filter out all robots that should not be available
					if (robotMessage.getRobotId() == id) {
						// if the robot is validated add it to the ally's list
						t.addRobot(new Ally(robotMessage.getRobotId(), false, robotMessage.getHeight(), 18.0, t, 1));
						allyFilter[id] = new Kalman(new Point(robotMessage.getX(), robotMessage.getY()), 0, 0);
						robotAdded = true;
					}
				}
			} else {
				enemyFilter[robotMessage.getRobotId()] = new Kalman(new Point(robotMessage.getX(), robotMessage.getY()), 0, 0);
				t.addRobot(new Enemy(robotMessage.getRobotId(), false, robotMessage.getHeight(), 18.0, t));
				robotAdded = true;
			}
		}

		robot = t.getRobotByID(robotMessage.getRobotId());
		if (robot != null) {
			Point filterPoint = new Point(robotMessage.getX(), robotMessage.getY());
			Kalman filter;
			if(world.getOwnTeamColor().equals(color)){
				filter = allyFilter[robot.getRobotID()];
			}
			else{
				filter = enemyFilter[robot.getRobotID()];
			}
			int xSpeed = (int) (robotMessage.getX() - filter.getLastX());
			int ySpeed = (int) (robotMessage.getY() - filter.getLastY());
			Point filteredPoint = filter.filterPoint(filterPoint, xSpeed, ySpeed);

			if (robotMessage.hasOrientation()) {
				int degrees = (int) Math.toDegrees(robotMessage.getOrientation());
				robot.update(new Point(filteredPoint.getX(), filteredPoint.getY()), updateTime, degrees, camNo);
			} else {
				robot.update(new Point(filteredPoint.getX(), filteredPoint.getY()), updateTime, camNo);
			}
		}

		if (robotAdded) {
			world.RobotAdded();
		}


		/*
		 * TODO: every once in a while remove all robots from the model,
		 */
		// remove missing robots from teams, needs to be done here to prevent
		// accessing the same list in two threads
		// adding locks for the list would be too slow

		// removeMissingRobots(world.getAlly());
		// removeMissingRobots(world.getEnemy());
	}

	// public void logToCSV(float x, float y, double speed, float confidence,
	// int degrees, double updateTime, int camNo) {
	// BufferedWriter writer = null;
	// FileWriter fw;
	// try {
	// File file = new File("log/log.log");
	// if (!file.exists()) {
	// file.createNewFile();
	// }
	// fw = new FileWriter(file, true);
	// writer = new BufferedWriter(fw);
	// writer.write(x + "," + y + "," + speed + "," + confidence + "," + degrees
	// + "," + updateTime + "," + camNo);
	// writer.newLine();
	// writer.flush();
	// writer.close();
	// fw.close();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
}