/**
 * 
 */
package robocup.test;

import static org.junit.Assert.*;

import java.awt.geom.Line2D;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import robocup.model.FieldPoint;
import robocup.model.enums.FieldZone;

/**
 * Tests {@link FieldZone}.
 */
public class FieldZoneTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link FieldZone#contains(FieldPoint)}.
	 * Assumed is that {@link FieldZone#EAST} has at least 4 vertices.<br><br>
	 * To test, there are 2 lines drawn between the opposite corners. If they intersect, the center of 
	 * each of the lines should be inside the {@link FieldZone} polygon.
	 */
	@Test
	public void testContains() {
		FieldPoint[] vertices = FieldZone.EAST.getVertices();
		Line2D.Double line1 = new Line2D.Double(vertices[0].getX(), vertices[2].getX(), vertices[0].getY(), vertices[2].getY());
		Line2D.Double line2 = new Line2D.Double(vertices[1].getX(), vertices[3].getX(), vertices[1].getY(), vertices[3].getY());
		if (line1.intersectsLine(line2))
			assertEquals(true, FieldZone.EAST.contains(new FieldPoint((vertices[0].getX()+vertices[2].getX())/2, (vertices[0].getY()+vertices[2].getY())/2)));
		else
			fail("Zone has a idiotic shape!");
	}
}
