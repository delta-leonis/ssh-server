package nl.saxion.robosim.model;

public class Target {

	private double x;
	private double y;

	/**
	 * Constructs a new Target with given coordinates
	 * @param x The x coordinate in millimeters
	 * @param y The y coordinate in millimeters.
	 */
	public Target(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @return the x co-ordinate of this {@link Target} in millimeters
	 */
	public double getX() {
		return x;
	}

	/**
	 * @param x the x co-ordinate to set for this {@link Target} in millimeters
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * @return the y co-ordinate of this {@link Target} in millimeters
	 */
	public double getY() {
		return y;
	}

	/**
	 * @param y the y co-ordinate to set for this {@link Target} in millimeters
	 */
	public void setY(double y) {
		this.y = y;
	}
}
