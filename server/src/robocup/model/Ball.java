package robocup.model;

/**
 * Describes a ball on the {@link Field}
 */
public class Ball extends FieldObject {

	private double posZ;
	private Robot owner;

	/**
	 * create object
	 */
	public Ball() {
		super();
	}

	/**
	 * Update parameters of the ball object
	 * @param newTime	Update timestamp
	 * @param p			new {@link FieldPoint} position
	 * @param posZ		new Z position for object
	 */
	public void update(double newTime, FieldPoint p, double posZ, int lastCamUpdateNo) {
		super.update(p, newTime, lastCamUpdateNo);
		this.posZ = posZ;
	}

	/**
	 * @return current Z-position 
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

	@Override
	public String toString() {
		return "Ball [posZ=" + posZ + ", " + super.toString() + "]" + "\r\n";
	}
}
