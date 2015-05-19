package robocup.controller.ai.highLevelBehavior;

import java.util.ArrayList;
import java.util.logging.Logger;

import robocup.Main;
import robocup.controller.ai.highLevelBehavior.events.EventSystem;
import robocup.controller.ai.highLevelBehavior.strategy.attack.FreeShotRoundPlay;
import robocup.controller.ai.highLevelBehavior.strategy.defense.BarricadeDefending;
import robocup.controller.ai.highLevelBehavior.strategy.standard.DirectFreeKickAttack;
import robocup.controller.ai.highLevelBehavior.strategy.standard.DirectFreeKickDefense;
import robocup.controller.ai.highLevelBehavior.strategy.standard.GameStop;
import robocup.controller.ai.highLevelBehavior.strategy.standard.IndirectFreeKickAttack;
import robocup.controller.ai.highLevelBehavior.strategy.standard.IndirectFreeKickDefense;
import robocup.controller.ai.highLevelBehavior.strategy.standard.KickOffAttack;
import robocup.controller.ai.highLevelBehavior.strategy.standard.KickOffDefense;
import robocup.controller.ai.highLevelBehavior.strategy.standard.PenaltyAttack;
import robocup.controller.ai.highLevelBehavior.strategy.standard.PenaltyDefense;
import robocup.controller.ai.highLevelBehavior.zoneBehavior.AttackMode;
import robocup.controller.ai.highLevelBehavior.zoneBehavior.DefenseMode;
import robocup.controller.ai.highLevelBehavior.zoneBehavior.Mode;
import robocup.controller.ai.highLevelBehavior.zoneBehavior.StandardMode;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.World;
import robocup.model.enums.Command;
import robocup.model.enums.Event;
import robocup.model.enums.TeamColor;

public class ZoneBehavior extends Behavior {

	private Logger LOGGER = Logger.getLogger(Main.class.getName());
	private World world;
	private Mode currentMode; // AttackMode or DefenseMode
	private EventSystem events;
	private ArrayList<AttackMode> attackModes;
	private ArrayList<DefenseMode> defenseModes;

	/**
	 * Create a ZoneBehavior.
	 * DefenseMode and AttackMode will be created as well.
	 * 	Modes are currently contained within ArrayLists for testing purposes.
	 * @see {@link #chooseAttackStrategy(ArrayList)}
	 * @see {@link #chooseDefenseStrategy(ArrayList)}
	 * @param executers the list containing all RobotExecuters
	 */
	public ZoneBehavior(ArrayList<RobotExecuter> executers) {
		world = World.getInstance();
		events = new EventSystem();

		attackModes = new ArrayList<AttackMode>();
//		attackModes.add(new AttackMode(new CornerToCornerAttack(), executers));
		attackModes.add(new AttackMode(new FreeShotRoundPlay(), executers));
//		attackModes.add(new AttackMode(new PenaltyAreaKickIn(), executers));
//		attackModes.add(new AttackMode(new SecondPostKickIn(), executers));

		defenseModes = new ArrayList<DefenseMode>();
		defenseModes.add(new DefenseMode(new BarricadeDefending(), executers));
//		defenseModes.add(new DefenseMode(new ForwardDefending(), executers));
//		defenseModes.add(new DefenseMode(new ZonallyBackward(), executers));
//		defenseModes.add(new DefenseMode(new ZonallyForward(), executers));
	}

	/**
	 * Execute the ZoneBehavior.
	 * Mode will be determined and executed based on ball and team positioning.
	 */
	@Override
	public void execute(ArrayList<RobotExecuter> executers) {
		determineMode(executers);

		if (currentMode != null)
			currentMode.execute();
	}

	/**
	 * Determine which Mode needs to be used.
	 * @param executers All the {@link robocup.controller.ai.lowLevelBehavior.RobotExecuter executers} that control our {@link robocup.model.Robot robots}
	 * @return {@link AttackMode} when our team is closer to the ball. {@link DefenseMode} when the enemy team is closer to the ball.
	 */
	private void determineMode(ArrayList<RobotExecuter> executers) {
		Event event = events.getNewEvent();

		if (event != null) {
			LOGGER.info("Event: " + event.name());
			switch (event) {
			case BALL_ALLY_CAPTURE:
				currentMode = chooseAttackStrategy(executers);
				break;
			case BALL_ENEMY_CAPTURE:
				currentMode = chooseDefenseStrategy(executers);
				break;
			case BALL_ALLY_CHANGEOWNER:
			case BALL_ENEMY_CHANGEOWNER:
				currentMode.assignRoles();
				break;
			case BALL_MOVESPAST_MIDLINE:
				if (world.allyHasBall())
					currentMode = chooseAttackStrategy(executers);
				else
					currentMode = chooseDefenseStrategy(executers);
				break;
			case BALL_MOVESPAST_NORTHSOUTH:
				currentMode.assignRoles();
				break;
			case REFEREE_NEWCOMMAND:
				if (world.getReferee().getCommand() == Command.NORMAL_START
						|| world.getReferee().getCommand() == Command.FORCE_START)
					if (world.allyHasBall())
						currentMode = chooseAttackStrategy(executers);
					else
						currentMode = chooseDefenseStrategy(executers);
				else
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
			}
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
		mode.assignRoles();
		LOGGER.info("strategy: " + mode.getStrategy().getClass().getSimpleName());
		return mode;
	}

	/**
	 * Choose a defense strategy based on previous decisions
	 * TODO track all previous strategies
	 * @return The DefenseMode containing the chosen strategy
	 */
	private DefenseMode chooseDefenseStrategy(ArrayList<RobotExecuter> executers) {
		DefenseMode mode = defenseModes.get((int) (Math.random() * defenseModes.size()));
		mode.assignRoles();
		LOGGER.info("strategy: " + mode.getStrategy().getClass().getSimpleName());
		return mode;
	}

	/**
	 * Choose a standard strategy based on referee command
	 * @return The mode containing the chosen strategy
	 */
	private Mode chooseStandardStrategy(ArrayList<RobotExecuter> executers) {
		Mode returnMode = null;

		switch (world.getReferee().getCommand()) {
		case DIRECT_FREE_BLUE:
			if (world.getReferee().getAllyTeamColor() == TeamColor.BLUE) {
				returnMode = new StandardMode(new DirectFreeKickAttack(), executers);
			} else {
				returnMode = new StandardMode(new DirectFreeKickDefense(), executers);
			}
			break;
		case DIRECT_FREE_YELLOW:
			if (world.getReferee().getAllyTeamColor() == TeamColor.YELLOW) {
				returnMode = new StandardMode(new DirectFreeKickAttack(), executers);
			} else {
				returnMode = new StandardMode(new DirectFreeKickDefense(), executers);
			}
			break;
		case FORCE_START:
			// Cannot be reached as this command is already handled, so return null
			break;
		case GOAL_BLUE:
		case GOAL_YELLOW:
			returnMode = new StandardMode(new GameStop(), executers);
			break;
		case HALT:
			returnMode = new StandardMode(new GameStop(), executers);
			break;
		case INDIRECT_FREE_BLUE:
			if (world.getReferee().getAllyTeamColor() == TeamColor.BLUE) {
				returnMode = new StandardMode(new IndirectFreeKickAttack(), executers);
			} else {
				returnMode = new DefenseMode(new IndirectFreeKickDefense(), executers);
			}
			break;
		case INDIRECT_FREE_YELLOW:
			if (world.getReferee().getAllyTeamColor() == TeamColor.YELLOW) {
				returnMode = new StandardMode(new IndirectFreeKickAttack(), executers);
			} else {
				returnMode = new StandardMode(new IndirectFreeKickDefense(), executers);
			}
			break;
		case NORMAL_START:
			// Cannot be reached as this command is already handled, so return null
			break;
		case PREPARE_KICKOFF_BLUE:
			if (world.getReferee().getAllyTeamColor() == TeamColor.BLUE) {
				returnMode = new StandardMode(new KickOffAttack(), executers);
			} else {
				returnMode = new StandardMode(new KickOffDefense(), executers);
			}
			break;
		case PREPARE_KICKOFF_YELLOW:
			if (world.getReferee().getAllyTeamColor() == TeamColor.YELLOW) {
				returnMode = new StandardMode(new KickOffAttack(), executers);
			} else {
				returnMode = new StandardMode(new KickOffDefense(), executers);
			}
			break;
		case PREPARE_PENALTY_BLUE:
			if (world.getReferee().getAllyTeamColor() == TeamColor.BLUE) {
				returnMode = new StandardMode(new PenaltyAttack(), executers);
			} else {
				returnMode = new StandardMode(new PenaltyDefense(), executers);
			}
			break;
		case PREPARE_PENALTY_YELLOW:
			if (world.getReferee().getAllyTeamColor() == TeamColor.YELLOW) {
				returnMode = new StandardMode(new PenaltyAttack(), executers);
			} else {
				returnMode = new StandardMode(new PenaltyDefense(), executers);
			}
			break;
		case STOP:
		case TIMEOUT_BLUE:
		case TIMEOUT_YELLOW:
			returnMode = new StandardMode(new GameStop(), executers);
			break;
		default:
			returnMode = null;
			break;
		}

		if (returnMode != null)
			returnMode.assignRoles();

		return returnMode;
	}
}
