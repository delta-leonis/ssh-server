package robocup.controller.ai.highLevelBehavior.testbehavior;

import java.util.ArrayList;

import robocup.Main;
import robocup.controller.ai.highLevelBehavior.Behavior;
import robocup.controller.ai.lowLevelBehavior.GotoPosition;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.Ball;
import robocup.model.FieldObject;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.model.World;
import robocup.output.ComInterface;
import robocup.output.RobotCom;

public class TestKeepingBehavior extends Behavior {

	private static final int GOAL_DEFENCE_RADIUS = 500;
	// used in calculation later on, prevent calculating it every update by defining it
	private static final int GOAL_DEFENCE_RADIUS_SQUARE = GOAL_DEFENCE_RADIUS * GOAL_DEFENCE_RADIUS;
	private static final int BORDER_ZONE_X = 200;
	private static final int BORDER_ZONE_Y = 200;
	
	// middle of the goal on both sides, negative having x < 0
	private static final Point MID_GOAL_NEGATIVE = new Point(-(World.getInstance().getField().getLength() / 2), 0);
	private static final Point MID_GOAL_POSITIVE = new Point(World.getInstance().getField().getLength() / 2, 0);
	
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
			
			// Initialize executer for this robot
			if(executer == null) {
				executer = new RobotExecuter(keeper);
				executer.setLowLevelBehavior(new GotoPosition(keeper, ComInterface.getInstance(RobotCom.class), 
						keeperDest != null ? keeperDest : new Point(0, 0)));
				new Thread(executer).start();
				executers.add(executer);
			}
			
			// Border zone of 200, only applies to X for keeper
			if(keeperDest != null) {
				if(keeperDest.getX() > 0 && MID_GOAL_POSITIVE.getX() - keeperDest.getX() < BORDER_ZONE_X)
					keeperDest.setX(MID_GOAL_POSITIVE.getX() - BORDER_ZONE_X);
				else if(keeperDest.getX() < 0 && MID_GOAL_NEGATIVE.getX() - keeperDest.getX() > -BORDER_ZONE_X)
					keeperDest.setX(MID_GOAL_NEGATIVE.getX() + BORDER_ZONE_X);
			}
			
			// get the low level behavior of the keeper
			GotoPosition go = null;
			if(executer.getLowLevelBehavior() instanceof GotoPosition)
				go = (GotoPosition) executer.getLowLevelBehavior();
			else
				return;
			
			// determine if the keeper should move towards the ball
			boolean moveToBall = false;
			if(ball.getPosition() != null 
					&& Math.abs(ball.getPosition().getX()) > MID_GOAL_POSITIVE.getX() - BORDER_ZONE_X
					&& Math.abs(ball.getPosition().getY()) > world.getField().getWidth() / 2 - BORDER_ZONE_Y)
					moveToBall = false;
			// Move towards the correct position, stop moving if the keeper is within 40 range
			// Move towards the ball if its close, but not more then 1000 from the goal
			if(keeperDest != null && moveToBall
					&& ball.getPosition().getDeltaDistance(keeper.getPosition().getX() > 0 
							? MID_GOAL_POSITIVE: MID_GOAL_NEGATIVE) < GOAL_DEFENCE_RADIUS + 200) {
				go.setTarget(ball.getPosition());
			} else if(keeperDest != null && !isWithinRange(keeper, keeperDest, 40)) {
				go.setTarget(keeperDest);
			} else {
				go.setTarget(null);
			}
		}
	}
	
	/**
	 * Calculate if the object is within range of the target
	 * @param keeper
	 * @param dest
	 * @param range
	 * @return
	 */
	private boolean isWithinRange(FieldObject object, Point target, int range) {		
		int dy = (int) (target.getY() - object.getPosition().getY());
//		int dx = (int) (target.getX() - object.getPosition().getY());
		
		return range > Math.abs(dy) /*&& range > Math.abs(dx)*/;
	}

	/**
	 * Calculate the position between the middle of the goal and the ball, with 500 distance from the goal
	 * @return
	 */
	private Point getKeeperPosition() {
		Point ballPosition = ball.getPosition();
		Point midGoal = MID_GOAL_POSITIVE;
		Point newPosition = null;

		if(keeper.getPosition().getX() < 0)
			midGoal = MID_GOAL_NEGATIVE;
		
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
