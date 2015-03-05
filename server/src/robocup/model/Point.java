package robocup.model;

import java.awt.geom.Point2D;

/**
 * Specifies a point on the {@link Field}. 
 * The drawing underneath depicts what this point represents.
 * 
 * (-x,y)________________________________	(x,y)
 * 		|								 |
 * 		|								 |
 *	   |				(0,0)		  	  |
 * 		|								 |
 * 		|________________________________|
 * 	 (-x,-y)								(+x,-y)
 * 
 * x being half the field-width.
 * y being half the field-height.
 */
public class Point {
	private float x;
	private float y;

	public Point(float x, float y) {
		super();
		this.x = x;
		this.y = y;
	}
	
	public Point2D.Double toPoint2D(){
		return new Point2D.Double(x,y);
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
	 * @return the x co-ordinate of this {@link Point}
	 */
	public float getX() {
		return x;
	}

	/**
	 * @param x the x co-ordinate to set for this {@link Point}
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * @return the y co-ordinate of this {@link Point}
	 */
	public float getY() {
		return y;
	}

	/**
	 * @param y the y co-ordinate to set for this {@link Point}
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
