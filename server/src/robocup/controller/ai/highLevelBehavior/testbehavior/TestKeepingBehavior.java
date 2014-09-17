package robocup.controller.ai.highLevelBehavior.testbehavior;

import java.util.ArrayList;

import robocup.Main;
import robocup.controller.ai.highLevelBehavior.Behavior;
import robocup.controller.ai.lowLevelBehavior.GotoPosition;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.Ball;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.model.World;
import robocup.output.ComInterface;
import robocup.output.RobotCom;

public class TestKeepingBehavior extends Behavior {

	private static final int GOAL_DEFENCE_RADIUS = 500;
	// used in calculation later on, prevent calculating it every update by defining it
	private static final int GOAL_DEFENCE_RADIUS_SQUARE = GOAL_DEFENCE_RADIUS * GOAL_DEFENCE_RADIUS; 
	private Ball ball;
	private World world;
	private Robot keeper;
	
	public TestKeepingBehavior() {
		world = World.getInstance();
	}
	
	@Override
	public void execute(ArrayList<RobotExecuter> executers) {
		keeper = world.getAlly().getRobotByID(Main.KEEPER_ROBOT_ID);
		ball = world.getBall();
		
		if(keeper != null) {
			RobotExecuter executer = findExecuter(Main.KEEPER_ROBOT_ID, executers);
			Point keeperDest = getKeeperPosition();
			
//			if(keeperDest != null)
//				System.out.println("Keeper will defend at: " + keeperDest);
			
			if(executer == null) {
				executer = new RobotExecuter(keeper);
				executer.setLowLevelBehavior(new GotoPosition(keeper, ComInterface.getInstance(RobotCom.class), 
						keeperDest != null ? keeperDest : new Point(0, 0)));
				new Thread(executer).start();
				executers.add(executer);
				
				((GotoPosition) executer.getLowLevelBehavior()).setTarget(null);
			}
			
			GotoPosition go = null;
			if(executer.getLowLevelBehavior() instanceof GotoPosition)
				go = (GotoPosition) executer.getLowLevelBehavior();
			else
				return;
			
			if(keeperDest != null && !isNearTarget(keeper, keeperDest)) {
				go.setTarget(keeperDest);
			} else {
				go.setTarget(null);
			}
		}
	}
	
	private boolean isNearTarget(Robot keeper, Point dest) {
		int keeperY = (int) keeper.getPosition().getY();
		int destY = (int) dest.getY();
		
		int dy = destY - keeperY;
		System.out.println(dy);
		
		return dy < 40 && dy > -40;
	}

	/**
	 * Calculate the position between the middle of the goal and the ball, with 500 distance from the goal
	 * @return
	 */
	private Point getKeeperPosition() {
		Point ballPosition = ball.getPosition();
		Point midGoal = new Point(world.getField().getLength() / 2, 0);
		Point newPosition = null;
		
		
		if(keeper.getPosition().getX() < 0)
			midGoal.setX(-midGoal.getX());
		
		if(ballPosition != null) {
			int angle = Math.abs(midGoal.getAngle(ballPosition));
			int realAngle = angle > 90 ? 180 - angle : angle;
			
			double dx = Math.sin(Math.toRadians(realAngle)) * GOAL_DEFENCE_RADIUS;
			double dy = Math.sqrt(GOAL_DEFENCE_RADIUS_SQUARE - dx * dx);
			
			int midGoalX = (int) midGoal.getX();
			int destX = (int) (midGoalX > 0 ? midGoalX - dx : midGoalX + dx);
			
			int midGoalY = (int) midGoal.getY();
			int destY = (int) (ballPosition.getY() > 0 ?  midGoalY + dy : midGoalY - dy);
			
			newPosition = new Point(destX, destY);
		}
		
		return newPosition;
	}
}
