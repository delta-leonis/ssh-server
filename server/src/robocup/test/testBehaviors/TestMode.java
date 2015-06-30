package robocup.test.testBehaviors;

import java.util.ArrayList;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.controller.ai.highLevelBehavior.zoneBehavior.Mode;
import robocup.controller.ai.lowLevelBehavior.Attacker;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.controller.ai.movement.GotoPosition;
import robocup.model.FieldPoint;
import robocup.model.Robot;
import robocup.model.World;
import robocup.model.enums.FieldZone;

public class TestMode extends Mode {
	private RobotExecuter robot;
	private ArrayList<RobotExecuter> robots;
	private GotoPosition go;
	private static final int[] robotIDs = new int[]{0,3,6};
	
	public TestMode(Strategy strategy) {
		super(strategy, World.getInstance().getRobotExecuters());
		
		initializeOneRobot();
//		initializeTwoRobots();
//		initializeThreeRobots();
	}
	
	public void initializeOneRobot(){
		robot = executers.get(TestBehaviour.ROBOT_ID);
	}
	
	public void initializeTwoRobots(){
		robots = new ArrayList<RobotExecuter>();
		for(int i = 0; i < robotIDs.length -1; ++i){
			robots.add(World.getInstance().getRobotExecuters().get(robotIDs[i]));
		}
	}
	
	public void initializeThreeRobots(){
		robots = new ArrayList<RobotExecuter>();
		for(int i = 0; i < robotIDs.length; ++i){
			robots.add(World.getInstance().getRobotExecuters().get(robotIDs[i]));
		}
	}
	
	@Override
	public void execute(){
		if(World.getInstance().getRobotExecuters().get(TestBehaviour.ROBOT_ID).getLowLevelBehavior() == null){
			World.getInstance().getRobotExecuters().get(TestBehaviour.ROBOT_ID).setLowLevelBehavior(new Attacker(robot.getRobot()));
		}
//		testAttacker();
//		testFollowBallFacing(robot);
		testFollowBallMovementFacing(robot);
//		testThreeKeeperDefenders(robots, MID_GOAL_NEGATIVE);
//		testFollowBallMovement(robot);
//		testTwoRobotFollowBall();
//		testCircleAroundBall(robot);
	}
	
	public void testTwoRobotFollowBall(){
		if(World.getInstance().getRobotExecuters().get(robotIDs[0]).getLowLevelBehavior() == null){
			World.getInstance().getRobotExecuters().get(robotIDs[0]).setLowLevelBehavior(new Attacker(robots.get(0).getRobot()));
		}
		if(World.getInstance().getRobotExecuters().get(robotIDs[1]).getLowLevelBehavior() == null){
			World.getInstance().getRobotExecuters().get(robotIDs[1]).setLowLevelBehavior(new Attacker(robots.get(1).getRobot()));
		}
		
		FieldPoint ballPos = World.getInstance().getBall().getPosition();
		FieldPoint pos0 = new FieldPoint(ballPos.getX(), ballPos.getY() + 250);
		FieldPoint pos1 = new FieldPoint(ballPos.getX(), ballPos.getY() - 250);

		go = new GotoPosition(robots.get(0).getRobot(), 
				pos0,
				World.getInstance().getBall().getPosition());
		World.getInstance().getRobotExecuters().get(robotIDs[0]).getLowLevelBehavior().setGotoPosition(go);
		go.calculate(0, false);
		
		go = new GotoPosition(robots.get(1).getRobot(), 
				pos1,
				World.getInstance().getBall().getPosition());
		World.getInstance().getRobotExecuters().get(robotIDs[1]).getLowLevelBehavior().setGotoPosition(go);
		go.calculate(0, false);
	}
	
	public void testAttacker(){
		Attacker attacker = (Attacker)(World.getInstance().getRobotExecuters().get(TestBehaviour.ROBOT_ID).getLowLevelBehavior());
		attacker.update(robot.getRobot().getPosition().getAngle(new FieldPoint(-3000, 0)), 0, World.getInstance().getBall().getPosition());
		attacker.calculate();

	}
	
	/**
	 * Function that let's the given robot face the ball continuously.
	 */
	public void testFollowBallFacing(RobotExecuter robot){
		
		go = new GotoPosition(robot.getRobot(), 
				/*robot.getPosition()*/ null,
				 World.getInstance().getBall().getPosition());
		World.getInstance().getRobotExecuters().get(TestBehaviour.ROBOT_ID).getLowLevelBehavior().setGotoPosition(go);

		go.calculate(0, true);
	}
	
	/**
	 * Function that let's the given robot face the ball continuously.
	 */
	public void testCircleAroundBall(RobotExecuter robot){
		
		go = new GotoPosition(robot.getRobot(), 
				new FieldPoint(World.getInstance().getBall().getPosition().getX() + 100, World.getInstance().getBall().getPosition().getY()),
				 World.getInstance().getBall().getPosition());
		World.getInstance().getRobotExecuters().get(TestBehaviour.ROBOT_ID).getLowLevelBehavior().setGotoPosition(go);

		go.calculateTurnAroundTarget(300);
	}
	
	/**
	 * Function that makes the robot follow the ball without trying to face it.
	 */
	public void testFollowBallMovement(RobotExecuter robot){
		go = new GotoPosition(World.getInstance().getReferee().getAlly().getRobotByID(robot.getRobot().getRobotId()), 
				World.getInstance().getBall().getPosition(),
				 new FieldPoint(0,0));
		World.getInstance().getRobotExecuters().get(TestBehaviour.ROBOT_ID).getLowLevelBehavior().setGotoPosition(go);

		go.calculate(0, true);
	}
	
	/**
	 * Function that makes the robot follow the ball without trying to face it.
	 */
	public void testFollowBallMovementFacing(RobotExecuter robot){		
		go = new GotoPosition(robot.getRobot(), 
				new FieldPoint(World.getInstance().getBall().getPosition().getX() + 100, World.getInstance().getBall().getPosition().getY()),
				World.getInstance().getBall().getPosition());
		World.getInstance().getRobotExecuters().get(TestBehaviour.ROBOT_ID).getLowLevelBehavior().setGotoPosition(go);
//		go.setDribble(true);
		go.calculate(0, false);
	}
	
	/**
	 * Function that moves the given robot towards the given zone.
	 * @param robot The {@link Robot} we wish to move.
	 * @param zone The {@link Zone} we wish to move towards.
	 */
	public void testMoveToZone(RobotExecuter robot, FieldZone zone){
		robot.getLowLevelBehavior().setGotoPosition(new GotoPosition(World.getInstance().getReferee().getAlly().getRobotByID(robot.getRobot().getRobotId()), 
				zone.getCenterPoint(),
				new FieldPoint(0,0)));
		World.getInstance().getRobotExecuters().get(TestBehaviour.ROBOT_ID).getLowLevelBehavior().setGotoPosition(go);

		go.calculate(0,true);
	}
	
	/**
	 * Function that tests whether three {@link Robot robots} can defend the given point.
	 * @param robotExecs The {@link Robot robots} we wish to test.
	 * @param pointToDefend
	 */
	public void testThreeKeeperDefenders(ArrayList<RobotExecuter> robotExecs, FieldPoint objectPosition){
		FieldPoint newDestination = null;
		int distance = world.getField().getDefenceRadius() + world.getField().getDefenceStretch() / 2 + 50;
//		FieldPoint objectPosition = world.getReferee().getEastTeam().equals(world.getReferee().getAlly()) ? MID_GOAL_POSITIVE : MID_GOAL_NEGATIVE;
		FieldPoint subjectPosition = world.getBall().getPosition();
		FieldPoint offset = null;
		int i = 0;
		
		if (objectPosition != null && subjectPosition != null) {
			for(RobotExecuter robot : robotExecs){
				if(World.getInstance().getRobotExecuters().get(robotIDs[i]).getLowLevelBehavior() == null){
					World.getInstance().getRobotExecuters().get(robotIDs[i]).setLowLevelBehavior(new Attacker(robot.getRobot()));
				}
				
				if (robot.getRobot().getPosition().getY() == Math.max(
						robotExecs.get(0).getRobot().getPosition().getY(),
						Math.max(robotExecs.get(1).getRobot().getPosition().getY(), robotExecs.get(2).getRobot().getPosition()
								.getY())))
					offset = new FieldPoint(0, 250);
				else if (robot.getRobot().getPosition().getY() == Math.min(
						robotExecs.get(0).getRobot().getPosition().getY(),
						Math.min(robotExecs.get(1).getRobot().getPosition().getY(), robotExecs.get(2).getRobot().getPosition()
								.getY())))
					offset = new FieldPoint(0, -250);
//				else
//					offset = new FieldPoint(0, 0);
				
				double angle = objectPosition.getAngle(subjectPosition);
				double dx = Math.cos(Math.toRadians(angle)) * distance;
				double dy = Math.sin(Math.toRadians(angle)) * distance;
	
				double destX = objectPosition.getX() + dx;
				double destY = objectPosition.getY() + dy;
				newDestination = new FieldPoint(destX, destY);
				if(offset!=null){
					newDestination.setX(newDestination.getX() + offset.getX());
					newDestination.setY(newDestination.getY() + offset.getY());
				}
				
				
				go = new GotoPosition(robot.getRobot(), newDestination, subjectPosition);
				World.getInstance().getRobotExecuters().get(robotIDs[i]).getLowLevelBehavior().setGotoPosition(go);
				go.calculate(0, false);
				
//				goes.set(i, new GotoPosition(robot.getRobot(), newDestination, subjectPosition));
//				goes.get(i).calculate(false);
//				
				i++;
			}
		}
	}
	

	@Override
	public void updateAttacker(RobotExecuter executer) {
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
