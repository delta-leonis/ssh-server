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
	private static final int GOAL_DEFENCE_RADIUS_SQUARE = 250000; 
	private Ball ball;
	private World world;
	private Robot keeper;
	
	public TestKeepingBehavior() {
		world = World.getInstance();
		ball = world.getBall();
		keeper = world.getAlly().getRobotByID(Main.KEEPER_ROBOT_ID);
	}
	
	@Override
	public void execute(ArrayList<RobotExecuter> executers) {
		if(keeper != null) {
			RobotExecuter executer = findExecuter(Main.KEEPER_ROBOT_ID, executers);
			Point keeperDest = getKeeperPosition();
			
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
				keeperDest = null;
				ball.setPosition(null);
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
		return dy < 200 && dy > -200;
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
			midGoal.diagMirror();
		
		if(ballPosition != null) {
			int angle = Math.abs(midGoal.getAngle(ballPosition));
			
			int dx = (int) Math.sin(angle) * GOAL_DEFENCE_RADIUS;
			int dy = (int) Math.sqrt(GOAL_DEFENCE_RADIUS_SQUARE - dx * dx);
			
			int midGoalX = (int) midGoal.getX();
			int destX = midGoalX > 0 ? midGoalX - dx : midGoalX + dx;
			
			int midGoalY = (int) midGoal.getY();
			int destY = ballPosition.getY() > 0 ?  midGoalY + dy : midGoalY - dy;
			
			newPosition = new Point(destX, destY);
		}
		
		return newPosition;
	}
}
