package robocup.model;
/**
 * Describes an object on the {@link Field}
 * 
 * TODO needs a rewrite and cleanup. Data might be different with 4 cameras.
 */
public abstract class FieldObject {

	private FieldPoint position;
	// LastUpdateTime = time off the day in sec
	protected double lastUpdateTime;
	private double direction;
	private double speed;

	public FieldObject() {
		lastUpdateTime = 0;
		position = null;
	}

	/**
	 * Updates FieldObject
	 * @param updateTime Update timestamp given by the SSL Vision program
	 * @param newPosition New {@link FieldPoint}
	 * @post Updated position, direction and speed.
	 */
	public void update(FieldPoint newPosition, double updateTime, int camUpdateNo) {
		switch (camUpdateNo) {
		case 0:
			if (newPosition.getX() >= 0 || newPosition.getY() <= 0)
				return;
			break;
		case 1:
			if (newPosition.getX() <= 0 || newPosition.getY() >= 0)
				return;
			break;
		case 2:
			if (newPosition.getX() <= 0 || newPosition.getY() <= 0)
				return;
			break;
		case 3:
			if (newPosition.getX() >= 0 || newPosition.getY() >= 0)
				return;
			break;
		}

		if (position != null) {
			setDirection(newPosition);
			setSpeed(updateTime, newPosition);
		}

		position = newPosition;
		lastUpdateTime = updateTime;
	}

	/**
	 * @param direction Sets the direction on this object based on its last position.
	 */
	public void setDirection(FieldPoint newPosition) {
		if (position != null) {
			double deltaDistance = position.getDeltaDistance(newPosition);

			if (deltaDistance > 1.5) {
				direction = position.getAngle(newPosition);
				// System.out.println(direction);
			}
		}
	}

	/**
	 * @return the direction this object is facing in degrees
	 */
	public double getDirection() {
		return direction;
	}

	/**
	 * Calculates speed of this FieldObject based on its {@link FieldPoint previous position}, {@link FieldPoint current position} and the current time.
	 * @param updateTime The current time in seconds
	 * @param newPosition The {@link FieldPoint current position} of this object.
	 */
	private void setSpeed(double updateTime, FieldPoint newPosition) {
		double deltaDistance = position.getDeltaDistance(newPosition);
		double deltaTime = updateTime - lastUpdateTime;
		if (deltaDistance > 1.5) {
			speed = Math.abs((deltaDistance / deltaTime));
		}
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
	public double getLastUpdateTime() {
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
