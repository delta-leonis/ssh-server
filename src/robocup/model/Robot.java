package robocup.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import robocup.controller.ai.movement.DijkstraPathPlanner;
import robocup.controller.ai.movement.GotoPosition;

/**
 * Represents a Robot on the {@link Field}.
 * This class is abstract, so make sure your Robot is either from the {@link Ally} or {@link Enemy} class.
 */
public abstract class Robot extends FieldObject implements Drawable {

	public static final int DIAMETER = 180;	//In millimeters.
	private int robotId;
	/**
	 * Orientation is in degrees<br>
	 * Directions for reference: 0 = EAST, 90 = NORTH, 180 = WEST, -90 = SOUTH
	 */
	private double orientation;
	private double height;

	private boolean visible = true;		//true = visible in the GUI
	private boolean ignore;

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
	 * 
	 * @param object	object to compare to
	 * @param distance	maximum distance to object
	 * @param angle		maximum difference in angle to object
	 * @return			true when object is close
	 */
	public boolean isCloseTo(FieldObject object, int distance, int angle){
		return Math.abs(
				Math.abs(getOrientation()) - Math.abs(getPosition().getAngle(object.getPosition()))) < angle
				&& getPosition().getDeltaDistance(object.getPosition()) < distance;
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
	 * @param direction 
	 * @param speed 
	 */
	public void update(FieldPoint newPosition, long updateTime, double orientation, double speed, double direction) {
		position = newPosition;
		lastUpdateTime = updateTime;
		this.orientation = orientation;
		this.speed = speed;
		this.direction = direction;
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
	 * If this robot is ignored, then it won't take part in the pathplanning.
	 * @param ignore
	 */
	public void setIgnore(boolean ignore){
		this.ignore = ignore;
	}
	
	public boolean getIgnore(){
		return ignore;
	}
	
	/**
	 * Used by {@link DijkstraPathPlanner}
	 * @param minDistance The maximum distance we need to keep from this {@link Robot}
	 * @return A {@link Rectangle2D} which signifies 
	 */
	public Ellipse2D getDangerEllipse(int minDistance, int maxDistance){
		double x = getPosition().getX();
		double y = getPosition().getY();
		double actualDistance = minDistance + ((getSpeed() *(maxDistance - minDistance)) / (GotoPosition.MAX_VELOCITY/1000));
		return new Ellipse2D.Double(x - actualDistance, y - actualDistance, actualDistance*2, actualDistance*2);
	}


	@Override
	public String toString() {
		return "robotID=" + robotId + ", isKeeper=" + (isKeeper() ? "true" : "false") + ", orientation=" + orientation + ", height=" + height
				+ ", diameter=" + DIAMETER + ", " + super.toString() + "\r\n";
	}
	
	public void paint(Graphics2D g2, FieldPoint position){
		g2.setStroke(new BasicStroke((int) (Robot.DIAMETER*0.10)));
		double robotOrientation = getOrientation();
		double leftX = position.getX() + Math.cos(Math.toRadians(-robotOrientation + 45.0)) * Robot.DIAMETER / 2.0 ;
		double leftY = position.getY() + Math.sin(Math.toRadians(-robotOrientation + 45.0)) * Robot.DIAMETER / 2.0 ;
		double rightX = position.getX() + Math.cos(Math.toRadians(-robotOrientation - 45.0)) * Robot.DIAMETER / 2.0 ;
		double rightY = position.getY() + Math.sin(Math.toRadians(-robotOrientation - 45.0)) * Robot.DIAMETER / 2.0 ;

		//SOLID COLOR
		g2.fillArc(
				(int) (position.getX() - (double) (Robot.DIAMETER / 2) ),
				(int) (position.getY() - (double) (Robot.DIAMETER / 2) ),
				(int) (Robot.DIAMETER ), (int) (Robot.DIAMETER ),
				(int) getOrientation() + 35, 295);
		g2.fillPolygon(new int[] {(int) rightX, (int) leftX, (int) position.getX()},
				new int[] {(int) rightY, (int) leftY , (int)position.getY()}, 3);

		//BORDERS
		g2.setColor(g2.getColor().darker());
		g2.drawLine((int) (leftX),
				(int) (leftY),
				(int) (rightX),
				(int) (rightY));

		// draw round part of robot
		g2.drawArc(
				(int) (position.getX() - (double) (Robot.DIAMETER / 2) ),
				(int) (position.getY()- (double) (Robot.DIAMETER / 2) ),
				(int) (Robot.DIAMETER ), (int) (Robot.DIAMETER ),
				(int) robotOrientation + 45, 270);

		g2.setColor(Color.BLACK);
		g2.setFont(new Font(g2.getFont().getFontName(), Font.BOLD, (int) (Robot.DIAMETER)/2));
		g2.drawString("" + getRobotId(), (int) position.getX() -(getRobotId()/10 + 1)*(g2.getFont().getSize()/3),
				(int) position.getY() +(g2.getFont().getSize()/3));
	}
}