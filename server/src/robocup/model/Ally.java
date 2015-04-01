package robocup.model;

import robocup.model.enums.RobotMode;

public class Ally extends Robot {
	private boolean dribble;
	private long lastKicked;
	private RobotMode role;

	public Ally(int robotID, boolean isKeeper, double height) {
		super(robotID, isKeeper, height);
		role = RobotMode.KEEPERDEFENDER;
	}

	/**
	 * @return the dribble
	 */
	public boolean isDribble() {
		return dribble;
	}

	/**
	 * @param dribble the dribble to set
	 */
	public void setDribble(boolean dribble) {
		this.dribble = dribble;
	}
	
	/** 
	 * Set a role (keeper/defender/attacker/etc)
	 * @param role A role from the enumaration Mode.roles
	 */
	public void setRole(RobotMode role) {
		// System.out.println(" set role " + role);
		this.role = role;
	}

	/**
	 * Get the assigned role (keeper/defender/attacker/null/etc)
	 * @return A role from the enumaration Mode.roles
	 */
	public RobotMode getRole() {
		return role;
	}

	/**
	 * @return the lastKicked
	 */
	public long getLastKicked() {
		return lastKicked;
	}

	/**
	 * @param lastKicked the lastKicked to set
	 */
	public void setLastKicked(long lastKicked) {
		this.lastKicked = lastKicked;
	}

	@Override
	public String toString() {
		return "Ally [dribble=" + dribble + ", lastKicked=" + lastKicked + ", "
				+ super.toString();
	}
}
