package robocup.model;

import java.awt.geom.Rectangle2D;

/**
 * Represents a Robot on the {@link Field}.
 * This class is abstract, so make sure your Robot is either from the {@link Ally} or {@link Enemy} class.
 */
public abstract class Robot extends FieldObject {

	public static final int DIAMETER = 180;	//In millimeters.
	private int robotId;
	/**
	 * Orientation is in degrees<br>
	 * Directions for reference: 0 = EAST, 90 = NORTH, 180 = WEST, -90 = SOUTH
	 */
	private double orientation;
	private double height;
	/** 
	 * A robot is on sight if the camera currently detects it.
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
	 * @return whether the robot should be visible on the GUI
	 */
	public boolean isVisible(){
		return visible;
	}
	
	/**
	 * @param True if the robot should be visible in the GUI, false otherwise.
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
	
	/**
	 * Used by {@link DijkstraPathPlanner}
	 * @param DISTANCE_TO_ROBOT The maximum distance we need to keep from this {@link Robot}
	 * @return A {@link Rectangle2D} which signifies 
	 */
	public Rectangle2D getDangerRectangle(int DISTANCE_TO_ROBOT){
		double x = getPosition().getX();
		double y = getPosition().getY();
		double actualDistance = DISTANCE_TO_ROBOT;
		return new Rectangle2D.Double(x - actualDistance, y - actualDistance, actualDistance*2, actualDistance*2);
	}


	@Override
	public String toString() {
		return "robotID=" + robotId + ", isKeeper=" + (isKeeper() ? "true" : "false") + ", orientation=" + orientation + ", height=" + height
				+ ", diameter=" + DIAMETER + ", " + super.toString() + "\r\n";
	}
}
