package robocup.controller.ai.movement.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import robocup.Main;
import robocup.controller.ai.movement.DijkstraPathPlanner;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.model.Team;
import robocup.model.World;

public class TestPathPlanner extends DijkstraPathPlanner {
	private static final boolean LOGGING = true;
	private static final int HEIGHT = 4000;
	private static final int WIDTH = 6000;

	private static final double RATIO = 0.10;

	private static final int X_OFFSET = (int) (WIDTH * RATIO) / 2;
	private static final int Y_OFFSET = (int) (HEIGHT * RATIO) / 2;
	
	private static final int LINE_WIDTH_PATHPLANNER = 5;

	/**
	 * Initiate a test World to test the functions. This world has at least an
	 * ally team and an enemy team. Robots will be added in test functions.
	 */
	@Before
	public void initWorld() {
		Main.initTeams();
	}

	public static void main(String args[]) {
		Main.initTeams();

		TestPathPlanner.visualTestOstacleTooClose();
//		 TestPathPlanner.visualTestDeadEnd();
//		 TestPathPlanner.visualRandomTest();
	}

	/**
	 * Shows what happens when an object is placed too close to the Source.
	 */
	public static void visualTestOstacleTooClose() {
		Point destination = new Point(1500, 1500);
		final TestPathPlanner planner = new TestPathPlanner();
		planner.setupRobotsTooClose(destination, true);
//		planner.generateAll();
		planner.printCurrentSituation(destination, true, true, planner);
	}

	/**
	 * Shows what happens when there's a dead-end vertex.
	 */
	public static void visualTestDeadEnd() {
		Point destination = new Point(1500, 1500);
		final TestPathPlanner planner = new TestPathPlanner();
		// planner.setupRobots(destination, true);
		// planner.generateAll();
		planner.setupDeadEnd(destination, true);
		planner.printCurrentSituation(destination, true, true, planner);
	}

	public static void visualRandomTest() {
		Point destination = new Point(1500, 1500);
		final TestPathPlanner planner = new TestPathPlanner();
		planner.setupRandomRobots(destination, true);
//		planner.generateAll();
		planner.printCurrentSituation(destination, true, true, planner);
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

		testIntersectObject(new Vertex(new Point(0, 0)), new Vertex(new Point(
				2000, 2000)), new Point(1000, 1000), true, LOGGING);
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

		testIntersectObject(new Vertex(new Point(100, 100)), new Vertex(
				new Point(500, 500)), new Point(300, 300), true, LOGGING);
	}

	/** Tests whether we always stay at least 40mm away from any Robot. */
	@Test
	public void testIntersect3() {
		if (LOGGING) {
			System.out.println("\nTest Intersect 3\nTest East");
		}
		testIntersectObject(new Vertex(new Point(0, 0)), new Vertex(new Point(
				50, 0)), new Point(181, 0), false, LOGGING); // Object east 181
																// = 50 +
																// DijkstraPathPlanner.DISTANCE_TO_ROBOT
																// + 1

		if (LOGGING) {
			System.out.println("\nTest North");
		}
		testIntersectObject(new Vertex(new Point(0, 0)), new Vertex(new Point(
				0, 50)), new Point(0, 181), false, LOGGING); // Object north

		if (LOGGING) {
			System.out.println("\nTest West");
		}
		testIntersectObject(new Vertex(new Point(0, 0)), new Vertex(new Point(
				-50, 0)), new Point(-181, 0), false, LOGGING); // Object west

		if (LOGGING) {
			System.out.println("\nTest South");
		}
		testIntersectObject(new Vertex(new Point(0, 0)), new Vertex(new Point(
				0, -50)), new Point(0, -181), false, LOGGING); // Object south
	}

	/**
	 * Tests the function
	 * {@link ThomaPathPlanner#intersectsObject(Vertex vertex1, Vertex vertex2)}
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
			Point interceptingBox, boolean expectedReturn, boolean log) {
		Team allyTeam = World.getInstance().getAlly();
		Robot sourceRobot = allyTeam.getRobotByID(0);
		sourceRobot.setPosition(source.getPosition());
		sourceRobot.setOnSight(true);

		Robot interceptingRobot = allyTeam.getRobotByID(1);
		interceptingRobot.setPosition(interceptingBox);
		interceptingRobot.setOnSight(true);

		generateObjectList(0); // Generate from RobotID's perspective.

		if (log) {
			for (Rectangle2D r : objects) {
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
	public void setupRobotsTooClose(Point destination, boolean log) {
		Team allyTeam = World.getInstance().getAlly();

		setupOneRobot(allyTeam, new Point(0, 0), 0, log);
		setupOneRobot(allyTeam, new Point(150, 150), 4, log);
		setupOneRobot(allyTeam, new Point(400, 400), 1, log);
		setupOneRobot(allyTeam, new Point(845, 850), 2, log);
		setupOneRobot(allyTeam, new Point(1300, 1300), 3, log);

		// setupOneRobot(allyTeam, new Point(-800,-245), 4, log);
		// setupOneRobot(allyTeam, new Point(854,-695), 5, log);
		// setupOneRobot(allyTeam, new Point(-900,-1594), 6, log);

//		Vertex source = new Vertex(World.getInstance().getAlly()
//				.getRobotByID(0).getPosition());
//		source.setDist(0);
//		vertices.add(source);
//		Vertex dest = new Vertex(destination);
//		vertices.add(dest);
	}

	public void setupRandomRobots(Point destination, boolean log) {
		Team allyTeam = World.getInstance().getAlly();
		for (int i = 0; i < 8; ++i) {
			setupOneRobot(allyTeam,
					new Point((int) (Math.random() * WIDTH - WIDTH / 2),
							(int) (Math.random() * HEIGHT - HEIGHT / 2)), i,
					log);
		}
//		Vertex source = new Vertex(World.getInstance().getAlly()
//				.getRobotByID(0).getPosition());
//		source.setDist(0);
//		vertices.add(source);
//		Vertex dest = new Vertex(destination);
//		vertices.add(dest);
	}

	/**
	 * Sets up a bunch of vertices which includes a dead-end
	 * 
	 * @param log
	 */
	public void setupDeadEnd(Point destination, boolean log) {
		Team allyTeam = World.getInstance().getAlly();

		setupOneRobot(allyTeam, new Point(0, 0), 0, log);
		// setupOneRobot(allyTeam, new Point(550,500), 1, log);

		Vertex source = new Vertex(World.getInstance().getAlly()
				.getRobotByID(0).getPosition());
		source.setDist(0);
		vertices.add(source);
		Vertex dest = new Vertex(destination);
		vertices.add(dest);

		Vertex vertex1 = new Vertex(new Point(500, 400));
		Vertex vertex2 = new Vertex(new Point(900, 800));
		Vertex vertex3 = new Vertex(new Point(-800, 800));
		Vertex vertex4 = new Vertex(new Point(-1600, 800));

		vertices.add(vertex1);
		vertices.add(vertex2);
		vertices.add(vertex3);
		vertices.add(vertex4);

		vertex1.addNeighbour(vertex2);
		vertex2.addNeighbour(vertex1);
		vertex2.addNeighbour(vertex3);
		vertex3.addNeighbour(vertex4);
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
	public void setupOneRobot(Team team, Point position, int id, boolean log) {
		Robot robot = team.getRobotByID(id);
		robot.setPosition(position);
		robot.setOnSight(true);
		if (log)
			System.out.println("Created new Robot with ID: " + id
					+ " and position: " + position);
	}

	/**
	 * Gives a visual representation of the objects. Helps the developer when
	 * figuring out what dummy data to use.
	 */
	public void printCurrentSituation(final Point destination,
			final boolean drawNeighbours, final boolean drawPath,
			final TestPathPlanner planner) {
		JFrame frame = new JFrame();
		
		final LinkedList<Point> path =  planner.getRoute(World.getInstance()
				.getAlly().getRobotByID(0).getPosition(), destination, 0, true);
		
		JPanel panel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;

				// Draw background
				g.setColor(Color.GREEN);
				g.fillRect(0, 0, 600, 400);
				// Draw obstacles
				g.setColor(Color.BLACK);

				g.drawLine(0, Y_OFFSET, (int) (TestPathPlanner.WIDTH * RATIO),
						Y_OFFSET);
				g.drawLine(X_OFFSET, 0, X_OFFSET,
						(int) (TestPathPlanner.HEIGHT * RATIO));
				g.drawRect(0, 0, 600, 400);

				g.setColor(Color.WHITE);
				g.drawRect(0, Y_OFFSET - 25, 10, 50);
				g.drawRect((int) (TestPathPlanner.WIDTH * RATIO) - 10,
						Y_OFFSET - 25, 10, 50);

				for (Vertex vertex : planner.testVertices) {
					g.setColor(Color.MAGENTA);

					int x = (int) (X_OFFSET + vertex.getPosition().getX()
							* RATIO);
					int y = (int) (Y_OFFSET - vertex.getPosition().getY()
							* RATIO);
					if(!vertex.isRemovable()){
						System.out.println("Test!");
						g.drawString("nRmvbl", x, y);
					}
					g.drawOval(x - 5, y - 5, 10, 10);

					if (drawNeighbours) {
						g.setColor(new Color((int) (Math.random() * 255),
								(int) (Math.random() * 255), (int) (Math
										.random() * 255)));
						for (Vertex neighbour : vertex.getNeighbours()) {
							int x2 = (int) (X_OFFSET + neighbour.getPosition()
									.getX() * RATIO);
							int y2 = (int) (Y_OFFSET - neighbour.getPosition()
									.getY() * RATIO);
							g.drawLine(x, y, x2, y2);
						}
					}
				}
				
				g.setColor(Color.BLACK);

				for (Rectangle2D rect : planner.objects) {
					drawRobot(
							g,
							(int) rect.getCenterX(),
							(int) rect.getCenterY(),
							90,
							DISTANCE_TO_ROBOT,
							VERTEX_DISTANCE_TO_ROBOT,
							"[" + (int) rect.getCenterX() + ","
									+ (int) rect.getCenterY() + "]");
				}
				// Source
				g.setColor(Color.RED);
				Robot source = World.getInstance().getAlly().getRobotByID(0);
				drawRobot(g, (int) source.getPosition().getX(), (int) source
						.getPosition().getY(), 90, DISTANCE_TO_ROBOT,
						VERTEX_DISTANCE_TO_ROBOT, "id=" + source.getRobotId());
				// Destination
				g.setColor(Color.BLUE);
				drawRobot(g, (int) destination.getX(),
						(int) destination.getY(), 90, DISTANCE_TO_ROBOT,
						VERTEX_DISTANCE_TO_ROBOT, "dest");
				

				// Draw path
				if (drawPath) {
					// planner.reset();
					// planner.setupRobots(destination , true);
					drawPath(g, g2, path, source.getPosition(), destination);
				}

			}
		};

		panel.setSize((int) (WIDTH * RATIO), (int) (HEIGHT * RATIO));
		frame.setContentPane(panel);
		frame.setSize((int) (WIDTH * RATIO) + 100, (int) (HEIGHT * RATIO) + 100);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setVisible(true);
	}

	public final static void drawRobot(Graphics g, int x, int y, int radius,
			int dangerZone, int vertexDist, String name) {
		int realX = (int) (X_OFFSET + x * RATIO);
		int realY = (int) (Y_OFFSET - y * RATIO);
		// Robot
		g.fillOval(realX - (int) (radius * RATIO), realY
				- (int) (radius * RATIO), (int) (radius * 2 * RATIO),
				(int) (radius * 2 * RATIO));
		// DangerZone
		g.drawRect(realX - (int) (dangerZone * RATIO), realY
				- (int) (dangerZone * RATIO), (int) (dangerZone * 2 * RATIO),
				(int) (dangerZone * 2 * RATIO));
		// Vertice square.
		// g.drawRect(realX - (int)(vertexDist * RATIO), realY -
		// (int)(vertexDist * RATIO),
		// (int)(vertexDist * 2 * RATIO), (int)(vertexDist * 2 * RATIO));

		g.drawString(name, realX + 10, realY + 10);
	}

	public final static void drawPath(Graphics g, Graphics2D g2,
			LinkedList<Point> path, Point start, Point destination) {
		g.setColor(Color.ORANGE);
		g2.setStroke(new BasicStroke(LINE_WIDTH_PATHPLANNER));
		Point previous = start;
		for (Point p : path) {
			int x = (int) (X_OFFSET + p.getX() * RATIO);
			int y = (int) (Y_OFFSET - p.getY() * RATIO);
			int x2 = (int) (X_OFFSET + previous.getX() * RATIO);
			int y2 = (int) (Y_OFFSET - previous.getY() * RATIO);
			g.drawLine(x, y, x2, y2);
			g2.draw(new Line2D.Float(x, y, x2, y2));
			previous = p;
		}
		// Draw point to destination
		int x = (int) (X_OFFSET + previous.getX() * RATIO);
		int y = (int) (Y_OFFSET - previous.getY() * RATIO);
		int x2 = (int) (X_OFFSET + destination.getX() * RATIO);
		int y2 = (int) (Y_OFFSET - destination.getY() * RATIO);
		g.drawLine(x, y, x2, y2);
		g2.draw(new Line2D.Float(x, y, x2, y2));
	}

}
