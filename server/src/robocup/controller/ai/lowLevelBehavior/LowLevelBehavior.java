package robocup.controller.ai.lowLevelBehavior;

import java.util.Calendar;
import java.util.logging.Logger;

import robocup.Main;
import robocup.output.ComInterface;
import robocup.controller.ai.highLevelBehavior.forcebehavior.Mode;
import robocup.controller.ai.movement.GotoPosition;
import robocup.model.FieldObject;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.model.World;

/**
 * Describes the LowLevelBahaviour each role builds upon.
 * @see {@link Blocker}
 * @see {@link Attacker}
 * @see {@link Keeper}
 * @see {@link KeeperDefender}
 */
public abstract class LowLevelBehavior {
	protected Robot robot;
	protected ComInterface output;
	protected GotoPosition go;
	private static Logger LOGGER = Logger.getLogger(Main.class.getName());
	protected Mode.roles role;

	public LowLevelBehavior(Robot robot, ComInterface output) {
		this.robot = robot;
		this.output = output;
	}

	public abstract void calculate();

	/**
	 * @see {@link Mode.roles}
	 * @return the {@link Mode.roles} assigned to this behaviour.
	 */
	public Mode.roles getRole() {
		return role;
	}

	/**
	 * Check if the robot timed out, should be used at the start of calculate in every low level behavior
	 * @return true if the robot timed out
	 */
	public boolean timeOutCheck() {
		boolean failed = robot.getLastUpdateTime() + 0.20 < Calendar.getInstance().getTimeInMillis() / 1000
				|| !World.getInstance().getReferee().isStart();

		if (failed) {
			LOGGER.warning("Robot " + robot.getRobotId() + " is not on sight");
			LOGGER.warning("Time: " + (Calendar.getInstance().getTimeInMillis() / 1000));
			LOGGER.warning("Robot: " + (robot.getLastUpdateTime()));

			robot.setOnSight(false);
			output.send(1, robot.getRobotId(), 0, 0, 0, 0, 0, 0, false); // stop
																			// moving
																			// if
																			// the
																			// robot
																			// timed
																			// out
		}
		return failed;
	}

	/**
	 * Calculate if the object is within range of the target
	 * @param object The {@link FieldObject object} we want to know whether the given {@link Point} is in the given range.
	 * @param target The {@link Point} we want to know whether it's in range of the given {@link FieldObject object}
	 * @param range The range between the {@link FieldObject object} and the {@link Point target}. TODO: Range is in millimeters?
	 * @return true if the {@link Point target} is within the given range of the {@link FieldObject object}, false otherwise.
	 */
	protected boolean isWithinRange(FieldObject object, Point target, int range) {
		int dy = (int) (target.getY() - object.getPosition().getY());
		int dx = (int) (target.getX() - object.getPosition().getX());

		return range > Math.abs(dy) && range > Math.abs(dx);
	}

	/**
	 * Calculate the position where the robot will be able to shoot
	 * Basically makes sure your {@link Robot} is half-diameter away from the ball.
	 * 
	 * @param shootDirection The direction you want your {@link Robot} to shoot the {@link Ball}. 
	 * 						 This direction is in degrees, with a value between -180 and 180. 0 being east and 90 being north.
	 * @param ballPosition 	 The position of the Ball. See {@link Point} for further documentation. 
	 * @return The position we want our {@link Robot} to be at when before we chip or kick.
	 */
	public Point getShootingPosition(int shootDirection, Point ballPosition) {
		// TODO find out why direction on robot is inverted / twisted. Problem probably lies in the code within the physical Robot.  Possible problem: Mbed:Robotcontroller#Drive() 
		int angle = -shootDirection + 270;			// Angle needs to be the inverse of the shootDirection, to position the Robot behind the ball.

		double dx = Math.sin(Math.toRadians(angle)) * (Robot.DIAMETER / 2);
		double dy = Math.cos(Math.toRadians(angle)) * (Robot.DIAMETER / 2);

		int destX = (int) (ballPosition.getX() + dx);
		int destY = (int) (ballPosition.getY() + dy);

		return new Point(destX, destY);
	}
}
