package robocup.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.awt.geom.Line2D;

import javax.swing.JFrame;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import robocup.Main;
import robocup.model.FieldPoint;
import robocup.model.World;
import robocup.model.enums.FieldZone;

public class TestWorld {
	private static final boolean LOGGING = true;

	
	/**
	 * Run as application to get a visual representation of the path planner.
	 */
	public static void main(String args[]) {
		new TestWorld();
		// test with a combination of
		// test if object(s) in triangle is found
		// test if object are found just outside the polygon
		// test if object(s) outside of triangle isn't found (all sides?)

		// all to be tested with multiple
		// test concave
		// test convex
		// test wrong shape (line, point)
		// null points
		
		// Not to  be tested
		// reentrant polygons, will not be usefull.
	}
	
	@Before
	public void setUp() throws Exception {
		Main.initTeams();
		System.out.println("Hooooi");
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testMethod() {
		if (LOGGING) {
			System.out.println("\nTest Intersect 3\nTest East");
		}	
	}
	


}
