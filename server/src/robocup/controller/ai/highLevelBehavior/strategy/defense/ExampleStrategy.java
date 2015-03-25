package robocup.controller.ai.highLevelBehavior.strategy.defense;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

/**
 * Example strategy class which implements the 'Dichtleggen links' strategy on the west half of the field
 */
public class ExampleStrategy extends Strategy {

	/**
	 * Create an example strategy.
	 * Roles must be added in the constructor in a specified order.
	 * Keeper must first be assigned (done through super()).
	 * Roles which are connected to a zone must be assigned afterwards.
	 * Last, all roles without specified zones must be added, i.e. KEEPERDEFENDER.
	 * This specific order is needed for the mode, roles are assigned in the same order.
	 * KEEPER > ROLES CONNECTED TO ZONE > ROLES WITHOUT ZONE.
	 */
	public ExampleStrategy() {
		super();
		roles.add(RobotMode.COUNTER);
		roles.add(RobotMode.GOALPOSTCOVERER);

		zonesForRole.put(RobotMode.GOALPOSTCOVERER, FieldZone.WEST_RIGHT_SECOND_POST);
		zonesForRole.put(RobotMode.COUNTER, FieldZone.EAST_CENTER);

		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.DISTURBER);
	}
}
