package robocup.controller.ai.highLevelBehavior.testbehavior;

import java.util.ArrayList;

import robocup.controller.ai.highLevelBehavior.Behavior;
import robocup.controller.ai.lowLevelBehavior.GotoPosition;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.Ball;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.model.World;
import robocup.output.ComInterface;
import robocup.output.RobotCom;

public class TestBehavior extends Behavior {

	Robot robot11;
	Point target = new Point(1000, 450);


	@Override
	public void execute(ArrayList<RobotExecuter> executers) {
		Ball b = World.getInstance().getBall();
		if(b != null && b.getPosition() != null){
//			System.out.println(World.getInstance().getBall().getPosition().getX() + ":" + World.getInstance().getBall().getPosition().getY());
			target.setX(World.getInstance().getBall().getPosition().getX());
			target.setY(World.getInstance().getBall().getPosition().getY());
		}
		robot11 = World.getInstance().getAlly().getRobotByID(0xb);
		if(robot11 != null){
			if(!World.getInstance().getReferee().isStart()){
				ComInterface.getInstance(RobotCom.class).send(1, robot11.getRobotID(), 0, 0, 0, 0, 0, 0, false);
				executers.remove(findExecuter(robot11.getRobotID(), executers));
				return;
			}
			RobotExecuter executer = findExecuter(robot11.getRobotID(), executers);
			if( executer == null || !(executer.getLowLevelBehavior() instanceof GotoPosition) ){
				RobotExecuter e = new RobotExecuter(robot11);
				//e.setLowLevelBehavior(new DriveSquare(robot11, ComInterface.getInstance(RobotCom.class)));
				e.setLowLevelBehavior(new GotoPosition(robot11, ComInterface.getInstance(RobotCom.class), target));
				//e.setLowLevelBehavior(new FollowBall(robot11, ComInterface.getInstance(RobotCom.class)));
				new Thread(e).start();
				executers.add(e);
			}
			
			
			if( Math.abs(robot11.getPosition().getX() - target.getX()) < 100 && 
					Math.abs(robot11.getPosition().getY() - target.getY()) < 100){
				if(World.getInstance().getBall()!= null){
//					System.out.println(World.getInstance().getBall().getPosition().getX() + ":" + World.getInstance().getBall().getPosition().getY());
//					target.setX(World.getInstance().getBall().getPosition().getX());
//					target.setY(World.getInstance().getBall().getPosition().getY());
				} else {
					System.out.println("No ball");
					target.setX((float) ((Math.random()*2200) - 1100));
					target.setY((float) ((Math.random()*1600) - 800));
				}
				//target.setX(target.getX()*-1);
				//target.setY(target.getY()*-1);
				executers.remove(executer);
				RobotExecuter e = new RobotExecuter(robot11);
				e.setLowLevelBehavior(new GotoPosition(robot11, ComInterface.getInstance(RobotCom.class), target));
				executers.add(e);
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