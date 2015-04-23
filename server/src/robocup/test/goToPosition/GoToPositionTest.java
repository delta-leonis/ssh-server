package robocup.test.goToPosition;

import static org.junit.Assert.assertTrue;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.Test;

import robocup.Main;
import robocup.controller.ai.movement.GotoPosition;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.Team;
import robocup.model.World;

public class GoToPositionTest{
	private GotoPosition go;
	
	private String message;
	
	private static Logger LOGGER = Logger.getLogger(Main.class.getName());

	
	@Test
	public void test1ID(){
		System.out.println("test1ID");
		setupOneRobot(1, new FieldPoint(0,0), new FieldPoint(0,0), new FieldPoint(0,0));
		go.calculate();
		assertTrue(message.split(",")[0].equals("1"));
		System.out.println("Message: " + message);
		System.out.println();
	}
	
	@Test
	public void test2_1Direction(){
		System.out.println("test2_1Direction");
		setupOneRobot(1, new FieldPoint(0,0), new FieldPoint(1000,1000), new FieldPoint(0,0));
		go.calculate();
		assertTrue(message.split(",")[1].equals("45"));
		System.out.println("Message: " + message);
		System.out.println();
	}
	@Test
	public void test2_2Direction(){
		System.out.println("test2_2Direction");
		setupOneRobot(1, new FieldPoint(0,0), new FieldPoint(-1000,1000), new FieldPoint(0,0));
		go.calculate();
		assertTrue(message.split(",")[1].equals("135"));
		System.out.println("Message: " + message);
		System.out.println();
	}
	
	@Test
	public void test2_3Direction(){
		System.out.println("test2_3Direction");
		setupOneRobot(1, new FieldPoint(0,0), new FieldPoint(-1000,-1000), new FieldPoint(0,0));
		go.calculate();
		assertTrue(message.split(",")[1].equals("-135"));
		System.out.println("Message: " + message);
		System.out.println();
	}
	
	@Test
	public void test2_4Direction(){
		System.out.println("test2_4Direction");
		setupOneRobot(1, new FieldPoint(0,0), new FieldPoint(1000,-1000), new FieldPoint(0,0));
		go.calculate();
		assertTrue(message.split(",")[1].equals("-45"));
		System.out.println("Message: " + message);
		System.out.println();
	}
	
	@Test
	public void test2_5Direction(){
		System.out.println("test2_5Direction");
		setupOneRobot(1, new FieldPoint(0,0), new FieldPoint(1000,0), new FieldPoint(0,0));
		go.calculate();
		assertTrue(message.split(",")[1].equals("0"));
		System.out.println("Message: " + message);
		System.out.println();
	}
	
	@Test
	public void test2_6Direction(){
		System.out.println("test2_6Direction");
		setupOneRobot(1, new FieldPoint(0,0), new FieldPoint(-1000,0), new FieldPoint(0,0));
		go.calculate();
		assertTrue(message.split(",")[1].equals("180") || message.split(",")[1].equals("-180"));
		System.out.println("Message: " + message);
		System.out.println();
	}
	
	@Test
	public void test2_7Direction(){
		System.out.println("test2_7Direction");
		setupOneRobot(1, new FieldPoint(0,0), new FieldPoint(0,1000), new FieldPoint(0,0));
		go.calculate();
		assertTrue(message.split(",")[1].equals("90"));
		System.out.println("Message: " + message);
		System.out.println();
	}
	
	@Test
	public void test2_8Direction(){
		System.out.println("test2_8Direction");
		setupOneRobot(1, new FieldPoint(0,0), new FieldPoint(0,-1000), new FieldPoint(0,0));
		go.calculate();
		assertTrue(message.split(",")[1].equals("-90"));
		System.out.println("Message: " + message);
		System.out.println();
	}
	
	@Test
	public void test2_9Direction(){
		System.out.println("test2_9Direction");
		setupOneRobot(1, new FieldPoint(-1500,-1500), new FieldPoint(-1000,-1000), new FieldPoint(0,0));
		go.calculate();
		
		System.out.println("Test 9 Message: " + message);
		System.out.println();

		assertTrue(message.split(",")[1].equals("45"));
	}
	
	@Test
	public void test2_10Direction(){
		System.out.println("test2_10Direction");
		setupOneRobot(1, new FieldPoint(-1000,-1000), new FieldPoint(-1500,-1500), new FieldPoint(0,0));
		go.calculate();
		System.out.println("Message: " + message);
		System.out.println();
		assertTrue(message.split(",")[1].equals("-135"));
	}
	
	@Test
	public void test3_1Target(){
		System.out.println("test3_1Direction");
		setupOneRobot(1, new FieldPoint(0,0), new FieldPoint(0,0), new FieldPoint(500,500));
		go.calculate();
		System.out.println("Message: " + message);
		System.out.println();
		assertTrue(message.split(",")[3].equals("1000"));
	}
	
	@Test
	public void test3_2Target(){
		System.out.println("test3_2Direction");
		setupOneRobot(1, new FieldPoint(0,0), new FieldPoint(0,0), new FieldPoint(-500,-500));
		go.calculate();
		System.out.println("Message: " + message);
		System.out.println();
		assertTrue(message.split(",")[3].equals("-3000"));
	}
	
	@Test
	public void test3_3Target(){
		System.out.println("test3_3Direction");
		setupOneRobot(1, new FieldPoint(0,0), new FieldPoint(0,0), new FieldPoint(-500,500));
		go.calculate();
		System.out.println("Message: " + message);
		System.out.println();
		assertTrue(message.split(",")[3].equals("3000"));
	}
	
	@Test
	public void test3_4Target(){
		System.out.println("test3_4Direction");
		setupOneRobot(1, new FieldPoint(0,0), new FieldPoint(0,0), new FieldPoint(500,-500));
		go.calculate();
		System.out.println("Message: " + message);
		System.out.println();
		assertTrue(message.split(",")[3].equals("-1000"));
	}
	
	@Test
	public void test4_1TargetTurnedRobot(){
		System.out.println("test4_1TargetTurnedRobot");
		setupOneRobot(1, new FieldPoint(0,0), new FieldPoint(0,0), new FieldPoint(0,500));
		World.getInstance().getReferee().getAlly().getRobotByID(1).setOrientation(90);
		go.calculate();
		System.out.println("Message: " + message);
		System.out.println();
		assertTrue(message.split(",")[3].equals("0"));
	}
	
	@Test
	public void test4_2TargetTurnedRobot(){
		System.out.println("test4_1TargetTurnedRobot");
		setupOneRobot(1, new FieldPoint(0,0), new FieldPoint(0,0), new FieldPoint(0,0));
		World.getInstance().getReferee().getAlly().getRobotByID(1).setOrientation(90);
		go.calculate();
		System.out.println("Message: " + message);
		System.out.println();
		assertTrue(message.split(",")[3].equals("-2000"));
	}
	
	/**
	 * Creates one {@link Robot} for the specified {@link Team} and sets it
	 * "on sight"
	 * 
	 * @param position
	 *            The position of the {@link Robot}
	 * @param id
	 *            The ID of the {@link Robot}
	 */
	public void setupOneRobot(int id, FieldPoint position, FieldPoint target, FieldPoint destination) {
		Team team = World.getInstance().getReferee().getAlly();
		Robot robot = team.getRobotByID(id);
		robot.setPosition(position);
		robot.setOnSight(true);
		
		go = new GotoPosition(robot, target, destination);
		LOGGER.addHandler(new LoggerHandler());

	}
	
	/**
	 * Handler for {@link Logger} messages
	 */
	private class LoggerHandler extends Handler {
		@Override
		public void close() throws SecurityException {
		}

		@Override
		public void flush() {
		}

		@Override
		public void publish(LogRecord record) {
			if(record.getLevel() == Level.INFO){
				message = record.getMessage();
			}
		}
	}
}
