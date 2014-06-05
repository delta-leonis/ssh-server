package model;

public abstract class Robot extends FieldObject {

	private int robotID;
	private boolean isKeeper;
	private int orientation;
	private float height;
	private double diameter;
	private Team team;
	private int batteryStatus;
	private long powerUpTime;
	private boolean onSight;

	public Robot(int robotID, boolean isKeeper, float height, double diameter, Team team) {
		super();
		this.robotID = robotID;
		this.isKeeper = isKeeper;
		this.height = height;
		this.diameter = diameter;
		this.team = team;
	}

	/**
	 * @return the robotID
	 */
	public int getRobotID() {
		return robotID;
	}

	/**
	 * @param robotID
	 *            the robotID to set
	 */
	public void setRobotID(int robotID) {
		this.robotID = robotID;
	}

	public void update(Point p, double updateTime, int orientation, int lastCamUpdateNo) {
		super.update(p, updateTime, lastCamUpdateNo);
//		if(robotID == 0xb){
//			System.out.println("LastUpdateTime: " + lastUpdateTime);
//			System.out.println("UpdateTime:     " + updateTime);
//			System.out.println();
//		}

		this.orientation = orientation;
	}

	public void update(Point p, double updateTime, int lastCamUpdateNo) {
		super.update(p, updateTime, lastCamUpdateNo);
	}

	/**
	 * @return the isKeeper
	 */
	public boolean isKeeper() {
		return isKeeper;
	}

	public boolean isOnSight() {
		return onSight;
	}

	public void setOnSight(boolean onSight) {
		this.onSight = onSight;
	}

	/**
	 * @param isKeeper
	 *            the isKeeper to set
	 */
	public void setKeeper(boolean isKeeper) {
		this.isKeeper = isKeeper;
	}

	/**
	 * @return the orientation
	 */
	public float getOrientation() {
		return orientation;
	}

	/**
	 * @param orientation
	 *            the orientation to set
	 */
	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	/**
	 * @return the height
	 */
	public float getHeight() {
		return height;
	}

	/**
	 * @param height
	 *            the height to set
	 */
	public void setHeight(float height) {
		this.height = height;
	}

	/**
	 * @return the diameter
	 */
	public double getDiameter() {
		return diameter;
	}

	/**
	 * @param diameter
	 *            the diameter to set
	 */
	public void setDiameter(double diameter) {
		this.diameter = diameter;
	}

	/**
	 * @return the team
	 */
	public Team getTeam() {
		return team;
	}

	/**
	 * @param team
	 *            the team to set
	 */
	public void setTeam(Team team) {
		this.team = team;
	}

	/**
	 * @return the batteryStatus
	 */
	public int getBatteryStatus() {
		return batteryStatus;
	}

	/**
	 * @return the powerUpTime
	 */
	public long getPowerUpTime() {
		return powerUpTime;
	}

	public void setBatteryStatus(int batteryStatus, long timestamp) {
		this.batteryStatus = batteryStatus;
		powerUpTime = timestamp;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "robotID=" + robotID + ", isKeeper=" + isKeeper + ", orientation=" + orientation + ", height=" + height
				+ ", diameter=" + diameter + ", " + super.toString() + "\r\n";
	}

}
