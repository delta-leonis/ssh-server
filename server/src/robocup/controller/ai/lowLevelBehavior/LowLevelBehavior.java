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
	 * Calculate the needed rotation to destination
	 * @param newPoint
	 * @return
	 */
	public int rotationToDest(Point newPoint) {
		// angle vector between old and new
		double dy = newPoint.getY() - robot.getPosition().getY();
		double dx = newPoint.getX() - robot.getPosition().getX();
		double newRad = Math.atan2(dy, dx);
		int rot = (int) (Math.toDegrees(newRad) - robot.getOrientation());
		if (rot > 180)
			rot -= 360;
		if (rot <= -180)
			rot += 360;
		return rot;
	}

	/**
	 * Returns the role assigned to this behaviour
	 * @return
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
			LOGGER.warning("Robot " + robot.getRobotID() + " is not on sight");
			LOGGER.warning("Time: " + (Calendar.getInstance().getTimeInMillis() / 1000));
			LOGGER.warning("Robot: " + (robot.getLastUpdateTime()));

			robot.setOnSight(false);
			output.send(1, robot.getRobotID(), 0, 0, 0, 0, 0, 0, false); // stop
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
	 * @param keeper
	 * @param dest
	 * @param range
	 * @return
	 */
	protected boolean isWithinRange(FieldObject object, Point target, int range) {
		int dy = (int) (target.getY() - object.getPosition().getY());
		int dx = (int) (target.getX() - object.getPosition().getX());

		return range > Math.abs(dy) && range > Math.abs(dx);
	}

	/**
	 * Calculate the position where the robot will be able to shoot
	 * @param shootDirection
	 * @param ballPosition
	 * @return
	 */
	public Point getShootingPosition(int shootDirection, Point ballPosition) {
		int angle = shootDirection + 180;

		int dx = (int) (Math.sin(Math.toRadians(angle)) * (robot.getDiameter() / 2));
		int dy = (int) (Math.cos(Math.toRadians(angle)) * (robot.getDiameter() / 2));

		int destX = (int) (ballPosition.getX() + dx);
		int destY = (int) (ballPosition.getY() + dy);

		return new Point(destX, destY);
	}
}
