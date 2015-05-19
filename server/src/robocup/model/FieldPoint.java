package robocup.model;

import java.awt.geom.Point2D;
import java.util.logging.Logger;

import robocup.Main;

/**
 * Specifies a point on the {@link Field}. 
 * The drawing underneath depicts what this point represents.<br>
 * <img src="../../../images/fieldPoint.jpg" /><br>
 * 
 * x being half the field-width in millimeters.<br>
 * y being half the field-height in millimeters.
 */
public class FieldPoint {
	private double x;
	private double y;
	
	/**
	 * Constructs a new FieldPoint with given coordinates
	 * @param x The x coordinate in millimeters
	 * @param y The y coordinate in millimeters.
	 */
	public FieldPoint(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}

	/**
	 * Constructor for {@link FieldPoint} where x and y are initialized based off a String
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

	/**
	 * @param ratio Ratio is percentage/100 that represents minification of the drawn field
	 * @return The {@link FieldPoint} translated to a {@link Point2D.Double} for GUI use.
	 */
	public Point2D.Double toGUIPoint(double ratio) {
		return toGUIPoint(ratio, false);
	}
	
	/**
	 * @param ratio Ratio is percentage/100 that represents minification of the drawn field
	 * @param mirror When Mirror is true, the returned point will be mirrored horizontally and vertically
	 * @return The {@link FieldPoint} translated to a {@link Point2D.Double} for GUI use.
	 */
	public Point2D.Double toGUIPoint(double ratio, boolean mirror){
		return new Point2D.Double(((mirror ? -1 : 1)*x+(World.getInstance().getField().getLength()/2))*ratio,
				((mirror ? 1 : -1)*y+World.getInstance().getField().getWidth()/2)*ratio);
	}
	
	/**
	 * @return convert {@link FieldPoint} to a {@link Point2D.Double} for GUI purposes
	 */
	public Point2D.Double toPoint2D(){
		return new Point2D.Double(x,y);
	}

	/**
	 * Calculate angle between this point and the given {@link FieldPoint}
	 * @param target The target {@link FieldPoint}
	 * @return Angle in degrees between this and target
	 */
	public double getAngle(FieldPoint target) {
		return Math.toDegrees(Math.atan2(target.getY() - y, target.getX() - x));
	}

	/**
	 * Calculates distance between 2 points
	 * @param target The target {@link FieldPoint}
	 * @return Distance between this and target in millimeters
	 */
	public double getDeltaDistance(FieldPoint target) {
		return Math.sqrt((target.getX() - x) * (target.getX() - x) + (target.getY() - y) * (target.getY() - y));
	}

	/**
	 * Creates a new mirrored {@link FieldPoint} in x=0
	 * @return The current {@link FieldPoint} only mirrored across the middle.
	 */

	public FieldPoint mirror() {
		return new FieldPoint(-x, y);
	}

	/**
	 * @return the x co-ordinate of this {@link FieldPoint} in millimeters
	 */
	public double getX() {
		return x;
	}

	/**
	 * @param x the x co-ordinate to set for this {@link FieldPoint} in millimeters
	 */
	public void setX(double x) {
		this.x = x;
	}

	/**
	 * @return the y co-ordinate of this {@link FieldPoint} in millimeters
	 */
	public double getY() {
		return y;
	}

	/**
	 * @param y the y co-ordinate to set for this {@link FieldPoint} in millimeters
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
	
	/**
	 * Same functionality as {@link #equals(Object)} but values are rounded before comparing.
	 * Can be useful when a {@link FieldPoint} needs to be compared, but not too precise.
	 */
	public boolean equalsRounded(Object point) {
		FieldPoint p = (FieldPoint) point;
		return Math.round(p.getX()) == Math.round(x) && Math.round(p.getY()) == Math.round(y);
	}
}
