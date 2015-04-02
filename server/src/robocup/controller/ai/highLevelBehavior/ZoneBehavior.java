package robocup.controller.ai.highLevelBehavior;

import java.util.ArrayList;

import robocup.controller.ai.highLevelBehavior.events.EventSystem;
import robocup.controller.ai.highLevelBehavior.strategy.attack.CornerToCornerAttack;
import robocup.controller.ai.highLevelBehavior.strategy.attack.FreeShotRoundPlay;
import robocup.controller.ai.highLevelBehavior.strategy.attack.PenaltyAreaKickIn;
import robocup.controller.ai.highLevelBehavior.strategy.attack.SecondPostKickIn;
import robocup.controller.ai.highLevelBehavior.strategy.defense.BarricadeDefending;
import robocup.controller.ai.highLevelBehavior.strategy.defense.ExampleStrategy;
import robocup.controller.ai.highLevelBehavior.strategy.defense.ForwardDefending;
import robocup.controller.ai.highLevelBehavior.strategy.defense.ZonallyBackward;
import robocup.controller.ai.highLevelBehavior.strategy.defense.ZonallyForward;
import robocup.controller.ai.highLevelBehavior.zoneBehavior.AttackMode;
import robocup.controller.ai.highLevelBehavior.zoneBehavior.DefenseMode;
import robocup.controller.ai.highLevelBehavior.zoneBehavior.Mode;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.Ball;
import robocup.model.World;
import robocup.model.enums.Command;

public class ZoneBehavior extends Behavior {

	private World world;
	private Mode currentMode;
	private Ball ball;

	private EventSystem events;
	private ArrayList<AttackMode> attackModes;
	private ArrayList<DefenseMode> defenseModes;

	/**
	 * Create a ZoneBehavior.
	 * DefenseMode and AttackMode will be created as well.
	 * @param executers the list containing all RobotExecuters
	 */
	public ZoneBehavior(ArrayList<RobotExecuter> executers) {
		world = World.getInstance();
		ball = world.getBall();
		events = new EventSystem();

		attackModes = new ArrayList<AttackMode>();
		attackModes.add(new AttackMode(new CornerToCornerAttack(), executers));
		attackModes.add(new AttackMode(new FreeShotRoundPlay(), executers));
		attackModes.add(new AttackMode(new PenaltyAreaKickIn(), executers));
		attackModes.add(new AttackMode(new SecondPostKickIn(), executers));

		defenseModes = new ArrayList<DefenseMode>();
		defenseModes.add(new DefenseMode(new BarricadeDefending(), executers));
		defenseModes.add(new DefenseMode(new ForwardDefending(), executers));
		defenseModes.add(new DefenseMode(new ZonallyBackward(), executers));
		defenseModes.add(new DefenseMode(new ZonallyForward(), executers));
	}

	/**
	 * Execute the ZoneBehavior.
	 * Mode will be determined and executed based on ball and team positioning.
	 */
	@Override
	public void execute(ArrayList<RobotExecuter> executers) {
		determineMode(executers);

		currentMode.execute(executers);
	}

	/**
	 * Determine which Mode needs to be used.
	 * @param executers all executers
	 * @return AttackMode when our team is closer to the ball. DefenseMode when the enemy team is closer to the ball.
	 */
	private void determineMode(ArrayList<RobotExecuter> executers) {
		switch (events.getNewEvent()) {
		case BALL_ALLY_CAPTURE:
			currentMode = chooseAttackStrategy(executers);
			break;
		case BALL_ENEMY_CAPTURE:
			currentMode = chooseDefenseStrategy(executers);
			break;
		case BALL_ALLY_CHANGEOWNER:
		case BALL_ENEMY_CHANGEOWNER:
			currentMode.setRoles(executers);
			break;
		case BALL_MOVESPAST_MIDLINE:
			if (world.allyHasBall())
				currentMode = chooseAttackStrategy(executers);
			else
				currentMode = chooseDefenseStrategy(executers);
			break;
		case BALL_MOVESPAST_NORTHSOUTH:
			currentMode.getStrategy().updateZones(ball.getPosition());
			currentMode.setRoles(executers);
			break;
		case REFEREE_NEWCOMMAND:
			currentMode = chooseStandardStrategy(executers);
			break;
		case ROBOT_ENEMY_ATTACKCOUNT_CHANGE:
			if (world.getAttackingEnemiesCount() > 3)
				// choose ultra defense strategy
				currentMode = chooseDefenseStrategy(executers);
			else
				// choose normal defense strategy
				currentMode = chooseDefenseStrategy(executers);
			break;
		default:
			break;
		}

		// Check in case of missed event
		if (world.getReferee().getCommand() == Command.NORMAL_START) {
			if (world.allyHasBall() && currentMode instanceof DefenseMode)
				currentMode = chooseAttackStrategy(executers);

			if (!world.allyHasBall() && currentMode instanceof AttackMode)
				currentMode = chooseDefenseStrategy(executers);
		}
	}

	/**
	 * Choose an attack strategy based on previous decisions
	 * TODO track all previous strategies
	 * @return The AttackMode containing the chosen strategy
	 */
	private AttackMode chooseAttackStrategy(ArrayList<RobotExecuter> executers) {
		AttackMode mode = attackModes.get((int) (Math.random() * attackModes.size()));
		mode.getStrategy().updateZones(ball.getPosition());
		return mode;
	}

	/**
	 * Choose a defense strategy based on previous decisions
	 * TODO track all previous strategies
	 * @return The DefenseMode containing the chosen strategy
	 */
	private DefenseMode chooseDefenseStrategy(ArrayList<RobotExecuter> executers) {
		DefenseMode mode = defenseModes.get((int) (Math.random() * defenseModes.size()));
		mode.getStrategy().updateZones(ball.getPosition());
		return mode;
	}

	/**
	 * Choose a standard strategy based on referee command
	 * TODO create the corresponding strategy classes
	 * @return The mode containing the chosen strategy
	 */
	private Mode chooseStandardStrategy(ArrayList<RobotExecuter> executers) {
		switch (world.getReferee().getCommand()) {
		case DIRECT_FREE_BLUE:
			// return new StandardMode(new directFreeKick(), executers);
			break;
		case DIRECT_FREE_YELLOW:
			// return new StandardMode(new directFreeKick(), executers);
			break;
		case FORCE_START:
			// return new StandardMode(new forceStart(), executers);
			break;
		case GOAL_BLUE:
			// return new StandardMode(new goal(), executers);
			break;
		case GOAL_YELLOW:
			// return new StandardMode(new goal(), executers);
			break;
		case HALT:
			// return new StandardMode(new halt(), executers);
			break;
		case INDIRECT_FREE_BLUE:
			// return new StandardMode(new indirectFreeKick(), executers);
			break;
		case INDIRECT_FREE_YELLOW:
			// return new StandardMode(new indirectFreeKick(), executers);
			break;
		case NORMAL_START:
			// return new StandardMode(new normalStart(), executers);
			break;
		case PREPARE_KICKOFF_BLUE:
			// return new StandardMode(new prepareKickOff(), executers);
			break;
		case PREPARE_KICKOFF_YELLOW:
			// return new StandardMode(new prepareKickOff(), executers);
			break;
		case PREPARE_PENALTY_BLUE:
			// return new StandardMode(new preparePenalty(), executers);
			break;
		case PREPARE_PENALTY_YELLOW:
			// return new StandardMode(new preparePenalty(), executers);
			break;
		case STOP:
			// return new StandardMode(new stop(), executers);
			break;
		case TIMEOUT_BLUE:
			// return new StandardMode(new timeOut(), executers);
			break;
		case TIMEOUT_YELLOW:
			// return new StandardMode(new timeOut(), executers);
			break;
		default:
			// LOGGER.info("unknown command?");
			break;
		}

		return new DefenseMode(new ExampleStrategy(), executers);
	}

	/**
	 * @deprecated use execute instead.
	 */
	@Override
	public void updateExecuters(ArrayList<RobotExecuter> executers) {
		// Use execute instead, this is deprecated
	}
}
