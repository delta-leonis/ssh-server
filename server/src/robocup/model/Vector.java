package robocup.model;

public class Vector {
	
	private int x;
	private int y;

	/**
	 * Create a vector from origin to point x, y
	 * @param x
	 * @param y
	 */
	public Vector(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Add two vectors
	 * @param v
	 */
	public void add(Vector v) {
		x += v.x;
		y += v.y;
	}
	
	/**
	 * Calculate the length (power) of this vector
	 * @return length (power) of the vector
	 */
	public double length() {
		return Math.sqrt(x*x + y*y);
	}
}
