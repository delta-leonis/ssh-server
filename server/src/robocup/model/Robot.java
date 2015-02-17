package robocup.model;

import robocup.controller.ai.highLevelBehavior.forcebehavior.Mode;

/**
 * Represents a Robot on the {@link Field}.
 * This class is abstract, so make sure your Robot is either from the {@link Ally} or {@link Enemy} class.
 */
public abstract class Robot extends FieldObject {

	public static final int DIAMETER = 180;	//In millimeters.
	
	private int robotID;
	private boolean isKeeper;
	private int orientation;
	private float height;
	private Team team;
	private float batteryStatus;
	private long powerUpTime;
	private boolean onSight;
	private Mode.roles role;

	public Robot(int robotID, boolean isKeeper, float height, Team team) {
		super();
		this.robotID = robotID;
		this.isKeeper = isKeeper;
		this.height = height;
		this.team = team;
		this.role = null;
	}

	/**
	 * @return the robotID
	 */
	public int getRobotID() {
		return robotID;
	}

	/**
	 * @param robotID the robotID to set
	 */
	public void setRobotID(int robotID) {
		this.robotID = robotID;
	}

	/**
	 * {@inheritDoc}
	 * @param orientation 
	 */
	public void update(Point p, double updateTime, int orientation, int lastCamUpdateNo) {
		super.update(p, updateTime, lastCamUpdateNo);
		this.orientation = orientation; // setOrientation(orientation)
	}

	/**
	 * {@inheritDoc}
	 */
	public void update(Point p, double updateTime, int lastCamUpdateNo) {
		super.update(p, updateTime, lastCamUpdateNo);
	}

	/** 
	 * Set a role (keeper/defender/attacker/etc)
	 * @param role A role from the enumaration Mode.roles
	 */
	public void setRole(Mode.roles role) {
		// System.out.println(" set role " + role);
		this.role = role;
	}

	/**
	 * Get the assigned role (keeper/defender/attacker/null/etc)
	 * @return A role from the enumaration Mode.roles
	 */
	public Mode.roles getRole() {
		return role;
	}

	/**
	 * @return whether this Robot is a keeper or not.
	 */
	public boolean isKeeper() {
		if(World.getInstance().getAlly().getGoalie() == this.robotID) return true;
			return false;
	}

	/**
	 * TODO: what the hell is this
	 * @return
	 */
	public boolean isOnSight() {
		return onSight;
	}

	/**
	 * TODO: Document
	 * @param onSight
	 */
	public void setOnSight(boolean onSight) {
		this.onSight = onSight;
	}

	/**
	 * @param isKeeper the isKeeper to set
	 * @deprecated use {@link #setRole()}
	 */
	public void setKeeper(boolean isKeeper) {
		this.isKeeper = isKeeper;
	}

	/**
	 * TODO: Document. What is the orientation? The direction the Robot is looking at? If so, is this value in degrees or radians?
	 * @return the orientation
	 */
	public float getOrientation() {
		return orientation;
	}

	/**
	 * TODO: Document. What is the orientation? The direction the Robot is looking at? If so, is this value in degrees or radians?
	 * @param orientation the orientation to set
	 */
	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	/**
	 * Height of the Robot in Millimeters. Retrieved from {@link robocup.controller.handlers.protohandlers.DetectionHandler DetectionHandler}
	 * @return the height of the Robot in Millimeters
	 */
	public float getHeight() {
		return height;
	}

	/**
	 * Height of the Robot in Millimeters. Retrieved from {@link robocup.controller.handlers.protohandlers.DetectionHandler DetectionHandler}
	 * @param height the height to set for the {@link Robot}
	 */
	public void setHeight(float height) {
		this.height = height;
	}

	/**
	 * @return the team this {@link Robot} belongs to.
	 */
	public Team getTeam() {
		return team;
	}

	/**
	 * Sets what {@link Team} this {@link Robot} belongs to.
	 * @param team: The {@link Team} this {@link Robot} belongs to.
	 */
	public void setTeam(Team team) {
		this.team = team;
	}

	/**
	 * TODO: Document. Is this the percentage of battery left on the Robot?
	 * @return the batteryStatus
	 */
	public float getBatteryStatus() {
		return batteryStatus;
	}

	/**
	 * TODO: Document. Is this the time it takes to fully charge the Robot? If so, is the value in seconds or milliseconds?
	 * @return the powerUpTime
	 */
	public long getPowerUpTime() {
		return powerUpTime;
	}

	/**
	 * @param batteryStatus TODO: Document. Is this the percentage of battery left on the Robot?
	 * @param timestamp TODO: Document. Is this the time it takes to fully charge the Robot? If so, is the value in seconds or milliseconds?
	 */
	public void setBatteryStatus(int batteryStatus, long timestamp) {
		this.batteryStatus = batteryStatus;
		powerUpTime = timestamp;
	}

	/**
	 * TODO: Document. Is the batteryStatus supposed to be in integers like in {@link Robot#setBatteryStatus(int, long)} or is it supposed to be a long?
	 * @param batteryStatus
	 */
	public void setBatteryStatus(float batteryStatus) {
		this.batteryStatus = batteryStatus;
	}

	@Override
	public String toString() {
		return "robotID=" + robotID + ", isKeeper=" + isKeeper + ", orientation=" + orientation + ", height=" + height
				+ ", diameter=" + DIAMETER + ", " + super.toString() + "\r\n";
	}
}
