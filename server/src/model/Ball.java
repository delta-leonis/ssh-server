package model;

public class Ball extends FieldObject {

	private float posZ;

	public Ball(double diameter){
		super(diameter);
	}

	/**
	 *  
	 */
	public void update(double newTime, Point p, float posZ) {
		super.update(p, newTime);
		this.posZ = posZ;
	}
	
	/**
	 *  
	 */
	public void update(double newTime, Point p) {
		super.update(p, newTime);
	}

	public float getPosZ() {
		return posZ;
	}

	public void setPosZ(float posZ) {
		this.posZ = posZ;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Ball [posZ=" + posZ + ", " + super.toString() + "]" + "\r\n";
	}
	
	
	
	

}