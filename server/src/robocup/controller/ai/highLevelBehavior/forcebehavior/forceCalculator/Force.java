package robocup.controller.ai.highLevelBehavior.forcebehavior.forceCalculator;

import java.util.ArrayList;

import robocup.Main;
import robocup.model.World;
import robocup.output.ComInterface;
import robocup.output.RobotCom;
import robocup.controller.ai.highLevelBehavior.Behavior;
import robocup.controller.ai.highLevelBehavior.forcebehavior.Mode;
import robocup.controller.ai.lowLevelBehavior.FuckRobot;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Force extends Behavior {
	
	private World world;
	
	@SuppressWarnings("unused")
	private ArrayList<Mode> modes;

	@SuppressWarnings("unused")
	private Mode[] forceBehaviors;
	
	/* 
	 * 
	 * for(RobotExecuter r : executers){
			if(r.getRobot().getRobotID() == robotId)
				return r;
		}
		
	 */
	
	public Force() {
		world = World.getInstance();
		
		//	Team team = world.getAlly();
		/* 	Zet teamrollen
		   	1 keeper
		   	2 defenders
		   	3 attackers
		   
		   	Kan dynamisch gewisseld worden
	   		
	   		
	   		
	   */
		
		
	}
	
	@SuppressWarnings("unused")
	private Mode determineMode(World w) {
		//calculate the most effective mode to play in, being either attack or defensive playstyles 
		
		//why attack and defense
		
		
		
		
		
		throw new NotImplementedException();
	}

	@Override
	public void execute(ArrayList<RobotExecuter> executers) {
		// We zitten bv in defence, alle executers moeten defence dingen doen.
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		/*
		 defender = world.getAlly().getRobotByID(Main.TEST_ROBOT_ID);
		opponent = world.getEnemy().getRobotByID(Main.TEST_FUCK_ROBOT_ID);
		
		
		ball = world.getBall();
		int distanceToOpponent = 250;
		
		if(defender != null && ball != null) {
			RobotExecuter executer = findExecuter(Main.TEST_ROBOT_ID, executers);
			
			// Initialize executer for this robot
			if(executer == null) {
				executer = new RobotExecuter(defender);
				executer.setLowLevelBehavior(new FuckRobot(defender, ComInterface.getInstance(RobotCom.class), distanceToOpponent, 
						ball.getPosition(), defender.getPosition(), opponent.getPosition()));
				new Thread(executer).start();
				executers.add(executer);
			} else {
				((FuckRobot)executer.getLowLevelBehavior()).update(distanceToOpponent, ball.getPosition(), defender.getPosition(), opponent.getPosition());
			}
		}
		 */
		throw new NotImplementedException();
	}
}