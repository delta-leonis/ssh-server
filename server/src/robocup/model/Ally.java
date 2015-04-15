package robocup.model;

import robocup.model.enums.RobotMode;

/**
 * Ally implementation of {@link Robot}
 */
public class Ally extends Robot {
	private boolean dribble;
	/**
	 * timestamp when kicker was activated for the last time
	 */
	private long lastKicked;
	private RobotMode role;

	/**
	 * Construct a new {@link Ally}
	 * @param robotID	id for {@link Robot}
	 * @param height	height of the {@link Robot}
	 */
	public Ally(int robotID,  double height) {
		super(robotID, height);
		role = RobotMode.KEEPERDEFENDER;
	}

	/**
	 * @return true if robot has a activated dribbeler
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
	 * Set a {@link RobotMode}
	 * @param role the new {@link RobotMode} 
	 */
	public void setRole(RobotMode role) {
		this.role = role;
	}

	/**
	 * Get the assigned {@link RobotMode} 
	 * @return current {@link RobotMode} 
	 */
	public RobotMode getRole() {
		return role;
	}

	/**
	 * @return the timestamp of the last kick
	 */
	public long getLastKicked() {
		return lastKicked;
	}

	/**
	 * set a new timestamp that describes last time that kicker has been activated
	 * @param lastKicked new timestamp
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
