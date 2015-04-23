package robocup.controller.ai.lowLevelBehavior;

import java.util.logging.Logger;

import robocup.Main;
import robocup.model.Robot;
import robocup.model.World;
import robocup.output.ComInterface;

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
			try{
				if (stop) {
					if(lowLevelBehavior != null)
						lowLevelBehavior.go.setDestination(null);
				} else if (robot.getPosition() != null)
					executeBehavior();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}catch(Exception e){
				LOGGER.severe("NULL POINTER IN ROBOTEXECUTER");
				e.printStackTrace();
				World.getInstance().stop();
				for(int i = 0; i < 11; ++i){
					ComInterface.getInstance().send(1, i,0, 0, 0, 0, false);
					try {
						Thread.sleep(10);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
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
