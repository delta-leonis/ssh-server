package robocup.model;

import robocup.model.enums.RobotMode;

/**
 * Ally implementation of {@link Robot}
 */
public class Ally extends Robot {
	private boolean dribble;
	/**
	 * Timestamp of when kicker or chipper was last activated
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
	 * @return true if robot's dribbler is running
	 */
	public boolean isDribble() {
		return dribble;
	}

	/**
	 * @param dribble True to start the dribbler, false to stop it
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
	 * Set a new timestamp that describes last time that kicker or chipper has been activated
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
