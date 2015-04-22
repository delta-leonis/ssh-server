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
		System.out.println("\"Before\" setUp method");
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("\"BeforeClass\" setUpBeforeClass method");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("\"AfterClass\" tearDownAfterClass method");
	}

	@After
	public void tearDown() throws Exception {
		System.out.println("\"After\" tearDown method");
	}
	
	@Test
	public void circlesInConcave() {
		if (LOGGING) {
			System.out.println("running test \"circlesInConcave\"");
		}	
	}
	@Test
	public void circlesOutConcave() {
		if (LOGGING) {
			System.out.println("running test \"circlesOutConcave\"");
		}	
	}
	@Test
	public void circlesNextToConcave() {
		if (LOGGING) {
			System.out.println("running test \"circlesNextToConcave\"");
		}	
	}
	@Test
	public void circlesInAndOutConcave() {
		if (LOGGING) {
			System.out.println("running test \"circlesInAndOutConcave\"");
		}	
	}
	
	
	
	@Test
	public void circlesInConvex() {
		if (LOGGING) {
			System.out.println("running test \"circlesInConvex\"");
		}	
	}
	@Test
	public void circlesOutConvex() {
		if (LOGGING) {
			System.out.println("running test \"circlesOutConvex\"");
		}	
	}
	@Test
	public void circlesNextToConvex() {
		if (LOGGING) {
			System.out.println("running test \"circlesNextToConvex\"");
		}	
	}
	@Test
	public void circlesInAndOutConvex() {
		if (LOGGING) {
			System.out.println("running test \"circlesInAndOutConvex\"");
		}	
	}


	@Test
	public void lineShape() {
		if (LOGGING) {
			System.out.println("running test \"lineShape\"");
		}	
	}
	@Test
	public void pointShape() {
		if (LOGGING) {
			System.out.println("running test \"pointShape\"");
		}	
	}
	@Test
	public void nullShape() {
		if (LOGGING) {
			System.out.println("running test \"nullShape\"");
		}	
	}
}
