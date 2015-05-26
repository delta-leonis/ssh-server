// Code from robocup-ssh git (march 2015)

package robocup.model;

public class Point {
	private float x;
	private float y;

	public Point(float x, float y) {
		super();
		this.x = x;
		this.y = y;
	}

	/**
	 * Calculate angle between 2 points
	 * @param target
	 * @return Angle in degrees (int) between this and target
	 */
	public int getAngle(Point target) {
		// return (int) Math.toDegrees(Math.atan2(target.getX() - x,
		// target.getY() - y)); //TODO fix x, y fuck-up
		return (int) Math.toDegrees(Math.atan2(target.getY() - y, target.getX() - x));
	}

	/**
	 * Calculates distance between 2 points
	 * @param target
	 * @return Distance between this and target
	 */
	public double getDeltaDistance(Point target) {
		return Math.sqrt((target.getX() - x) * (target.getX() - x) + (target.getY() - y) * (target.getY() - y));
	}

	/**
	 * Creates a new diagonally mirrored point
	 * @return diagmirrored point
	 */
	public Point diagMirror() {
		return new Point(-x, -y);
	}

	/**
	 * @return the x
	 */
	public float getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public float getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(float y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "Point [x=" + Math.round(x) + ", y=" + Math.round(y) + "]";
	}

	@Override
	public boolean equals(Object point) {
		Point p = (Point) point;
		return p.getX() == x && p.getY() == y;
	}
}