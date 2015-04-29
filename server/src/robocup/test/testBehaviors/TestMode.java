package robocup.test.testBehaviors;

import java.util.ArrayList;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.controller.ai.highLevelBehavior.zoneBehavior.Mode;
import robocup.controller.ai.lowLevelBehavior.Attacker;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.controller.ai.movement.GotoPosition;
import robocup.model.Ally;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.World;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

public class TestMode extends Mode {
	private Robot robot;
	private ArrayList<Robot> robots;
	private GotoPosition go;
	private ArrayList<GotoPosition> goes;
	
    /** Co-ordinates of the goal on the left side of the field */
    private static final FieldPoint MID_GOAL_NEGATIVE = new FieldPoint(-3000, 0);
    /** Co-ordinates of the goal on the right side of the field */
    private static final FieldPoint MID_GOAL_POSITIVE = new FieldPoint(3000, 0);
	
	public TestMode(Strategy strategy, ArrayList<RobotExecuter> executers) {
		super(strategy, executers);
		
		initializeOneRobot();
	}
	
	public void initializeOneRobot(){
		robot = executers.get(0).getRobot();
	}
	
	public void initializeThreeRobots(){
		robots = new ArrayList<Robot>();
		robots.add(robot);
		robots.add(executers.get(1).getRobot());
		robots.add(executers.get(2).getRobot());
	}
	
	@Override
	public void execute(){
		testFollowBallFacing(robot);
	}
	
	/**
	 * Function that let's the given robot face the ball continuously.
	 */
	public void testFollowBallFacing(Robot robot){
		go = new GotoPosition(World.getInstance().getReferee().getAlly().getRobotByID(robot.getRobotId()), 
				robot.getPosition(),
				 World.getInstance().getBall().getPosition());
		go.calculate();
	}
	
	/**
	 * Function that makes the robot follow the ball without trying to face it.
	 */
	public void testFollowBallMovement(Robot robot){
		go = new GotoPosition(World.getInstance().getReferee().getAlly().getRobotByID(robot.getRobotId()), 
				World.getInstance().getBall().getPosition(),
				 new FieldPoint(0,0));
		go.calculate();
	}
	
	/**
	 * Function that makes the robot follow the ball without trying to face it.
	 */
	public void testFollowBallMovementFacing(Robot robot){
		go = new GotoPosition(World.getInstance().getReferee().getAlly().getRobotByID(robot.getRobotId()), 
				World.getInstance().getBall().getPosition(),
				World.getInstance().getBall().getPosition());
		go.calculate();
	}
	
	/**
	 * Function that moves the given robot towards the given zone.
	 * @param robot The {@link Robot} we wish to move.
	 * @param zone The {@link Zone} we wish to move towards.
	 */
	public void testMoveToZone(Robot robot, FieldZone zone){
		go = new GotoPosition(World.getInstance().getReferee().getAlly().getRobotByID(robot.getRobotId()), 
				zone.getCenterPoint(),
				new FieldPoint(0,0));
		go.calculate();
	}
	
	/**
	 * Function that tests whether three {@link Robot robots} can defend the given point.
	 * @param robots The {@link Robot robots} we wish to test.
	 * @param pointToDefend
	 */
	public void testThreeKeeperDefenders(ArrayList<Robot> robots, FieldPoint pointToDefend){
		FieldPoint newDestination = null;
		int distance = world.getField().getDefenceRadius() + world.getField().getDefenceStretch() / 2 + 50;
		FieldPoint objectPosition = world.getReferee().getEastTeam().equals(world.getReferee().getAlly()) ? MID_GOAL_POSITIVE : MID_GOAL_NEGATIVE;
		FieldPoint subjectPosition = world.getBall().getPosition();
		FieldPoint offset;
		int i = 0;
		
		if (objectPosition != null && subjectPosition != null) {
			for(Robot robot : robots){
				if (robot.getPosition().getY() == Math.max(
						robots.get(0).getPosition().getY(),
						Math.max(robots.get(1).getPosition().getY(), robots.get(2).getPosition()
								.getY())))
					offset = new FieldPoint(0, 150);
				else if (robot.getPosition().getY() == Math.min(
						robots.get(0).getPosition().getY(),
						Math.min(robots.get(1).getPosition().getY(), robots.get(2).getPosition()
								.getY())))
					offset = new FieldPoint(0, -150);
				else
					offset = new FieldPoint(0, 0);
				
				double angle = objectPosition.getAngle(subjectPosition);
				double dx = Math.cos(Math.toRadians(angle)) * distance;
				double dy = Math.sin(Math.toRadians(angle)) * distance;
	
				double destX = objectPosition.getX() + dx;
				double destY = objectPosition.getY() + dy;
				newDestination = new FieldPoint(destX, destY);
				newDestination.setX(newDestination.getX() + offset.getX());
				newDestination.setY(newDestination.getY() + offset.getY());
				
				goes.set(i, new GotoPosition(robot, newDestination, subjectPosition));
				goes.get(i).calculate();
				
				i++;
			}
		}
	}
	

	@Override
	public void updateAttacker(RobotExecuter executer) {
		// Unused in DefenseMode
		Attacker attacker = (Attacker) executer.getLowLevelBehavior();
		int chipKick = 40;
		FieldPoint ballPosition = ball.getPosition();
		FieldPoint freeShot = world.hasFreeShot();

		if (freeShot != null) {
			double shootDirection = ballPosition.getAngle(world.hasFreeShot());
			attacker.update(shootDirection, chipKick, ballPosition);
		} else {
			ArrayList<Ally> runners = new ArrayList<Ally>();

			for (RobotExecuter itExecuter : executers) {
				Ally robot = (Ally) itExecuter.getRobot();

				if (robot.getRole() == RobotMode.RUNNER)
					runners.add(robot);
			}

			if (runners.size() > 0 && runners.get(0).getPosition() != null) {
				double shootDirection = ballPosition.getAngle(runners.get(0).getPosition());
				attacker.update(shootDirection, chipKick, ballPosition);
			}
		}
	}

	@Override
	public void updateRunner(RobotExecuter executer) {
	}

	@Override
	public void updateCoverer(RobotExecuter executer) {
	}

	@Override
	public void updateKeeperDefender(RobotExecuter executer) {
	}

	@Override
	public void updateKeeper(RobotExecuter executer) {
	}

	@Override
	protected void updatePenaltyKeeper(RobotExecuter executer) {
	}

	@Override
	protected void updateGoalPostCoverer(RobotExecuter executer) {
	}

	@Override
	protected void updateDisturber(RobotExecuter executer) {
	}

	@Override
	protected void updateCounter(RobotExecuter executer) {
	}
}
