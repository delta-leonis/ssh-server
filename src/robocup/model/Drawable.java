package robocup.model;

import java.awt.Graphics2D;

/**
 * describes a drawable object for use in a GUI enviroment.
 * Object should be drawn with 0,0 as a center, the paint method should transform this to something usable in each unique case
 *
 */
public interface Drawable {
	/**
	 * Draw an object
	 * @param g Graphics2D object to draw to
	 */
	public void paint(Graphics2D g);
	
	/**
	 * Draw an object at a given origin
	 * @param g			Graphics2D object to draw to
	 * @param origin	given orientation
	 */
	public void paint(Graphics2D g, FieldPoint origin);
}
