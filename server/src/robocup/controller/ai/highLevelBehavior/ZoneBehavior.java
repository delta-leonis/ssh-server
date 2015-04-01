package robocup.controller.ai.highLevelBehavior;

import java.util.ArrayList;

import robocup.controller.ai.highLevelBehavior.events.EventSystem;
import robocup.controller.ai.highLevelBehavior.strategy.defense.ExampleStrategy;
import robocup.controller.ai.highLevelBehavior.zoneBehavior.AttackMode;
import robocup.controller.ai.highLevelBehavior.zoneBehavior.DefenseMode;
import robocup.controller.ai.highLevelBehavior.zoneBehavior.Mode;
import robocup.controller.ai.lowLevelBehavior.RobotExecuter;
import robocup.model.Ball;
import robocup.model.World;

public class ZoneBehavior extends Behavior {

	private World world;
	private Mode currentMode;
	private EventSystem events;
	private Ball ball;

	/**
	 * Create a ZoneBehavior.
	 * DefenseMode and AttackMode will be created as well.
	 * @param executers the list containing all RobotExecuters
	 */
	public ZoneBehavior(ArrayList<RobotExecuter> executers) {
		world = World.getInstance();
		ball = world.getBall();
		events = new EventSystem();
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
		case BALL_ALLY_CHANGEOWNER:
			currentMode.setRoles(executers);
			break;
		case BALL_ENEMY_CAPTURE:
			currentMode = chooseDefenseStrategy(executers);
			break;
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
		if (world.allyHasBall() && currentMode instanceof DefenseMode)
			currentMode = chooseAttackStrategy(executers);

		if (!world.allyHasBall() && currentMode instanceof AttackMode)
			currentMode = chooseDefenseStrategy(executers);
	}

	/**
	 * Choose an attack strategy based on previous decisions
	 * TODO track all previous strategies
	 * @return The AttackMode containing the chosen strategy
	 */
	private AttackMode chooseAttackStrategy(ArrayList<RobotExecuter> executers) {
		return new AttackMode(new ExampleStrategy(), executers);
	}

	/**
	 * Choose a defense strategy based on previous decisions
	 * TODO track all previous strategies
	 * @return The DefenseMode containing the chosen strategy
	 */
	private DefenseMode chooseDefenseStrategy(ArrayList<RobotExecuter> executers) {
		return new DefenseMode(new ExampleStrategy(), executers);
	}

	/**
	 * Choose a standard strategy based on referee command
	 * @return The mode containing the chosen strategy
	 */
	private Mode chooseStandardStrategy(ArrayList<RobotExecuter> executers) {
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
