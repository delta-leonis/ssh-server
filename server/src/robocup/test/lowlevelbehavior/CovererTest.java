/**
 * 
 */
package robocup.test.lowlevelbehavior;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import robocup.controller.ai.lowLevelBehavior.Coverer;
import robocup.model.Ally;
import robocup.model.FieldPoint;
import robocup.output.ComInterface;

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
		covererBehavior = new Coverer(covererRobot, ComInterface.getInstance(), 0, null, null, 0);
	}

	@Test
	public final void testCoverer() {
		setUp();
		
		objectPosition = new FieldPoint(-500, 0);
		subjectPosition = new FieldPoint(500, 0);
		covererBehavior.update(200, objectPosition, subjectPosition, 0);
		covererBehavior.calculate();
		assertEquals(new FieldPoint(300, 0), covererBehavior.getGotoPosition().getDestination());
	}
}
