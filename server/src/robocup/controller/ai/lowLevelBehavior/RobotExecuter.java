package robocup.controller.ai.lowLevelBehavior;

import java.util.logging.Logger;

import robocup.Main;
import robocup.model.Robot;

public class RobotExecuter implements Runnable{

	private Robot robot;
	private static Logger LOGGER = Logger.getLogger(Main.class.getName());
	
	private LowLevelBehavior lowLevelBehavior;

	public RobotExecuter(Robot robot){
		LOGGER.info("new robot executer");
		this.robot = robot;
	}
	
	public void run(){
		while(true){
			if(robot.getPosition() != null)
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
		if(lowLevelBehavior == null){
			return;
		}
		lowLevelBehavior.calculate();
	}

	public void setLowLevelBehavior(LowLevelBehavior lowLevelBehavior) {
		this.lowLevelBehavior = lowLevelBehavior;
	}
	
	public LowLevelBehavior getLowLevelBehavior(){
		return lowLevelBehavior;
	}

	public Robot getRobot(){
		return robot;
	}
}
