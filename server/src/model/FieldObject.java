package model;

import java.awt.geom.Ellipse2D;

public abstract class FieldObject {

	private Point position;
	private double diameter;
	private double lastUpdateTime;
	private int direction;
	private double speed;

	public FieldObject(double diameter) {
		this.diameter = diameter;
		lastUpdateTime = 0;
		position = new Point(0, 0);
	}

	/**
	 * Updates FieldObject
	 * 
	 * @param updateTime
	 *            Update timestamp
	 * @param newPosition
	 *            New positiont point.
	 * @post Updated position, direction and speed.
	 */
	public void update(Point newPosition, double updateTime) {
		double newTime = updateTime;
		setDirection(newPosition);
		setSpeed(newTime, newPosition);
		position = newPosition;
		lastUpdateTime = newTime;
	}

	/**
	 * @param direction
	 *            the direction to set
	 */
	public void setDirection(Point newPosition) {
		double deltaDistance = position.getDeltaDistance(newPosition);

		if (deltaDistance > 1.5) {
			direction = position.getAngle(newPosition);
		}
	}

	/**
	 * @return the direction
	 */
	public float getDirection() {
		return direction;
	}

	/**
	 * Calculates speed of FieldObject using
	 * 
	 * @param updateTime
	 * @param newPosition
	 */
	private void setSpeed(double updateTime, Point newPosition) {
		double deltaDistance = position.getDeltaDistance(newPosition);
		double deltaTime = updateTime - lastUpdateTime;
		// if (deltaTime == 0) {
		// System.err.println("DeltaTime == 0 Delen door nul is flauwe kul");
		// }
		if (deltaDistance > 1.5) {
			speed = Math.abs((deltaDistance / deltaTime));// Ik hoop dat dit
															// gaat werken
		}
	}

	/**
	 * @return the speed
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * @return the lastUpdateTime
	 */
	public double getLastUpdateTime() {
		return lastUpdateTime;
	}

	/**
	 * @param lastUpdateTime
	 *            the lastUpdateTime to set
	 */
	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	/**
	 * @return the position
	 */
	public Point getPosition() {
		return position;
	}

	/**
	 * @param position
	 *            the position to set
	 */
	public void setPosition(Point position) {
		this.position = position;
	}

	public Ellipse2D.Double getArea() {
		return new Ellipse2D.Double(position.getX() - (diameter / 2), position.getY() + (diameter / 2), diameter,
				diameter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "position=" + position + ", lastUpdateTime=" + lastUpdateTime + ", direction=" + direction + ", speed="
				+ speed + "]";
	}

}
