package robocup.controller.ai.movement.test;

import java.awt.geom.Rectangle2D;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import robocup.controller.ai.movement.DijkstraPathPlanner;
import robocup.model.Ally;
import robocup.model.Enemy;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.model.Team;
import robocup.model.World;
import robocup.model.enums.Color;
import sun.misc.Cleaner;

public class TestPathPlanner extends DijkstraPathPlanner{
	private static final float ROBOT_HEIGHT = 180;
	private static final boolean LOG = true;
	
	/**
	 * Initiate a test World to test the functions.
	 * This world has at least an ally team and an enemy team.
	 * Robots will be added in test functions.
	 */
	@Before
	public void initWorld(){
		World world = World.getInstance();
		Team ally = new Team("Yellow", Color.YELLOW);
		Team enemy = new Team("Japser", Color.BLUE);
		
		world.setAlly(ally);
		world.setEnemy(enemy);		
		
	}
	
	
	
	/** Simple test to see whether an object crosses a diagonal line at a 45 degree angle.*/
	@Test
	public void testIntersect1(){
		if( LOG ){ System.out.println("\nTest Intersect 1"); }

		testIntersectObject(new Vertex(new Point(0,0)),new Vertex(new Point(2000,2000)), new Point(1000,1000), true);
	}
	/** Simple test to see whether an object crosses a diagonal line at a 45 degree angle.*/
	@Test
	public void testIntersect2(){
		if( LOG ){ System.out.println("\nTest Intersect 2"); }
		
		testIntersectObject(new Vertex(new Point(100,100)),new Vertex(new Point(500,500)), new Point(300,300), true);
	}
	/** Tests whether we always stay at least 40mm away from any Robot. */
	@Test
	public void testIntersect3(){
		if( LOG ){ System.out.println("\nTest Intersect 3\nTest East"); }
		testIntersectObject(new Vertex(new Point(0,0)),new Vertex(new Point(50,0)), new Point(181,0), false);	//Object east 181 = 50 + DijkstraPathPlanner.DISTANCE_TO_ROBOT + 1
		
		if( LOG ){ System.out.println("\nTest North"); }
		testIntersectObject(new Vertex(new Point(0,0)),new Vertex(new Point(0,50)), new Point(0,181), false);	//Object north

		if( LOG ){ System.out.println("\nTest West"); }
		testIntersectObject(new Vertex(new Point(0,0)),new Vertex(new Point(-50,0)), new Point(-181,0), false);	//Object west

		if( LOG ){ System.out.println("\nTest South"); }
		testIntersectObject(new Vertex(new Point(0,0)),new Vertex(new Point(0,-50)), new Point(0,-181), false);	//Object south
	}

	
	/**
	 * Tests the function {@link ThomaPathPlanner#intersectsObject(Vertex vertex1, Vertex vertex2)}
	 * @param vertex1 The startingpoint
	 * @param vertex2 The destinationpoint
	 * @param interceptingBox The middle point of the box that might intercept the path
	 * @param expectedReturn The expected return value. The function will automatically test for this.
	 */
	public void testIntersectObject(Vertex vertex1, Vertex vertex2, Point interceptingBox, boolean expectedReturn){
		Team allyTeam = World.getInstance().getAlly();
		Robot robot = new Ally(0, true, ROBOT_HEIGHT);	//RobotID = 0
		robot.setPosition(vertex1.getPosition());
		robot = new Ally(1, true, ROBOT_HEIGHT);		//InterceptingRobotID = 1
		robot.setPosition(interceptingBox);
		allyTeam.addRobot(robot);
		
		generateObjectList(0);										//Generate from RobotID's perspective.
		
		for(Rectangle2D r : objects){
			System.out.println(r);
		}
		
		Assert.assertEquals(intersectsObject(vertex1, vertex2), expectedReturn);
		
		allyTeam.removeRobot(0);
		allyTeam.removeRobot(1);
		reset();
	}
}
