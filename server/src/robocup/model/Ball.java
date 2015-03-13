package robocup.model;

public class Ball extends FieldObject {

	private float posZ;
	private Robot owner;

	public Ball() {
		super();
	}

	public void update(double newTime, Point p, float posZ, int lastCamUpdateNo) {
		super.update(p, newTime, lastCamUpdateNo);
		this.posZ = posZ;
	}

	public void update(double newTime, Point p, int lastCamUpdateNo) {
		super.update(p, newTime, lastCamUpdateNo);
	}

	public float getPosZ() {
		return posZ;
	}

	public void setPosZ(float posZ) {
		this.posZ = posZ;
	}

	@Override
	public String toString() {
		return "Ball [posZ=" + posZ + ", " + super.toString() + "]" + "\r\n";
	}
	
	public void setOwner(Robot owner){
		this.owner = owner;
	}

	public Robot getOwner() {
		return owner;
	}
}
