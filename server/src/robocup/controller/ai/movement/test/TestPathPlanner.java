package robocup.controller.ai.movement.test;


import static org.junit.Assert.*;

import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import robocup.Main;
import robocup.controller.ai.movement.DijkstraPathPlanner;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.Team;
import robocup.model.World;

/**
 * Test class for the pathplanner.
 * Run as JUnit to test.
 * Tests:
 * - Locked in Source
 * - Locked in Destination
 * - Collision
 * 
 * Run the class itself to be presented with a GUI that shows how the path
 * is planned out in multiple situations. 
 * 
 * Note: When generating random robots, it may spawn robots on top of eachother
 *       This may result in no path.
 * @see {@link TestPathPlannerVisualTestPanel}
 */
public class TestPathPlanner extends DijkstraPathPlanner {
	private static final boolean LOGGING = false;	

	/**
	 * Initiate a test World to test the functions. This world has at least an
	 * ally team and an enemy team. Robots will be added in test functions.
	 */
	@Before
	public void initWorld() {
		Main.initTeams();
	}

	/**
	 * Run as application to get a visual representation of the path planner.
	 */
	public static void main(String args[]) {
		 
		JFrame frame = new JFrame();
		frame.setSize((int)(TestPathPlannerPanel.WIDTH * TestPathPlannerPanel.RATIO *1.8), 
				(int)(TestPathPlannerPanel.HEIGHT * TestPathPlannerPanel.RATIO*1.8));
		frame.setContentPane(new TestPathPlannerVisualTestPanel(new TestPathPlanner()));
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 
	}
	
	/**
	 * @returns a {@link TestPathPlannerPanel} with a bunch of random robots already set up.
	 */
	public TestPathPlannerPanel getRandomRobotsTestPanel(){
		Main.initTeams();

		FieldPoint destination = new FieldPoint((int)(Math.random() * TestPathPlannerPanel.WIDTH - TestPathPlannerPanel.WIDTH/2),
				(int)(Math.random() * TestPathPlannerPanel.HEIGHT - TestPathPlannerPanel.HEIGHT/2));
		TestPathPlanner planner = new TestPathPlanner();
		planner.setupRandomRobots(LOGGING);

		return new TestPathPlannerPanel(destination, false, true, planner);
	}

	/**
	 * Simple test to see whether an object crosses a diagonal line at a 45
	 * degree angle.
	 */
	@Test
	public void testIntersect1() {
		if (LOGGING) {
			System.out.println("\nTest Intersect 1");
		}

		testIntersectObject(new Vertex(new FieldPoint(0, 0)), new Vertex(new FieldPoint(
				2000, 2000)), new FieldPoint(1000, 1000), true, LOGGING);
	}

	/**
	 * Simple test to see whether an object crosses a diagonal line at a 45
	 * degree angle.
	 */
	@Test
	public void testIntersect2() {
		if (LOGGING) {
			System.out.println("\nTest Intersect 2");
		}

		testIntersectObject(new Vertex(new FieldPoint(100, 100)), new Vertex(
				new FieldPoint(500, 500)), new FieldPoint(300, 300), true, LOGGING);
	}

	/** Tests whether we always stay at least 90mm away from any Robot. */
	@Test
	public void testIntersect3() {
		if (LOGGING) {
			System.out.println("\nTest Intersect 3\nTest East");
		}
		testIntersectObject(new Vertex(new FieldPoint(0, 0)), new Vertex(new FieldPoint(
				50, 0)), new FieldPoint(231, 0), false, LOGGING); // Object east 181
																// = 50 +
																// DijkstraPathPlanner.DISTANCE_TO_ROBOT
																// + 1

		if (LOGGING) {
			System.out.println("\nTest North");
		}
		testIntersectObject(new Vertex(new FieldPoint(0, 0)), new Vertex(new FieldPoint(
				0, 50)), new FieldPoint(0, 231), false, LOGGING); // Object north

		if (LOGGING) {
			System.out.println("\nTest West");
		}
		testIntersectObject(new Vertex(new FieldPoint(0, 0)), new Vertex(new FieldPoint(
				-50, 0)), new FieldPoint(-231, 0), false, LOGGING); // Object west

		if (LOGGING) {
			System.out.println("\nTest South");
		}
		testIntersectObject(new Vertex(new FieldPoint(0, 0)), new Vertex(new FieldPoint(
				0, -50)), new FieldPoint(0, -231), false, LOGGING); // Object south
	}
	
	/**
	 * Tests whether no path is returned if the source is locked in.
	 */
	@Test
	public void testLockedInSource(){
		FieldPoint destination = new FieldPoint(1500, 1500);
		TestPathPlanner planner = new TestPathPlanner();
		planner.setupLockedInSource(destination, true);
		System.out.println("\nTest Locked In Source...");
		assertNull("Test Locked In Source", planner.getRoute(new FieldPoint(0,0), destination, 0, true));
		System.out.println("Succeeded");
	}
	
	/**
	 * Tests whether no path is returns if the destination is locked in.
	 */
	@Test
	public void testLockedInDestination(){
		FieldPoint destination = new FieldPoint(1500, 1500);
		TestPathPlanner planner = new TestPathPlanner();
		planner.setupLockedInDestination(destination, true);
		System.out.println("\nTest Locked In Destination...");
		assertNull("Test Locked In Source", planner.getRoute(new FieldPoint(0,0), destination, 0, true));
		System.out.println("Succeeded");
	}

	/**
	 * Tests the function
	 * {@link DijkstraPathPlanner#intersectsObject(Vertex vertex1, Vertex vertex2)}
	 * 
	 * @param source
	 *            The startingpoint
	 * @param destination
	 *            The destinationpoint
	 * @param interceptingBox
	 *            The middle point of the box that might intercept the path
	 * @param expectedReturn
	 *            The expected return value. The function will automatically
	 *            test for this.
	 */
	public void testIntersectObject(Vertex source, Vertex destination,
			FieldPoint interceptingBox, boolean expectedReturn, boolean log) {
		Team allyTeam = World.getInstance().getReferee().getAlly();
		Robot sourceRobot = allyTeam.getRobotByID(0);
		sourceRobot.setPosition(source.getPosition());
		sourceRobot.setOnSight(true);

		Robot interceptingRobot = allyTeam.getRobotByID(1);
		interceptingRobot.setPosition(interceptingBox);
		interceptingRobot.setOnSight(true);

		generateObjectList(0); // Generate from RobotID's perspective.

		if (log) {
			for (Rectangle2D r : getObjects()) {
				System.out.println(r);
			}
		}
		Assert.assertEquals(intersectsObject(source, destination),
				expectedReturn);

		reset();
	}

	/**
	 * Sets up a few testing {@link Robots} . NOTE: All tests are centered
	 * around RobotId = 0.
	 * 
	 * @param log
	 *            if true: System.out.print the progress.
	 */
	public void setupRobotsTooClose(FieldPoint destination, boolean log) {
		Team allyTeam = World.getInstance().getReferee().getAlly();

		setupOneRobot(allyTeam, new FieldPoint(0, 0), 0, log);
		setupOneRobot(allyTeam, new FieldPoint(150, 150), 4, log);
		setupOneRobot(allyTeam, new FieldPoint(400, 400), 1, log);
		setupOneRobot(allyTeam, new FieldPoint(845, 850), 2, log);
		setupOneRobot(allyTeam, new FieldPoint(1300, 1300), 3, log);
	}
	
	/**
	 * Sets up a few testing {@link Robots} . NOTE: All tests are centered
	 * around RobotId = 0.
	 * Source will be locked in.
	 * @param log
	 *            if true: System.out.print the progress.
	 */
	public void setupLockedInSource(FieldPoint destination, boolean log) {
		Team allyTeam = World.getInstance().getReferee().getAlly();

		setupOneRobot(allyTeam, new FieldPoint(0, 0), 0, log);
		setupOneRobot(allyTeam, new FieldPoint(180, 180), 4, log);
		setupOneRobot(allyTeam, new FieldPoint(-180, -180), 1, log);
		setupOneRobot(allyTeam, new FieldPoint(180, -180), 2, log);
		setupOneRobot(allyTeam, new FieldPoint(-180, 180), 3, log);
	}
	
	/**
	 * Sets up a few testing {@link Robots} . NOTE: All tests are centered
	 * around RobotId = 0.
	 * Source will be locked in.
	 * @param log
	 *            if true: System.out.print the progress.
	 */
	public void setupLockedInDestination(FieldPoint destination, boolean log) {
		Team allyTeam = World.getInstance().getReferee().getAlly();
		
		int x = (int)destination.getX();
		int y = (int)destination.getY();
		
		setupOneRobot(allyTeam, new FieldPoint(0, 0), 0, log);
		setupOneRobot(allyTeam, new FieldPoint(x + 180, y + 180), 4, log);
		setupOneRobot(allyTeam, new FieldPoint(x-180, y-180), 1, log);
		setupOneRobot(allyTeam, new FieldPoint(x+180, y-180), 2, log);
		setupOneRobot(allyTeam, new FieldPoint(x-180, y+180), 3, log);
	}

	/**
	 * Sets up 8 random robots. 
	 * Robots may spawn on top of eachother.
	 * @param log true if you want the robots to be printed
	 */
	public void setupRandomRobots(boolean log) {
		Team allyTeam = World.getInstance().getReferee().getAlly();
		for (int i = 0; i < 8; ++i) {
			setupOneRobot(allyTeam,
					new FieldPoint((int) (Math.random() * TestPathPlannerPanel.WIDTH - TestPathPlannerPanel.WIDTH / 2),
							(int) (Math.random() * TestPathPlannerPanel.HEIGHT - TestPathPlannerPanel.HEIGHT / 2)), i,
					log);
		}
	}

	public void generateAll() {
		generateObjectList(0);
		generateVertices();
		removeCollidingVertices();
		generateNeighbours();
	}

	/**
	 * Creates one {@link Robot} for the specified {@link Team} and sets it
	 * "on sight"
	 * 
	 * @param team
	 *            The {@link Team} we want to assign this {@link Robot} to
	 * @param position
	 *            The position of the {@link Robot}
	 * @param id
	 *            The ID of the {@link Robot}
	 * @param log
	 * 			  True for logging.
	 */
	public void setupOneRobot(Team team, FieldPoint position, int id, boolean log) {
		Robot robot = team.getRobotByID(id);
		robot.setPosition(position);
		robot.setOnSight(true);
		if (log)
			System.out.println("Created new Robot with ID: " + id
					+ " and position: " + position);
	}
}
