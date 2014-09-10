package robocup.controller.ai.lowLevelBehavior;

import java.util.Calendar;
import java.util.logging.Logger;

import robocup.Main;
import robocup.output.ComInterface;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.model.World;

public abstract class LowLevelBehavior {
	protected World world;
	protected Robot robot;
	protected ComInterface output;
	private static Logger LOGGER = Logger.getLogger(Main.class.getName());
	
	public LowLevelBehavior(Robot robot, ComInterface output){
		this.world = World.getInstance();
		this.robot = robot;
		this.output = output;
	}
	public abstract void calculate();
	
	/**
	 * Calculate the needed rotation to destination
	 * @param newPoint
	 * @return
	 */
	public int rotationToDest(Point newPoint){
		//angle vector between old and new
		double dy = newPoint.getY() - robot.getPosition().getY();
		double dx = newPoint.getX() - robot.getPosition().getX();
		double newRad = Math.atan2(dy, dx);
		int rot = (int)(Math.toDegrees(newRad) - robot.getOrientation());
		if( rot > 180 )
			rot -= 360;
		if (rot <= -180 )
			rot += 360;
		return  rot;
	}
	
	/**
	 * Check if the robot timed out, should be used at the start of calculate in every low level behavior
	 * @return true if the robot timed out
	 */
	public boolean timeOutCheck() {
		boolean failed = robot.getLastUpdateTime() + 0.20 < Calendar.getInstance().getTimeInMillis()/1000 ||
				!World.getInstance().getReferee().isStart();

		LOGGER.warning("Robot " + robot.getRobotID() + " is not on sight");
		LOGGER.warning("Time: " + (Calendar.getInstance().getTimeInMillis()/1000));
		LOGGER.warning("Robot: " + (robot.getLastUpdateTime()));
		
		if(failed) {
			robot.setOnSight(false);
			output.send(1, robot.getRobotID(), 0, 0, 0, 0, 0, 0, false);  // stop moving if the robot timed out
		}
		return failed;
	}
}