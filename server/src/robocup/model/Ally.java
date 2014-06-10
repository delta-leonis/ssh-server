package robocup.model;

public class Ally extends Robot {

	private int channel;
	private boolean dribble;
	private long lastKicked;

	public Ally(int robotID, boolean isKeeper, float height, double diameter,
			Team team, int channel) {
		super(robotID, isKeeper, height, diameter, team);
		this.channel = channel;
	}

	/**
	 * @return the channel
	 */
	public int getChannel() {
		return channel;
	}

	/**
	 * @param channel
	 *            the channel to set
	 */
	public void setChannel(int channel) {
		this.channel = channel;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Ally [channel=" + channel + ", dribble=" + dribble + ", lastKicked=" + lastKicked + ", " + super.toString();
	}
	
	
	
}
