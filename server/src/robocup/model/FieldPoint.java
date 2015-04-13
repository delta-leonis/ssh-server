package robocup.model;

import java.awt.geom.Point2D;
import java.util.logging.Logger;

import robocup.Main;

/**
 * Specifies a point on the {@link Field}. 
 * The drawing underneath depicts what this point represents.
 * 
 * (-x,y)________________________________	(x,y)
 * 		|								 |
 * 		|								 |
 *	    |				(0,0)		  	 |
 * 		|								 |
 * 		|________________________________|
 * 	 (-x,-y)								(+x,-y)
 * 
 * x being half the field-width.
 * y being half the field-height.
 * 
 * TODO
 * Give Point2D and convert to a FieldPoint. Can be a static method
 */
public class FieldPoint {
	private double x;
	private double y;
	
	public FieldPoint(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}

	/**
	 * Constructor for {@link FieldPoint} where x and y are initialized on basis of a String
	 * coordinate.
	 * @param coordinate The x and y coordinates separated by a comma. Like: 200,40
	 */
	public FieldPoint(String coordinate) {
		String[] xy = coordinate.split(",");
		if(xy.length != 2) {
			Logger.getLogger(Main.class.getName()).warning("Point coordinate has wrong format");
			return;
		}
		x = Double.parseDouble(xy[0]);
		y = Double.parseDouble(xy[1]);
	}

	public Point2D.Double toPoint2D(){
		return new Point2D.Double(x,y);
	}

	/**
	 * @param ratio Ratio is percentage/100 that represents minification of the drawn field
	 * @return The {@link FieldPoint} translated to a {@link Point2D.Double} for GUI use.
	 */
	public Point2D.Double toGUIPoint(double ratio) {
		return new Point2D.Double((x+(World.getInstance().getField().getWidth()/2))*ratio,
				(-1*y+World.getInstance().getField().getHeight()/2)*ratio);
	}

	/**
	 * Calculate angle between 2 points
	 * @param target
	 * @return Angle in degrees between this and target
	 */
	public double getAngle(FieldPoint target) {
		return Math.toDegrees(Math.atan2(target.getY() - y, target.getX() - x));
	}

	/**
	 * Calculates distance between 2 points
	 * @param target
	 * @return Distance between this and target
	 */
	public double getDeltaDistance(FieldPoint target) {
		return Math.sqrt((target.getX() - x) * (target.getX() - x) + (target.getY() - y) * (target.getY() - y));
	}

	/**
	 * Creates a new diagonally mirrored point
	 * @return diagmirrored point
	 */
	public FieldPoint diagMirror() {
		return new FieldPoint(-x, -y);
	}

	/**
	 * @return the x co-ordinate of this {@link FieldPoint}
	 */
	public double getX() {
		return x;
	}

	/**
	 * @param x the x co-ordinate to set for this {@link FieldPoint}
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * @return the y co-ordinate of this {@link FieldPoint}
	 */
	public double getY() {
		return y;
	}

	/**
	 * @param y the y co-ordinate to set for this {@link FieldPoint}
	 */
	public void setY(double y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "Point [x=" + Math.round(x) + ", y=" + Math.round(y) + "]";
	}

	@Override
	public boolean equals(Object point) {
		FieldPoint p = (FieldPoint) point;
		return p.getX() == x && p.getY() == y;
	}
}
