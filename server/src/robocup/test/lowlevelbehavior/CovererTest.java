/**
 * 
 */
package robocup.test.lowlevelbehavior;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import robocup.controller.ai.lowLevelBehavior.Coverer;
import robocup.model.Ally;
import robocup.model.FieldPoint;

public class CovererTest {

	private static Ally covererRobot;
	private Coverer covererBehavior;
	private static FieldPoint objectPosition;
	private static FieldPoint subjectPosition;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		covererRobot = new Ally(0, 180);
		covererRobot.update(new FieldPoint(0, 0), 0, 0, 0);
		objectPosition = null;
		subjectPosition = null;
	}

	@Before
	public void setUp() {
		covererBehavior = new Coverer(covererRobot, 0, null, null, 0);
	}

	@Test
	public final void testCoverer() {
		setUp();

		// Test if coverer robot finds a cover position in the correct direction when covering towards WEST
		objectPosition = new FieldPoint(-500, 0);
		subjectPosition = new FieldPoint(500, 0);
		covererBehavior.update(200, objectPosition, subjectPosition, 0);
		covererBehavior.calculate();
		assertTrue(new FieldPoint(300.0, 0.0).equalsRounded(covererBehavior.getGotoPosition().getDestination()));

		// Test if coverer robot finds a cover position in the correct direction when covering towards EAST
		objectPosition = new FieldPoint(500, 0);
		subjectPosition = new FieldPoint(-500, 0);
		covererBehavior.update(200, objectPosition, subjectPosition, 0);
		covererBehavior.calculate();
		assertTrue(new FieldPoint(-300.0, 0.0).equalsRounded(covererBehavior.getGotoPosition().getDestination()));

		// Test if coverer robot finds a cover position in the correct direction when covering towards NORTH
		objectPosition = new FieldPoint(0, 500);
		subjectPosition = new FieldPoint(0, -500);
		covererBehavior.update(200, objectPosition, subjectPosition, 0);
		covererBehavior.calculate();
		assertTrue(new FieldPoint(0.0, -300.0).equalsRounded(covererBehavior.getGotoPosition().getDestination()));

		// Test if coverer robot finds a cover position in the correct direction when covering towards SOUTH
		objectPosition = new FieldPoint(0, -500);
		subjectPosition = new FieldPoint(0, 500);
		covererBehavior.update(200, objectPosition, subjectPosition, 0);
		covererBehavior.calculate();
		assertTrue(new FieldPoint(0.0, 300.0).equalsRounded(covererBehavior.getGotoPosition().getDestination()));
	}

	@Test
	public final void testNull() {

	}
}
