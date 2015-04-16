/**
 * 
 */
package robocup.test.lowlevelbehavior;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import robocup.controller.ai.lowLevelBehavior.Attacker;
import robocup.model.Ally;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.output.ComInterface;

public class AttackerTest {

	private static Ally attackRobot;
	private Attacker attackBehavior;
	private FieldPoint ballPosition;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		attackRobot = new Ally(0, 180);
		attackRobot.update(new FieldPoint(0, 0), 0, 0, 0);
	}

	@Before
	public void setUp() {
		ballPosition = new FieldPoint(100, 100);
		attackBehavior = new Attacker(attackRobot, ComInterface.getInstance(), 0.0, 0, ballPosition);
	}

	@Test
	public final void testDestination() {
		setUp();
		
		// Test if robot finds a shooting position in the correct direction when shooting towards EAST
		attackRobot.update(new FieldPoint(0, 0), 0, 0, 0);
		attackBehavior.update(0.0, 0, ballPosition);
		attackBehavior.calculate();
		assertEquals(attackBehavior.getGotoPosition().getDestination(), new FieldPoint(ballPosition.getX()
				- Robot.DIAMETER / 2, ballPosition.getY()));

		// Test if robot finds a shooting position in the correct direction when shooting towards NORTH
		attackBehavior.update(90.0, 0, ballPosition);
		attackBehavior.calculate();
		assertEquals(attackBehavior.getGotoPosition().getDestination(), new FieldPoint(ballPosition.getX(),
				ballPosition.getY() - Robot.DIAMETER / 2));

		// Test if robot finds a shooting position in the correct direction when shooting towards WEST
		attackBehavior.update(180.0, 0, ballPosition);
		attackBehavior.calculate();
		assertEquals(attackBehavior.getGotoPosition().getDestination(), new FieldPoint(ballPosition.getX()
				+ Robot.DIAMETER / 2, ballPosition.getY()));

		// Test if robot finds a shooting position in the correct direction when shooting towards SOUTH
		attackBehavior.update(-90.0, 0, ballPosition);
		attackBehavior.calculate();
		assertEquals(attackBehavior.getGotoPosition().getDestination(), new FieldPoint(ballPosition.getX(),
				ballPosition.getY() + Robot.DIAMETER / 2));
	}

	@Test
	public final void testNull() {
		setUp();

		// Test if attacker robot throws an exception when the robot position is null
		attackRobot.setPosition(null);
		attackBehavior.update(0.0, 100, ballPosition);

		try {
			attackBehavior.calculate();
		} catch (Exception e) {
			fail();
		}

		// Test if attacker robot throws an exception when the ball position is null and chipKick strength > 0
		attackRobot.setPosition(new FieldPoint(1234, 4321));
		attackBehavior.update(0.0, 100, null);

		try {
			attackBehavior.calculate();
		} catch (Exception e) {
			fail();
		}

		// Test if attacker robot throws an exception when the ball position is null and chipKick strength is 0
		attackRobot.setPosition(new FieldPoint(1234, 4321));
		attackBehavior.update(0.0, 0, null);

		try {
			attackBehavior.calculate();
		} catch (Exception e) {
			fail();
		}
	}
}
