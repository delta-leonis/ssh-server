package robocup.controller.ai.highLevelBehavior.strategy.defense;

import org.apache.commons.math3.util.Pair;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.FieldPoint;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

/**
 * Example strategy class which implements the 'Dichtleggen' strategy on the west half of the field
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

		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.DISTURBER);

		// TODO move outside to a class which has acces to model
		updateZones(null);
	}

	@Override
	public void updateZones(FieldPoint ballPosition) {
		super.updateZones(ballPosition);

		// TODO bepaal waar de bal is, assign zones op basis hiervan
		zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.GOALPOSTCOVERER, FieldZone.WEST_NORTH_SECONDPOST));
		zonesForRole.add(new Pair<RobotMode, FieldZone>(RobotMode.COUNTER, FieldZone.EAST_CENTER));
	}
}