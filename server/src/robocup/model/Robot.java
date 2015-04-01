package robocup.model;


/**
 * Represents a Robot on the {@link Field}.
 * This class is abstract, so make sure your Robot is either from the {@link Ally} or {@link Enemy} class.
 */
public abstract class Robot extends FieldObject {

	public static final int DIAMETER = 180;	//In millimeters.
	private int robotId;
	private boolean isKeeper;
	private double orientation;
	private double height;
	private long powerUpTime;
	private boolean onSight;
	private boolean visible;		//true = visible in the GUI


	public Robot(int robotID, boolean isKeeper, double height) {
		super();
		this.robotId = robotID;
		this.isKeeper = isKeeper;
		this.height = height;
		this.visible = true;
	}

	/**
	 * @return if the robot should be visible on the GUI
	 */
	public boolean isVisible(){
		return visible;
	}
	
	/**
	 * @param whether to show the robot 
	 */
	public void setVisible(boolean _visible){
		visible = _visible;
	}
	/**
	 * @return the robotID
	 */
	public int getRobotId() {
		return robotId;
	}

	/**
	 * @param robotId the robotID to set
	 */
	public void setRobotID(int robotId) {
		this.robotId = robotId;
	}

	/**
	 * {@inheritDoc}
	 * @param degrees 
	 */
	public void update(FieldPoint newPosition, double updateTime, double degrees, int lastCamUpdateNo) {
		super.update(newPosition, updateTime, lastCamUpdateNo);
		this.orientation = degrees; // setOrientation(orientation)
	}

	/**
	 * {@inheritDoc}
	 */
	public void update(FieldPoint p, double updateTime, int lastCamUpdateNo) {
		super.update(p, updateTime, lastCamUpdateNo);
	}

	/**
	 * @return whether this Robot is a keeper or not.
	 */
	public boolean isKeeper() {
		if(World.getInstance().getReferee().getAlly().getGoalie() == this.robotId)
			return true;

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
	public double getOrientation() {
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
	public double getHeight() {
		return height;
	}

	/**
	 * Height of the Robot in Millimeters. Retrieved from {@link robocup.controller.handlers.protohandlers.DetectionHandler DetectionHandler}
	 * @param height the height to set for the {@link Robot}
	 */
	public void setHeight(double height) {
		this.height = height;
	}

	/**
	 * TODO: Document. Is this the time it takes to fully charge the Robot? If so, is the value in seconds or milliseconds?
	 * @return the powerUpTime
	 */
	public long getPowerUpTime() {
		return powerUpTime;
	}


	@Override
	public String toString() {
		return "robotID=" + robotId + ", isKeeper=" + isKeeper + ", orientation=" + orientation + ", height=" + height
				+ ", diameter=" + DIAMETER + ", " + super.toString() + "\r\n";
	}
}
