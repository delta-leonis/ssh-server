package controller.ai.lowLevelBehavior;

import model.Robot;

public class RobotExecuter implements Runnable{

	private Robot robot;

	private LowLevelBehavior lowLevelBehavior;

	public RobotExecuter(Robot robot){
		System.out.println("New robot executer");
		this.robot = robot;
	}
	
	public void run(){
		while(true){
			if(robot.getPosition() != null)
				executeBehavior();
			try {
				Thread.sleep(50);
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
