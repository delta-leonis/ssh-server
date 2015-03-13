package robocup.controller.ai.highLevelBehavior.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

public abstract class Strategy {

	protected ArrayList<RobotMode> roles = new ArrayList<RobotMode>();
	protected Map<RobotMode, FieldZone> zonesForRole = new HashMap<RobotMode, FieldZone>();

	/**
	 * Create a strategy with specified roles and zones connected to these roles.
	 * @param roles the roles used within this strategy
	 * @param zonesForRole the zones which are connected to specific roles
	 */
	public Strategy() {

	}

	/**
	 * Get a Zone object connected to a specific role for this strategy.
	 * @param role the role (connected to lowlevel behavior)
	 * @return Zone for this role when this role has a specified zone. null when no zone is specified.
	 */
	public FieldZone getZoneForRole(RobotMode role) {
		return zonesForRole.get(role);
	}
}
