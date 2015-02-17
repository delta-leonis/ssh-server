package robocup.model;

public class Ally extends Robot {
	private boolean dribble;
	private long lastKicked;

	public Ally(int robotID, boolean isKeeper, float height) {
		super(robotID, isKeeper, height);
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
