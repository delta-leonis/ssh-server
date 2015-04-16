/**
 * 
 */
package robocup.test.lowlevelbehavior;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import robocup.controller.ai.lowLevelBehavior.Counter;
import robocup.model.Ally;
import robocup.model.FieldPoint;
import robocup.model.enums.FieldZone;

public class CounterTest {

	private static Ally counterRobot;
	private static FieldPoint ballPosition;
	private Counter counterBehavior;
	private static FieldPoint freePosition;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		counterRobot = new Ally(0, 180);
		counterRobot.update(new FieldPoint(0, 0), 0, 0, 0);
		ballPosition = new FieldPoint(0, 0);
		freePosition = null;
	}

	@Before
	public void setUp() {
		freePosition = null;
		counterBehavior = new Counter(counterRobot);
	}

	@Before
	public void setUpEast() {
		freePosition = null;
		counterBehavior = new Counter(counterRobot);
	}

	@Before
	public void setUpWest() {
		freePosition = null;
		counterBehavior = new Counter(counterRobot);
	}

	@Test
	public final void testCalculateWithNoFreePosition() {
		setUpEast();
		counterBehavior.calculate();
		assertEquals(FieldZone.EAST_MIDDLE.getCenterPoint(), counterBehavior.getGotoPosition().getDestination());

		setUpWest();
		counterBehavior.calculate();
		assertEquals(FieldZone.WEST_MIDDLE.getCenterPoint(), counterBehavior.getGotoPosition().getDestination());
	}

	@Test
	public final void testCalculateWithFreePosition() {
		setUpEast();
		freePosition = new FieldPoint(1000, -1100);
		counterBehavior.update(FieldZone.EAST_MIDDLE, ballPosition, freePosition);
		counterBehavior.calculate();
		assertEquals(freePosition, counterBehavior.getGotoPosition().getDestination());

		setUpWest();
		freePosition = new FieldPoint(-1200, 800);
		counterBehavior.update(FieldZone.WEST_MIDDLE, ballPosition, freePosition);
		counterBehavior.calculate();
		assertEquals(freePosition, counterBehavior.getGotoPosition().getDestination());
	}

	@Test
	public final void testNull() {
		setUp();

		// Test if counter robot throws an exception when the robot position is null
		counterRobot.setPosition(null);
		counterBehavior.update(FieldZone.WEST_MIDDLE, new FieldPoint(500, 1000), new FieldPoint(1000, 500));

		try {
			counterBehavior.calculate();
		} catch (Exception e) {
			fail();
		}

		// Test if counter robot throws an exception when the freeposition and ball position are null
		counterRobot.setPosition(new FieldPoint(400, 800));
		counterBehavior.update(FieldZone.WEST_MIDDLE, null, null);

		try {
			counterBehavior.calculate();
		} catch (Exception e) {
			fail();
		}

		// Test if counter robot throws an exception when the zone is null
		counterRobot.setPosition(new FieldPoint(400, 800));
		counterBehavior.update(null, ballPosition, freePosition);

		try {
			counterBehavior.calculate();
		} catch (Exception e) {
			fail();
		}
	}
}
