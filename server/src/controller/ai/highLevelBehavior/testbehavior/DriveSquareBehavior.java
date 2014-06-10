package controller.ai.highLevelBehavior.testbehavior;

import java.util.ArrayList;

import output.ComInterface;
import output.RobotCom;
import model.Point;
import model.Robot;
import model.World;
import controller.ai.highLevelBehavior.Behavior;
import controller.ai.lowLevelBehavior.GotoPosition;
import controller.ai.lowLevelBehavior.RobotExecuter;

public class DriveSquareBehavior extends Behavior {

	private final Point[] targets = new Point[4];
	private int currentPoint = 1;
	private static final int specialRobotId = 0;
	
	public DriveSquareBehavior() {
		targets[0] = new Point(1000, 500);
		targets[1] = new Point(1000, -500);
		targets[2] = new Point(-1000, -500);
		targets[3] = new Point(-1000, 500);
	}
	
	@Override
	public void execute(ArrayList<RobotExecuter> executers) {
		// TODO Auto-generated method stub

		Robot specialRobot = World.getInstance().getAlly().getRobotByID(specialRobotId);
		if(specialRobot != null)
		{
			if(!World.getInstance().getReferee().isStart()){
				ComInterface.getInstance(RobotCom.class).send(1, specialRobot.getRobotID(), 0, 0, 0, 0, 0, 0, false);
				executers.remove(findExecuter(specialRobot.getRobotID(), executers));
				return;
			}
			
			Point target = targets[currentPoint];
			
			RobotExecuter executer = findExecuter(specialRobot.getRobotID(), executers);
			if( executer == null || !(executer.getLowLevelBehavior() instanceof GotoPosition) ){
				System.out.printf("Driving to new position: %f, %f\n", target.getX(), target.getY());
				RobotExecuter e = new RobotExecuter(specialRobot);
				e.setLowLevelBehavior(new GotoPosition(specialRobot, ComInterface.getInstance(RobotCom.class), target));
				new Thread(e).start();
				executers.add(e);
			}
			

			if( Math.abs(specialRobot.getPosition().getX() - target.getX()) < 100 && 
					Math.abs(specialRobot.getPosition().getY() - target.getY()) < 100){
				currentPoint = (currentPoint + 1) % 4;
				
				executers.remove(executer);
			}
		}
	}
	
	public RobotExecuter findExecuter(int robotId, ArrayList<RobotExecuter> executers){
		for(RobotExecuter r : executers){
			if(r.getRobot().getRobotID() == robotId)
				return r;
		}
		return null;
	}

}
