package robocup.model;

public class Ball extends FieldObject {

	private double posZ;
	private Robot owner;

	public Ball() {
		super();
	}

	public void update(double newTime, FieldPoint p, double posZ, int lastCamUpdateNo) {
		super.update(p, newTime, lastCamUpdateNo);
		this.posZ = posZ;
	}

	public void update(double newTime, FieldPoint p, int lastCamUpdateNo) {
		super.update(p, newTime, lastCamUpdateNo);
	}

	public double getPosZ() {
		return posZ;
	}

	public void setPosZ(double posZ) {
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
