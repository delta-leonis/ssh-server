package robocup.model;

/**
 * Represents a Robot on the {@link Field}.
 * This class is abstract, so make sure your Robot is either from the {@link Ally} or {@link Enemy} class.
 */
public abstract class Robot extends FieldObject {

	public static final int DIAMETER = 180;	//In millimeters.
	private int robotId;
	/**
	 * orientation is in degrees<br>
	 * directions for refference: 0 = EAST, 90 = NORTH, 180 = WEST, -90 = SOUTH
	 */
	private double orientation;
	private double height;
	/** 
	 * a robot is onsight when it is being seen by a camera
	 */
	private boolean onSight;
	private boolean visible = true;		//true = visible in the GUI

	/**
	 * Creates a new robot object
	 * @param robotID	reference ID for the robot
	 * @param height	height of the robot
	 */
	public Robot(int robotID,  double height) {
		super();
		this.robotId = robotID;
		this.height = height;
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
	 * @param orientation in degrees (0 = EAST, 90 = NORTH, 180 = WEST, -90 = SOUTH)
	 */
	public void update(FieldPoint newPosition, double updateTime, double orientation, int lastCamUpdateNo) {
		super.update(newPosition, updateTime, lastCamUpdateNo);
		this.orientation = orientation; // setOrientation(orientation)
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
	 * @return true if robot is visible on the {@link Field}
	 */
	public boolean isOnSight() {
		return onSight;
	}

	/**
	 * @param onSight robot visible on camera?
	 */
	public void setOnSight(boolean onSight) {
		this.onSight = onSight;
	}

	/**
	 * @return the orientation in degrees (0 = EAST, 90 = NORTH, 180 = WEST, -90 = SOUTH)
	 */
	public double getOrientation() {
		return orientation;
	}

	/**
	 * @param orientation in degrees (0 = EAST, 90 = NORTH, 180 = WEST, -90 = SOUTH)
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


	@Override
	public String toString() {
		return "robotID=" + robotId + ", isKeeper=" + (isKeeper() ? "true" : "false") + ", orientation=" + orientation + ", height=" + height
				+ ", diameter=" + DIAMETER + ", " + super.toString() + "\r\n";
	}
}
