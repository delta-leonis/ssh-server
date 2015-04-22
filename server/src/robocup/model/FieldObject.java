package robocup.model;
/**
 * Describes an object on the {@link Field}
 * 
 * TODO needs a rewrite and cleanup. Data might be different with 4 cameras.
 */
public abstract class FieldObject {

	private FieldPoint positionCam0;
	private FieldPoint positionCam1;
	private FieldPoint position;
	// LastUpdateTime = time off the day in sec
	protected double lastUpdateTime;
	private double direction;
	private double speed;

	public FieldObject() {
		lastUpdateTime = 0;
		position = null;
		positionCam0 = null;
		positionCam1 = null;
	}

	/**
	 * Updates FieldObject
	 * @param updateTime Update timestamp given by the SSL Vision program
	 * @param newPosition New {@link FieldPoint}
	 * @post Updated position, direction and speed.
	 */
	public void update(FieldPoint newPosition, double updateTime, int camUpdateNo) {
		double newTime = updateTime;

		// System.out.println(newPosition);
		FieldPoint tmpPosition;
		if (!correctCamSide(newPosition.getX(), newPosition.getY(), camUpdateNo)) {
			if (camUpdateNo == 0 && positionCam1 == null) {
				;
			} else if (camUpdateNo == 1 && positionCam0 == null) {
				;
			} else if (positionCam0 == null && positionCam1 == null) {
				;
			} else {
				;
			}
		}
		if (camUpdateNo == 0) {
			positionCam0 = new FieldPoint(newPosition.getX(), newPosition.getY());
			// System.out.println(camUpdateNo + "# " + positionCam0.getX());
		}
		if (camUpdateNo == 1) {
			positionCam1 = new FieldPoint(newPosition.getX(), newPosition.getY());
			// System.out.println(camUpdateNo + "# " + positionCam1.getX());
		}

		if (World.getInstance().getField().getCameraOverlapZoneWidth() > Math.abs(newPosition.getX())) {
			if (this instanceof Ball) {
				if ((positionCam0 != null && positionCam1 != null)) {

					double newX = positionCam0.getX();
					double newY = positionCam0.getY();
					if (Math.abs(positionCam1.getX()) > Math.abs(newX)
							&& Math.abs(positionCam1.getY()) > Math.abs(newY)) {
						newX = positionCam1.getX();
						newY = positionCam1.getY();
						positionCam1 = null;
					} else {
						positionCam0 = null;
					}

					// System.out.println("isbal: " + (this instanceof Ball));
					tmpPosition = new FieldPoint(newX, newY);
				} else {
					tmpPosition = newPosition;
				}
			} else {
				tmpPosition = newPosition;
			}
		} else {
			tmpPosition = newPosition;
		}

		// if(this instanceof Robot) {
		// System.out.println(" Robo loc: " + tmpPosition);
		// }

		// lastCamUpdateNo = camUpdateNo;
		if (position != null) {
			setDirection(tmpPosition);
			setSpeed(newTime, tmpPosition);
		}

		position = tmpPosition;
		lastUpdateTime = newTime;
	}

	public boolean correctCamSide(double d, double e, int camNo) {
		if (d > 0 && camNo == 1)
			return true;
		if (d < 0 && camNo == 0)
			return true;
		return false;
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
