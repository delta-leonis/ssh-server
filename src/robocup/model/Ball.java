package robocup.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

/**
 * Describes a ball on the {@link Field}
 */
public class Ball extends FieldObject {

	public final int SIZE = 43;	//in millimeter
	private double posZ;
	private Robot owner;

	/**
	 * Create object that describes a Ball on the {@link Field}
	 */
	public Ball() {
		super();
	}

	/**
	 * Update parameters of the ball object
	 * @param newTime	Update timestamp
	 * @param p			new {@link FieldPoint} position
	 * @param posZ		new Z position for object, also known the be the current height of the ball.
	 * @param direction 
	 * @param newOwner 
	 */
	public void update(long newTime, FieldPoint p, double posZ, double directionSpeed, double direction, Robot owner) {
		position = p;
		lastUpdateTime = newTime;
		this.posZ = posZ;
		this.speed = directionSpeed;
		this.direction = direction;
		this.owner = owner;
		World.getInstance().updateState();
	}

	/**
	 * @return current Z-position (Elevation off the ground)
	 */
	public double getPosZ() {
		return posZ;
	}

	/**
	 * Changes the current owner
	 * @param owner	new owner for the ball
	 */
	public void setOwner(Robot owner){
		this.owner = owner;
	}

	/**
	 * @return {@link Robot} that has the ball or is closest to it
	 */
	public Robot getOwner() {
		return owner;
	}
	
	public Rectangle2D getDangerRectangle(int distance){
		double x = getPosition().getX();
		double y = getPosition().getY();
		return new Rectangle2D.Double(x-distance, y-distance, distance*2, distance*2);
	}

	@Override
	public String toString() {
		return "Ball [posZ=" + posZ + ", " + super.toString() + "]" + "\r\n";
	}
	
	@Override
	public void paint(Graphics2D g2, FieldPoint position){
		g2.setColor(Color.orange);
		g2.fillArc((int) position.getX() -SIZE/2, (int)position.getY() -SIZE/2, SIZE, SIZE, 0, 360);
	}
}
