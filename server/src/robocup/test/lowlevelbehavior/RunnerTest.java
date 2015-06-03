/**
 * 
 */
package robocup.test.lowlevelbehavior;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import robocup.controller.ai.lowLevelBehavior.Runner;
import robocup.model.Ally;
import robocup.model.FieldPoint;

public class RunnerTest {

	private static Ally runnerRobot;
	private FieldPoint ballPosition;
	private Runner runnerBehavior;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@Before
	public void setUp() {
		runnerRobot = new Ally(0, 180);
		runnerRobot.update(new FieldPoint(0, 0), 0, 0, 0, 0);
		ballPosition = new FieldPoint(-100, 100);
		runnerBehavior = new Runner(runnerRobot);
	}

	@Test
	public final void testRunner() {
		setUp();

		// Test if Runner sets his destination to the free position and target to the ball position
		FieldPoint freePosition = new FieldPoint(500, -500);
		runnerBehavior.update(ballPosition, freePosition);
		runnerBehavior.calculate();
		assertEquals(freePosition, runnerBehavior.getGotoPosition().getDestination());
		assertEquals(ballPosition, runnerBehavior.getGotoPosition().getTarget());

		freePosition = new FieldPoint(-2000, -500);
		ballPosition = new FieldPoint(100, 100);
		runnerBehavior.update(ballPosition, freePosition);
		runnerBehavior.calculate();
		assertEquals(freePosition, runnerBehavior.getGotoPosition().getDestination());
		assertEquals(ballPosition, runnerBehavior.getGotoPosition().getTarget());
	}

	@Test
	public final void testNull() {
		setUp();

		// Test if runner robot throws an exception when the robot position, ball position and free position are null
		runnerRobot.setPosition(null);
		runnerBehavior.update(null, null);

		try {
			runnerBehavior.calculate();
		} catch (Exception e) {
			fail();
		}

		// Test if runner robot throws an exception when the ball position and free position are null
		runnerRobot.setPosition(new FieldPoint(100, 500));
		runnerBehavior.update(null, null);

		try {
			runnerBehavior.calculate();
		} catch (Exception e) {
			fail();
		}

		// Test if runner robot throws an exception when the free position is null
		runnerRobot.setPosition(new FieldPoint(100, 500));
		runnerBehavior.update(new FieldPoint(500, 500), null);

		try {
			runnerBehavior.calculate();
		} catch (Exception e) {
			fail();
		}

		// Test if runner robot throws an exception when the ball position is null
		runnerRobot.setPosition(new FieldPoint(100, 500));
		runnerBehavior.update(null, new FieldPoint(500, 500));

		try {
			runnerBehavior.calculate();
		} catch (Exception e) {
			fail();
		}
	}
}
