package robocup.controller.handlers.protohandlers;

import robocup.input.protobuf.MessagesRobocupSslDetection.SSL_DetectionBall;
import robocup.input.protobuf.MessagesRobocupSslDetection.SSL_DetectionFrame;
import robocup.input.protobuf.MessagesRobocupSslDetection.SSL_DetectionRobot;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import robocup.model.Ally;
import robocup.model.Enemy;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.model.Team;
import robocup.model.World;
import robocup.model.enums.Color;

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
	}

	/**
	 * process all balls
	 * 
	 * @param balls
	 */
	public void processBalls(List<SSL_DetectionBall> balls, double time, int camNo) {
		for (SSL_DetectionBall ball : balls) {
			updateBall(ball, time, camNo);
			//System.out.println("test" + balls.indexOf(ball));
		}
	}

	/**
	 * setPosition of ball
	 * 
	 * @param ball
	 */
	public void updateBall(SSL_DetectionBall ball, double time, int camNo) {
		Point p = new Point((int) ball.getX(), (int) ball.getY());
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
	 * Remove all inactive robots from the team
	 * @param team 
	 */
	private void removeMissingRobots(Team team) {
		Iterator<Robot> it = team.getRobots().iterator();
		
		while(it.hasNext())
			if(it.next().getLastUpdateTime() + 0.05 < Calendar.getInstance().getTimeInMillis() / 1000) 
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

		Robot robot = t.getRobotByID(robotMessage.getRobotId());
		boolean robotAddedPending = false;
		
		if (robot == null) { // Create robot object
			if (world.getOwnTeamColor().equals(color)) {
				// TODO: How to set/determine channel of robot.
				// TODO: What to do with diameter.
				t.addRobot(new Ally(robotMessage.getRobotId(), false, robotMessage.getHeight(), 18.0, t, 1));
			} else {
				t.addRobot(new Enemy(robotMessage.getRobotId(), false, robotMessage.getHeight(), 18.0, t));
			}
			
			robotAddedPending = true;
		}

		robot = t.getRobotByID(robotMessage.getRobotId());
		if (robotMessage.hasOrientation()) {
			int degrees = (int) Math.toDegrees(robotMessage.getOrientation());

			robot.update(new Point(robotMessage.getX(), robotMessage.getY()), updateTime, degrees, camNo);
		} else {
			robot.update(new Point(robotMessage.getX(), robotMessage.getY()), updateTime, camNo);
		}

		//!TODO nettere uitwerking van dit, mbv andere notify
		if(robotAddedPending) {
			world.RobotAdded();
		}
		
		// remove missing robots from teams, needs to be done here to prevent accessing the same list in two threads
		// adding locks for the list would be too slow
		removeMissingRobots(world.getAlly());
		removeMissingRobots(world.getEnemy());
	}


//	public void logToCSV(float x, float y, double speed, float confidence, int degrees, double updateTime, int camNo) {
//		BufferedWriter writer = null;
//		FileWriter fw;
//		try {
//			File file = new File("log/log.log");
//			if (!file.exists()) {
//				file.createNewFile();
//			}
//			fw = new FileWriter(file, true);
//			writer = new BufferedWriter(fw);
//			writer.write(x + "," + y + "," + speed + "," + confidence + "," + degrees + "," + updateTime + "," + camNo);
//			writer.newLine();
//			writer.flush();
//			writer.close();
//			fw.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}