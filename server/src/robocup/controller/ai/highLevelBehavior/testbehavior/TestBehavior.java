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

	ArrayList<Robot> robots = new ArrayList<>();

	@Override
	public void execute(ArrayList<RobotExecuter> executers) {
		World w = World.getInstance();
		robots = w.getAlly().getRobots();
		Robot closest = getClosestToTarget(robots, World.getInstance().getBall().getPosition());
		for( Robot r  : robots){
			try {
				RobotExecuter executer = findExecuter(r.getRobotID(), executers);
				if( executer == null ) {
					executer = new RobotExecuter(r);
					executer.setLowLevelBehavior(new GotoPosition(r, ComInterface.getInstance(RobotCom.class), w.getBall().getPosition()));
					new Thread(executer).start();
					executers.add(executer);
				}
				if(w.getBall().getPosition()!= null && r.getPosition().getDeltaDistance(w.getBall().getPosition() )> 700)
					goToBall(r, executer);
				else if(r.equals(closest)){
					goToBall(r, executer);
				} else {
					((GotoPosition) executer.getLowLevelBehavior()).setTarget(null);
				}
			}catch(NullPointerException e){
				e.printStackTrace();
			}
		}
	}
	
	private void goToBall(Robot r, RobotExecuter e){
		
		Ball b = World.getInstance().getBall();
		
		System.out.println(" Bal pos: " +  b.getPosition());
	
		GotoPosition go = null;
		if(e.getLowLevelBehavior() instanceof GotoPosition) {
			go = (GotoPosition)e.getLowLevelBehavior();
		}
		
		
		// check if new position is within the worlds bounds
		//System.out.println(b.getPosition());
		//System.out.println(World.getInstance().getField().getLength() + "  " + World.getInstance().getField().getWidth());
		
		//>prutteltje testing temp
		Point p = null;
		// Get field params
		float fieldX = World.getInstance().getField().getLength() / 2;
		float fieldY = World.getInstance().getField().getWidth() / 2;
		float targetPositionX = b.getPosition().getX();
		float targetPositionY = b.getPosition().getY();
		float borderZoneX = 100f;
		float borderZoneY = 100f;
		
		if(Math.abs(targetPositionX) < (fieldX - borderZoneX) && Math.abs(targetPositionY) < (fieldY - borderZoneY)) {
			p = new Point(targetPositionX, targetPositionY);
			
			go.setTarget(p);
		}
		else{
			go.setTarget(null);
		}
		//go.setTarget(b.getPosition());
	}
	
	private Robot getClosestToTarget(ArrayList<Robot> robots, Point p){
		Robot closest = null;
		double distance = Double.MAX_VALUE;
		for( Robot r : robots){
			Point robotPosition = r.getPosition();
//			System.out.println(r.isOnSight());
			if(robotPosition != null && p != null && r.isOnSight()){
				if( r.getPosition().getDeltaDistance(p) < distance ){
					closest = r;
					distance = r.getPosition().getDeltaDistance(p);
				}
			}
		}
		return closest;
	}
	
	public RobotExecuter findExecuter(int robotId, ArrayList<RobotExecuter> executers){
		for(RobotExecuter r : executers){
			if(r.getRobot().getRobotID() == robotId)
				return r;
		}
		return null;
	}
}