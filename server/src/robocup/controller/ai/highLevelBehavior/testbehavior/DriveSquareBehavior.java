package robocup.controller.ai.highLevelBehavior.testbehavior;

import java.util.ArrayList;

import robocup.controller.ai.highLevelBehavior.Behavior;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.controller.ai.movement.GotoPosition;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.model.World;
import robocup.output.ComInterface;
import robocup.output.RobotCom;

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
//			if( executer == null || !(executer.getLowLevelBehavior() instanceof GotoPosition) ){
//				System.out.printf("Driving to new position: %f, %f\n", target.getX(), target.getY());
//				RobotExecuter e = new RobotExecuter(specialRobot);
//				e.setLowLevelBehavior(new GotoPosition(specialRobot, ComInterface.getInstance(RobotCom.class), target));
//				new Thread(e).start();
//				executers.add(e);
//			}
			

			if( Math.abs(specialRobot.getPosition().getX() - target.getX()) < 100 && 
					Math.abs(specialRobot.getPosition().getY() - target.getY()) < 100){
				currentPoint = (currentPoint + 1) % 4;
				
				executers.remove(executer);
			}
		}
	}

	@Override
	public void updateExecuters(ArrayList<RobotExecuter> executers) {
		// TODO Auto-generated method stub
		
	}
}
