package robocup.controller.ai.highLevelBehavior.strategy.defense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import robocup.controller.ai.highLevelBehavior.strategy.Strategy;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

/**
 * Example strategy class which implements the 'Dichtleggen links' strategy on the west half of the field
 */
public class ExampleStrategy extends Strategy {

	private static ArrayList<RobotMode> roles = new ArrayList<RobotMode>();
	protected static Map<RobotMode, FieldZone> zonesForRole = new HashMap<RobotMode, FieldZone>();

	public ExampleStrategy() {
		roles.add(RobotMode.KEEPER);
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.KEEPERDEFENDER);
		roles.add(RobotMode.COUNTER);
		roles.add(RobotMode.DISTURBER);
		roles.add(RobotMode.GOALPOSTCOVERER);

		zonesForRole.put(RobotMode.GOALPOSTCOVERER, FieldZone.WEST_RIGHT_SECOND_POST);
		zonesForRole.put(RobotMode.COUNTER, FieldZone.EAST_CENTER);
	}
}
