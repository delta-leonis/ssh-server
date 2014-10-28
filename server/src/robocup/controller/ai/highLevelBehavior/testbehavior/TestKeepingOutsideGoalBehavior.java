package robocup.controller.ai.highLevelBehavior.testbehavior;

import java.util.ArrayList;

import robocup.controller.ai.highLevelBehavior.Behavior;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.controller.ai.movement.GotoPosition;
import robocup.model.Ball;
import robocup.model.FieldObject;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.model.World;
import robocup.output.ComInterface;
import robocup.output.RobotCom;

public class TestKeepingOutsideGoalBehavior extends Behavior {

	private static final int BORDER_ZONE_X = 200;
	private static final int BORDER_ZONE_Y = 200;
	private static final int BORDER_Y = World.getInstance().getField().getWidth() / 2;
	private static final int BORDER_X = World.getInstance().getField().getLength() / 2;
	
	// middle of the goal on both sides, negative having x < 0
	private static final Point MID_GOAL_NEGATIVE = new Point(-BORDER_X, 0);
	private static final Point MID_GOAL_POSITIVE = new Point(BORDER_X, 0);
	
	private Ball ball;
	private World world;
	private Robot keeper;
	private int robotId;
	private int goalRadius;
	private int goalRadiusSquare;
	private int yOffset;
	
	public TestKeepingOutsideGoalBehavior(int robotId, int goalRadius, int yOffset) {
//		world = World.getInstance();
		this.robotId = robotId;
		this.goalRadius = goalRadius;
		goalRadiusSquare = goalRadius * goalRadius;
		this.yOffset = yOffset;
	}
	
	@Override
	public void execute(ArrayList<RobotExecuter> executers) {
		keeper = world.getAlly().getRobotByID(robotId);
		ball = world.getBall();
		
		if(keeper != null) {
			RobotExecuter executer = findExecuter(robotId, executers);
			Point keeperDest = getKeeperPosition();
			
			// Initialize executer for this robot
			if(executer == null) {
				executer = new RobotExecuter(keeper);
//				executer.setLowLevelBehavior(new GotoPosition(keeper, ComInterface.getInstance(RobotCom.class), 
//						keeperDest != null ? keeperDest : new Point(0, 0)));
				new Thread(executer).start();
				executers.add(executer);
			}

			// border zone x
			if(keeperDest != null) {
				if(keeperDest.getX() > 0 && BORDER_X - keeperDest.getX() < BORDER_ZONE_X)
					keeperDest.setX(BORDER_X - BORDER_ZONE_X);
				else if(keeperDest.getX() < 0 && -BORDER_X - keeperDest.getX() > -BORDER_ZONE_X)
					keeperDest.setX(-BORDER_X + BORDER_ZONE_X);
			}

			// border zone y
			if(keeperDest != null) {
				if(keeperDest.getY() > 0 && keeperDest.getY() > BORDER_Y - BORDER_ZONE_Y)
					keeperDest.setY(BORDER_Y - BORDER_ZONE_Y);
				else if(keeperDest.getY() < 0 && keeperDest.getY() < -BORDER_Y  + BORDER_ZONE_Y)
					keeperDest.setY(-(BORDER_Y - BORDER_ZONE_Y));
			}

			// get the low level behavior of the keeper
			GotoPosition go = null;
//			if(executer.getLowLevelBehavior() instanceof GotoPosition)
//				go = (GotoPosition) executer.getLowLevelBehavior();
//			else
//				return;
			
			// determine if the keeper should move towards the ball
			boolean moveToBall = false;
			if(ball.getPosition() != null 
					&& Math.abs(ball.getPosition().getX()) > MID_GOAL_POSITIVE.getX() - BORDER_ZONE_X
					&& Math.abs(ball.getPosition().getY()) > BORDER_Y - BORDER_ZONE_Y)
					moveToBall = false;
			// Move towards the correct position, stop moving if the keeper is within 40 range
			// Move towards the ball if its close, but not more then 1000 from the goal
			if(keeperDest != null && moveToBall
					&& ball.getPosition().getDeltaDistance(keeper.getPosition().getX() > 0 
							? MID_GOAL_POSITIVE : MID_GOAL_NEGATIVE) < goalRadius + 200) {
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
			
			double dx = Math.sin(Math.toRadians(realAngle)) * goalRadius;
			double dy = Math.sqrt(goalRadiusSquare - dx * dx);
			
			int midGoalX = (int) midGoal.getX();
			int destX = (int) (midGoalX > 0 ? midGoalX - dx : midGoalX + dx);
			
			int midGoalY = (int) midGoal.getY();
			int destY = (int) (ballPosition.getY() > 0 ?  midGoalY + dy : midGoalY - dy) + yOffset;
			
			newPosition = new Point(destX, destY);
		}
		
		return newPosition;
	}
}
