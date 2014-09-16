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

	private Ball ball;
	private World world;
	
	@Override
	public void execute(ArrayList<RobotExecuter> executers) {
		world = World.getInstance();
		ball = world.getBall();
		Robot keeper = world.getAlly().getRobotByID(Main.KEEPER_ROBOT_ID);
		
		if(keeper != null) {
			RobotExecuter executer = findExecuter(Main.KEEPER_ROBOT_ID, executers);
			Point ballDest = getBallDestination();
			
			if(executer == null) {
				executer = new RobotExecuter(keeper);
				executer.setLowLevelBehavior(new GotoPosition(keeper, ComInterface.getInstance(RobotCom.class), 
						ballDest != null ? ballDest : new Point(1000, 0)));
//				executer.setLowLevelBehavior(new Keeping(keeper, ComInterface.getInstance(RobotCom.class)));
				new Thread(executer).start();
				executers.add(executer);
			}
			
			GotoPosition go = null;
			if(executer.getLowLevelBehavior() instanceof GotoPosition)
				go = (GotoPosition) executer.getLowLevelBehavior();
			else
				return;
			
			if(ballDest.getY() < 450 && ballDest.getY() > -450 && isOnSameSide(ballDest, keeper)) {
				System.out.println("Ball going towards defence line, intercepting.");
				go.setTarget(ballDest);
			} else {
				System.out.println("Setting keeper target to null");
				go.setTarget(null);
			}
		}
	}
	
	private boolean isOnSameSide(Point ballDest, Robot r) {
		return ballDest.getX() > 0 && r.getPosition().getX() > 0
			|| ballDest.getX() < 0 && r.getPosition().getX() < 0;
	}

	/**
	 * Calculate the position where the ball will cross the edge of the field
	 * @return
	 */
	private Point getBallDestination() {
		Point currentPosition = ball.getPosition();
		Point dest = null;
		if(currentPosition != null) {
			int direction = (int) ball.getDirection();
			
			int defenceLine = world.getField().getLength() / 2 - 300;
			
			if(currentPosition.getX() < 0)
				defenceLine = -defenceLine;
			
			int dx = defenceLine - (int) currentPosition.getX();
			// tan(90) or tan(-90) is inf, we can assume dx is 0 in this case
			int dy = direction == 90 || direction == -90 ? 0 : (int) (dx / Math.tan(direction));
			
			int destX = defenceLine;
			int destY = (int) currentPosition.getX() + dy;
			
			dest = new Point(destX, destY);
		}
		
		return dest;
	}
}
