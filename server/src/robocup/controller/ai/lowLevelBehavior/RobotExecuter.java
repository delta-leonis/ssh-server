package robocup.controller.ai.lowLevelBehavior;

import java.util.logging.Logger;

import robocup.Main;
import robocup.model.Robot;

public class RobotExecuter implements Runnable {

	private Robot robot;
	private static Logger LOGGER = Logger.getLogger(Main.class.getName());

	private LowLevelBehavior lowLevelBehavior;
	private boolean stop;

	public RobotExecuter(Robot robot) {
		LOGGER.info("new robot executer");
		this.robot = robot;
	}

	public void run() {
		while (true) {
			if (stop) {
				lowLevelBehavior.go.setDestination(null);
			} else if (robot.getPosition() != null)
				executeBehavior();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void executeBehavior() {
		if (lowLevelBehavior == null) {
			return;
		}
		lowLevelBehavior.calculate();
	}

	/**
	 * Sets the {@link LowLevelBehavior} for this class.
	 * @param lowLevelBehavior A {@link LowLevelBehaviour} class that tells the physical Robot what to do.
	 */
	public void setLowLevelBehavior(LowLevelBehavior lowLevelBehavior) {
		this.lowLevelBehavior = lowLevelBehavior;
	}

	/**
	 * @return the {@link LowLevelBehavior} assigned to this class.
	 */
	public LowLevelBehavior getLowLevelBehavior() {
		return lowLevelBehavior;
	}

	/**
	 * @return the {@link Robot} this class represents in the {@link robocup.model.World model}
	 */
	public Robot getRobot() {
		return robot;
	}

	/**
	 * Stops the Robot.
	 * @param stop true if you want to stop, false otherwise.
	 */
	public void stop(boolean stop) {
		this.stop = stop;
	}
}
