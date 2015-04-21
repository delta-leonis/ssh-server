package robocup.controller.ai.highLevelBehavior.strategy;

import java.util.ArrayList;

import org.apache.commons.math3.util.Pair;

import robocup.model.FieldPoint;
import robocup.model.enums.FieldZone;
import robocup.model.enums.RobotMode;

public abstract class Strategy {

	protected ArrayList<RobotMode> roles = new ArrayList<RobotMode>();
	protected ArrayList<Pair<RobotMode, FieldZone>> zonesForRole = new ArrayList<Pair<RobotMode, FieldZone>>();

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
		Pair<RobotMode, FieldZone> toRet = null;

		for (Pair<RobotMode, FieldZone> pair : zonesForRole) {
			if (pair.getFirst() == role) {
				toRet = pair;
				break;
			}
		}

		// remove zone role to prevent assigning the same zone twice
		zonesForRole.remove(toRet);

		return toRet == null ? null : toRet.getSecond();
	}

	/**
	 * Get all the roles which are used in this strategy
	 * @return all roles
	 */
	public ArrayList<RobotMode> getRoles() {
		return roles;
	}

	/**
	 * Update zones for this strategy based on ballposition.
	 * Example:
	 * Second goal post coverer will receive a new zone because its current zone is on the same field half as the ball.
	 * @param ballPosition The position of the ball
	 */
	public void updateZones(FieldPoint ballPosition) {
		zonesForRole.clear();
	}
}
