package robocup.controller.ai.highLevelBehavior.zoneBehavior;

import java.util.ArrayList;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.controller.ai.lowLevelBehavior.Attacker;
import robocup.controller.ai.lowLevelBehavior.Coverer;
import robocup.controller.ai.lowLevelBehavior.Keeper;
import robocup.controller.ai.lowLevelBehavior.KeeperDefender;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.Ally;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

public class DefenseMode extends Mode {

	public DefenseMode(Strategy strategy, ArrayList<RobotExecuter> executers) {
		super(strategy, executers);
	}

	@Override
	public void setRoles(ArrayList<RobotExecuter> executers) {
		// clear executers so we start clean
		for (RobotExecuter executer : executers) {
			((Ally) executer.getRobot()).setRole(null);
		}

		for (RobotMode role : strategy.getRoles()) {
			FieldZone zone = strategy.getZoneForRole(role);

			if (role == RobotMode.KEEPER) {
				// Find executer belonging to the goalie and set role
				((Ally) findExecuter(world.getReferee().getAlly().getGoalie(), executers).getRobot())
						.setRole(RobotMode.KEEPER);
			} else if (zone != null) {
				//	find robot near or in this zone and assign role
				//	Ally robot = world.getClosestAllyRobotToZoneWithoutRole(zone).get(0);
				//	robot.setRole(role);
			} else {
				// assign remaining roles
				ArrayList<Ally> robotsWithoutRole = getAllyRobotsWithoutRole();

				switch (role) {
				case KEEPERDEFENDER:
					// TODO move keeperdefenders to specific zone?
				case DISTURBER:
					robotsWithoutRole.get(0).setRole(role);
					break;
				default:
					System.out.println("Unknown role used in setRoles, please add me in DefenseMode, role: " + role);
				}
			}
		}
	}

	@Override
	public void updateAttacker(RobotExecuter executer) {
		Attacker attacker = (Attacker) executer.getLowLevelBehavior();
		// TODO Update with normal values
		attacker.update(0.0, 0, ball.getPosition());
	}

	@Override
	public void updateCoverer(RobotExecuter executer) {
		Coverer blocker = (Coverer) executer.getLowLevelBehavior();
		// TODO Update with normal values
		blocker.update(250, ball.getPosition(), null, 0);
	}

	@Override
	public void updateKeeperDefender(RobotExecuter executer) {
		KeeperDefender keeperDefender = (KeeperDefender) executer.getLowLevelBehavior();
		// TODO Update with normal values
		keeperDefender.update(1200, false, ball.getPosition(), executer.getRobot().getPosition());
	}

	@Override
	public void updateKeeper(RobotExecuter executer) {
		Keeper keeper = (Keeper) executer.getLowLevelBehavior();

		int distanceToGoal = 500;
		// TODO check if keeper needs to move to the ball, if so, set goToKick to true
		boolean goToKick = false;

		keeper.update(distanceToGoal, goToKick, ball.getPosition());
	}

	@Override
	protected void updatePenaltyKeeper(RobotExecuter executer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void updateGoalPostCoverer(RobotExecuter executer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void updateDisturber(RobotExecuter executer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void updateCounter(RobotExecuter executer) {
		// TODO Auto-generated method stub
		
	}
}
