package robocup.controller.handlers.protohandlers;

import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
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
import robocup.model.Enemy;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.model.Team;
import robocup.model.World;
import robocup.model.enums.Color;

public class DetectionHandler {

	private World world;
	
	//!TODO tijdelijke check op timeout variabelen
	/*
	final int robotTimeoutThreshold = 5;
	int robotTimeouts[] ;
	boolean robotUpdated[] ;*/
	
	Map<Integer, Integer> robotTimeoutCamera1;
	Map<Integer, Boolean> robotUpdatedCamera1;
	
	
	int validRobotIDs[] = {1, 3};
	List<Map<Integer, Integer>> robotTimeout;
	List<Map<Integer, Boolean>> robotUpdated;
	final int numberOfCameras = 2;
	final int timeOutThreshold = 5;
			//camId, 
	
	
	
	
	
	
	//Map<Integer, Map<Integer, Integer[]>> clusterfuck = new HashMap<Integer, Map<Integer, Integer[]>>();
	

	public DetectionHandler(World world) {
		this.world = world;
		/*
		this.robotTimeouts = new int[12];
		Arrays.fill(robotTimeouts, 0);
		this.robotUpdated = new boolean[12];
		Arrays.fill(robotUpdated, false);*/
		
		robotTimeoutCamera1= new HashMap<Integer, Integer>();
		robotUpdatedCamera1= new HashMap<Integer, Boolean>();
		for(int robotId : validRobotIDs) {
			robotTimeoutCamera1.put(robotId,5);
			robotUpdatedCamera1.put(robotId, false);
		}
		
//		robotTimeout = new Map<Integer, Integer>[numberOfCameras]; 
		/*
		robotTimeout = new ArrayList<Map<Integer, Integer>>();
		robotUpdated = new ArrayList<Map<Integer, Boolean>>();
		for(int i = 0; i < numberOfCameras; i++) {
			HashMap<Integer, Integer> tempTimeout = new HashMap<Integer, Integer>();
			HashMap<Integer, Boolean> tempUpdated = new HashMap<Integer, Boolean>();
			
			for(int robotId : validRobotIDs) {
				tempTimeout.put(robotId,timeOutThreshold);
				tempUpdated.put(robotId, false);
			}
			
			robotTimeout.add(tempTimeout);
			robotUpdated.add(tempUpdated);
		}
		*/
		
		/*
		 
		 
		 
		 Camera [4]
		 		Robot [6]
		 				TimoutTimer|int
		 				IsUpdated|bool
		 
		 
		 */
		
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
		
		
		// Set all robot updates flags to false
		//Arrays.fill(robotUpdated, false);
		
		
	
		for (SSL_DetectionRobot robot : yellowList) {
			
			// Robot valid?
			
			/*
			 * int validRobotIDs[] = {1, 2, 3};
	boolean robotUpdatedCamera1[] = new boolean[validRobotIDs.length];
	Map<Integer, Integer> robotTimeoutCamera1= new HashMap
	robotUpdatedCamera1<int, boolean>
			 */
			
			/*
			 
			 
			 check of robot toegstaan is
			 		
			 		als roboTimeout > 0, verlaag met 1
			 
			 
			 		als roboTimeout == 0
			 			update robot
			 		
			 		
			 		als na de loop robot niet geupdate is, reset de timeout naar 5
			 */
			// check of robot toegstaan is
			if(camNo == 1) {
				if(robotTimeoutCamera1.containsKey(robot.getRobotId())) {
					//als roboTimeout > 0, verlaag met 1
					if(robotTimeoutCamera1.get(robot.getRobotId()) > 0) {
						robotTimeoutCamera1.put(robot.getRobotId(), robotTimeoutCamera1.get(robot.getRobotId()) - 1);
					}
	
					//als roboTimeout == 0
					if(robotTimeoutCamera1.get(robot.getRobotId()) <= 0) {
						updateRobot(Color.YELLOW, robot, time, camNo);
					} else {
						//System.out.println(" robot: " + robot.getRobotId() + "  mag niet updaten");
					}
					
					//robot is behandeld/geupdate in dit frame
					robotUpdatedCamera1.put(robot.getRobotId(), true);
				}
			}
			
			
			
			
			//updateRobot(Color.YELLOW, robot, time, camNo);
			/*
			robotUpdated[robot.getRobotId()] = true;
			if(robotTimeouts[robot.getRobotId()] < (robotTimeoutThreshold)) {
				robotTimeouts[robot.getRobotId()] = robotTimeouts[robot.getRobotId()] + 2;
			} 
			
			
			if(robotTimeouts[robot.getRobotId()] >= (robotTimeoutThreshold)) {
				//System.out.println("robot :"+robot.getRobotId()+" update allowed: " + robotTimeouts[robot.getRobotId()] );
				updateRobot(Color.YELLOW, robot, time, camNo);
			} else {
				System.out.println("robot: "+robot.getRobotId() +" below threshold: " + robotTimeouts[robot.getRobotId()] );
			}
			
			
		}
		
		for(int i = 0; i < robotUpdated.length; i++ ) {
			if(!robotUpdated[i]) {
				if(robotTimeouts[i] > 0) {
					robotTimeouts[i]--;
				}
			}
			*/
		}
		
		
		//!TODO, zorgen dat straks op alle 4 de camera's een gemeenschappelijk check zit, zodat een robot zichtbaar op cam 1 niet door cam 3
		// uit het team gegooid wordt
		Team team = world.getTeamByColor(Color.YELLOW);
		if(camNo == 1) {
			//als na de loop robot niet geupdate is, reset de timeout naar 5 en de update flag terug naar false
			for (Entry<Integer, Boolean> cursor : robotUpdatedCamera1.entrySet()) {
				if(cursor.getValue() == false) {
					robotTimeoutCamera1.put(cursor.getKey(), 5);
//					System.out.println("Robot: " + cursor.getKey() + "  is kwijt" );
					
					Iterator<Robot> it = team.getRobots().iterator();
					while(it.hasNext()) {
						if(it.next().getRobotID() == cursor.getKey()) {
							it.remove();
						}
					}

				}
				
				robotUpdatedCamera1.put(cursor.getKey(), false);
			}
		}
	}
	

	/**
	 * Remove all inactive robots from the team
	 * @param team 
	 */
	private void removeMissingRobots(Team team) {
		Iterator<Robot> it = team.getRobots().iterator();
		
		while(it.hasNext())
//			if(it.next().getLastUpdateTime() + 0.05 < Calendar.getInstance().getTimeInMillis() / 1000) 
//				it.remove();
		
		if(it.next().getLastUpdateTime() + 0.02 < Calendar.getInstance().getTimeInMillis() / 1000) 
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
		
		//TODO change hardcoded values
		if(robotMessage.getRobotId() != 1 && robotMessage.getRobotId() != 3) {
//			System.out.println("id: "  + robotMessage.getRobotId() + " strength: "  +robotMessage.getConfidence());
//			System.out.println(robotMessage);
		}
		

		
		
		if (robot == null) { // Create robot object
//			System.out.println(robotMessage.getRobotId() + " bestaat niet??");
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
		
		
		//removeMissingRobots(world.getAlly());
		//removeMissingRobots(world.getEnemy());
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