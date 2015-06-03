/**
 * 
 */
package robocup.test.lowlevelbehavior;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import robocup.controller.ai.lowLevelBehavior.Disturber;
import robocup.model.Ally;
import robocup.model.FieldPoint;

public class DisturberTest {

	private Ally disturberRobot;
	private FieldPoint objectPosition;
	private Disturber disturberBehavior;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@Before
	public void setUp() {
		disturberRobot = new Ally(0, 180);
		disturberRobot.update(new FieldPoint(0, 0), 0, 0, 0, 0);
		objectPosition = new FieldPoint(0, 0);
	}

	@Before
	public void setUpNull() {
		disturberRobot = new Ally(0, 180);
		disturberRobot.update(new FieldPoint(0, 0), 0, 0, 0, 0);
		objectPosition = new FieldPoint(0, 0);
		disturberBehavior = new Disturber(disturberRobot, new FieldPoint(3000, 0));
	}

	@Before
	public void setUpEastSingleSize() {
		setUp();
		disturberBehavior = new Disturber(disturberRobot, new FieldPoint(3000, 0));
		disturberBehavior.update(200, false, objectPosition);
	}

	@Before
	public void setUpWestSingleSize() {
		setUp();
		disturberBehavior = new Disturber(disturberRobot, new FieldPoint(-3000, 0));
		disturberBehavior.update(200, false, objectPosition);
	}

	@Before
	public void setUpEastDoubleSize() {
		setUp();
		disturberBehavior = new Disturber(disturberRobot, new FieldPoint(4500, 0));
		disturberBehavior.update(200, false, objectPosition);
	}

	@Before
	public void setUpWestDoubleSize() {
		setUp();
		disturberBehavior = new Disturber(disturberRobot, new FieldPoint(-4500, 0));
		disturberBehavior.update(200, false, objectPosition);
	}

	@Test
	public final void testDisturberSingleSize() {
		setUpEastSingleSize();
		disturberBehavior.calculate();
		assertTrue(new FieldPoint(200, 0).equalsRounded(disturberBehavior.getGotoPosition().getDestination()));
		assertTrue(200.0 == Math.round(objectPosition.getDeltaDistance(disturberBehavior.getGotoPosition()
				.getDestination())));

		objectPosition = new FieldPoint(3000, 1000);
		disturberBehavior.update(200, false, objectPosition);
		disturberBehavior.calculate();
		assertTrue(new FieldPoint(3000, 800).equalsRounded(disturberBehavior.getGotoPosition().getDestination()));
		assertTrue(200.0 == Math.round(objectPosition.getDeltaDistance(disturberBehavior.getGotoPosition()
				.getDestination())));

		objectPosition = new FieldPoint(3000, -1000);
		disturberBehavior.update(200, false, objectPosition);
		disturberBehavior.calculate();
		assertTrue(new FieldPoint(3000, -800).equalsRounded(disturberBehavior.getGotoPosition().getDestination()));
		assertTrue(200.0 == Math.round(objectPosition.getDeltaDistance(disturberBehavior.getGotoPosition()
				.getDestination())));

		setUpWestSingleSize();
		disturberBehavior.calculate();
		assertTrue(new FieldPoint(-200, 0).equalsRounded(disturberBehavior.getGotoPosition().getDestination()));
		assertTrue(200.0 == Math.round(objectPosition.getDeltaDistance(disturberBehavior.getGotoPosition()
				.getDestination())));

		objectPosition = new FieldPoint(-3000, 1000);
		disturberBehavior.update(200, false, objectPosition);
		disturberBehavior.calculate();
		assertTrue(new FieldPoint(-3000, 800).equalsRounded(disturberBehavior.getGotoPosition().getDestination()));
		assertTrue(200.0 == Math.round(objectPosition.getDeltaDistance(disturberBehavior.getGotoPosition()
				.getDestination())));

		objectPosition = new FieldPoint(-3000, -1000);
		disturberBehavior.update(200, false, objectPosition);
		disturberBehavior.calculate();
		assertTrue(new FieldPoint(-3000, -800).equalsRounded(disturberBehavior.getGotoPosition().getDestination()));
		assertTrue(200.0 == Math.round(objectPosition.getDeltaDistance(disturberBehavior.getGotoPosition()
				.getDestination())));
	}

	@Test
	public final void testDisturberDoubleSize() {
		setUpEastDoubleSize();
		disturberBehavior.calculate();
		assertTrue(new FieldPoint(200, 0).equalsRounded(disturberBehavior.getGotoPosition().getDestination()));
		assertTrue(200.0 == Math.round(objectPosition.getDeltaDistance(disturberBehavior.getGotoPosition()
				.getDestination())));

		objectPosition = new FieldPoint(4500, 1000);
		disturberBehavior.update(200, false, objectPosition);
		disturberBehavior.calculate();
		assertTrue(new FieldPoint(4500, 800).equalsRounded(disturberBehavior.getGotoPosition().getDestination()));
		assertTrue(200.0 == Math.round(objectPosition.getDeltaDistance(disturberBehavior.getGotoPosition()
				.getDestination())));

		objectPosition = new FieldPoint(4500, -1000);
		disturberBehavior.update(200, false, objectPosition);
		disturberBehavior.calculate();
		assertTrue(new FieldPoint(4500, -800).equalsRounded(disturberBehavior.getGotoPosition().getDestination()));
		assertTrue(200.0 == Math.round(objectPosition.getDeltaDistance(disturberBehavior.getGotoPosition()
				.getDestination())));

		setUpWestDoubleSize();
		disturberBehavior.calculate();
		assertTrue(new FieldPoint(-200, 0).equalsRounded(disturberBehavior.getGotoPosition().getDestination()));
		assertTrue(200.0 == Math.round(objectPosition.getDeltaDistance(disturberBehavior.getGotoPosition()
				.getDestination())));

		objectPosition = new FieldPoint(-4500, 1000);
		disturberBehavior.update(200, false, objectPosition);
		disturberBehavior.calculate();
		assertTrue(new FieldPoint(-4500, 800).equalsRounded(disturberBehavior.getGotoPosition().getDestination()));
		assertTrue(200.0 == Math.round(objectPosition.getDeltaDistance(disturberBehavior.getGotoPosition()
				.getDestination())));

		objectPosition = new FieldPoint(-4500, -1000);
		disturberBehavior.update(200, false, objectPosition);
		disturberBehavior.calculate();
		assertTrue(new FieldPoint(-4500, -800).equalsRounded(disturberBehavior.getGotoPosition().getDestination()));
		assertTrue(200.0 == Math.round(objectPosition.getDeltaDistance(disturberBehavior.getGotoPosition()
				.getDestination())));
	}

	public final void testNull() {
		setUpNull();
		disturberBehavior.update(0, false, objectPosition);

		try {
			disturberBehavior.calculate();
		} catch (Exception e) {
			fail();
		}

		disturberRobot.setPosition(null);

		try {
			disturberBehavior.calculate();
		} catch (Exception e) {
			fail();
		}

		disturberRobot.setPosition(new FieldPoint(0, 0));
		disturberBehavior.update(0, false, null);

		try {
			disturberBehavior.calculate();
		} catch (Exception e) {
			fail();
		}
	}
}
