package robocup.model;
/**
 * Describes an object on the {@link Field}
 * 
 * TODO needs a rewrite and cleanup. Data might be different with 4 cameras.
 */
public abstract class FieldObject {

	protected FieldPoint position;
	// LastUpdateTime = time off the day in sec
	protected long lastUpdateTime;
	protected double direction;
	protected double speed;

	public FieldObject() {
		lastUpdateTime = 0;
		position = null;
	}

	/**
	 * @return the direction this object is facing in degrees
	 */
	public double getDirection() {
		return direction;
	}

	/**
	 * @return The speed in millimeters/seconds
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * @return The last time this object got updated in seconds.
	 */
	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	/**
	 * Updates the time this object has last been updated.
	 * @param lastUpdateTime The time this object has last been updated in seconds. (Given by the SSL Vision program)
	 */
	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	/**
	 * @return The {@link FieldPoint} describing the position of this object.
	 */
	public FieldPoint getPosition() {
		return position;
	}
	
	public boolean isNearPosition(FieldPoint argPos, int argMinDist) {
		return (position.getDeltaDistance(argPos) <= argMinDist);
	}

	/**
	 * @param The {@link FieldPoint current position} of this object.
	 */
	public void setPosition(FieldPoint position) {
		this.position = position;
	}

	@Override
	public String toString() {
		return "position=" + position + ", lastUpdateTime=" + lastUpdateTime + ", direction=" + direction + ", speed="
				+ speed + "]";
	}
}
