package robocup.model;

import java.awt.Graphics2D;

/**
 * Describes an object on the {@link Field}
 * 
 * TODO needs a rewrite and cleanup. Data might be different with 4 cameras.
 */
public abstract class FieldObject implements Drawable{

	protected FieldPoint position;
	// LastUpdateTime = time off the day in sec
	protected long lastUpdateTime;
	protected double direction;
	protected double speed;
	private boolean overrideOnsight;
	private boolean isOnsight;

	public FieldObject() {
		lastUpdateTime = 0;
		position = null;
		isOnsight = false;
		overrideOnsight = false;
	}

	/**
	 * @return the direction this object is facing in degrees
	 */
	public double getDirection() {
		return direction;
	}

	/**
	 * @return The speed in millimeters/seconds
	 */
	public double getSpeed() {
		return speed;
	}

	public boolean overrideOnsight(){
		return overrideOnsight;
	}

	public void setOverrideOnsight(boolean override){
		overrideOnsight = override;
	}

	/**
	 * @return true if robot is visible on the {@link Field}
	 */
	public boolean isOnSight() {
		return isOnsight || overrideOnsight;
	}

	/**
	 * @param onSight robot visible on camera?
	 */
	public void setOnSight(boolean onSight) {
		isOnsight = onSight;
	}

	/**
	 * @return The last time this object got updated in seconds.
	 */
	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	/**
	 * Updates the time this object has last been updated.
	 * @param lastUpdateTime The time this object has last been updated in seconds. (Given by the SSL Vision program)
	 */
	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	/**
	 * @return The {@link FieldPoint} describing the position of this object.
	 */
	public FieldPoint getPosition() {
		return position;
	}
	
	public boolean isNearPosition(FieldPoint argPos, int argMinDist) {
		return (position.getDeltaDistance(argPos) <= argMinDist);
	}

	/**
	 * @param The {@link FieldPoint current position} of this object.
	 */
	public void setPosition(FieldPoint position) {
		this.position = position;
	}

	/**
	 * 
	 * @param object	object to compare to
	 * @param distance	maximum distance to object
	 * @param angle		maximum difference in angle to object
	 * @return			true when object is close
	 */
	public boolean isCloseTo(Robot robot, int distance, int angle){
		return Math.abs(
				Math.abs(robot.getOrientation()) - Math.abs(robot.getPosition().getAngle(getPosition()))) < angle
				&& robot.getPosition().getDeltaDistance(getPosition()) < distance;
	}
	/**
	 * 
	 * @param object	object to compare to
	 * @param distance	maximum distance to object
	 * @return			true when object is close
	 */
	public boolean isCloseTo(FieldObject object, int distance){
		return object.getPosition().getDeltaDistance(getPosition()) < distance ;
	}
	
	@Override
	public String toString() {
		return "position=" + position + ", lastUpdateTime=" + lastUpdateTime + ", direction=" + direction + ", speed="
				+ speed + "]";
	}
	
	@Override
	public void paint(Graphics2D g2){
		FieldPoint position = (this.position == null ? new FieldPoint(0,0) : this.position ).mirrorY();
		paint(g2, position);
	}
	
	@Override
	public void paint(Graphics2D g2, FieldPoint position){
		g2.setFont(g2.getFont().deriveFont(22f));
		g2.drawString("?", (int) position.getX(), (int)position.getY());
	}
}
