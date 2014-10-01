package robocup.controller.ai.highLevelBehavior.testbehavior;

import java.util.ArrayList;

import robocup.controller.ai.highLevelBehavior.Behavior;
import robocup.controller.ai.lowLevelBehavior.Keeper;
import robocup.controller.ai.lowLevelBehavior.KeeperDefender;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.Ball;
import robocup.model.FieldObject;
import robocup.model.Point;
import robocup.model.Robot;
import robocup.model.World;
import robocup.output.ComInterface;
import robocup.output.RobotCom;

public class TestKeepingBehavior extends Behavior {
	
	// middle of the goal on both sides, negative having x < 0
	private static final Point MID_GOAL_NEGATIVE = new Point(-(World.getInstance().getField().getLength() / 2), 0);
	private static final Point MID_GOAL_POSITIVE = new Point(World.getInstance().getField().getLength() / 2, 0);
	
	private Ball ball;
	private World world;
	private Robot keeper;
	private int robotId;
	private Point offset;
	
	public TestKeepingBehavior(int robotId, Point offset) {
		world = World.getInstance();
		this.robotId = robotId;
		this.offset = offset;
	}
	
	@Override
	public void execute(ArrayList<RobotExecuter> executers) {
		keeper = world.getAlly().getRobotByID(robotId);
		ball = world.getBall();
		int distanceToGoal = offset != null ? 1000 : 500;
		
		if(keeper != null && ball != null) {
			RobotExecuter executer = findExecuter(robotId, executers);
			
			// Initialize executer for this robot
			if(executer == null) {
				executer = new RobotExecuter(keeper);
				if(offset == null)
					executer.setLowLevelBehavior(new Keeper(keeper, ComInterface.getInstance(RobotCom.class), distanceToGoal, false, 
						ball.getPosition(), keeper.getPosition(), keeper.getPosition().getX() > 0 ? MID_GOAL_POSITIVE : MID_GOAL_NEGATIVE));
				else
					executer.setLowLevelBehavior(new KeeperDefender(keeper, ComInterface.getInstance(RobotCom.class), distanceToGoal, false,
							ball.getPosition(), keeper.getPosition(), keeper.getPosition().getX() > 0 ? MID_GOAL_POSITIVE : MID_GOAL_NEGATIVE, offset));
				new Thread(executer).start();
				executers.add(executer);
			} else {
				((Keeper)executer.getLowLevelBehavior()).update(distanceToGoal, false, ball.getPosition(), keeper.getPosition());
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
}
