package robocup.model;

/**
 * Describes a goal on the {@link Field}
 */
public class Goal {
	private FieldPoint frontNorth;
	private FieldPoint frontSouth;
	private FieldPoint backNorth;
	private FieldPoint backSouth;
	private double wallWidth; //Goal posts are outside the defined goal area
	private double height;

	/**
	 * @param frontNorth	most northern point of the goal facing the field
	 * @param frontSouth	most southern point of the goal facing the field
	 * @param backNorth		most northern point of the goal facing away from the field
	 * @param backSouth		most southern point of the goal facing away from the field
	 * @param wallWidth		width of the posts
	 * @param height		height of the goal
	 */
	public Goal(FieldPoint frontNorth, FieldPoint frontSouth, FieldPoint backNorth, FieldPoint backSouth, double wallWidth, double height) {
		this.frontNorth = frontNorth;
		this.frontSouth = frontSouth;
		this.backNorth = backNorth;
		this.backSouth = backSouth;
		this.wallWidth = wallWidth;
		this.height = height;
	}
	
	/**
	 * @return goal width excluding post width
	 */
	public double getWidth(){
		return frontNorth.getY() - frontSouth.getY();
	}

	/**
	 * @return the width of the posts in millimeters
	 */
	public double getWallWidth() {
		return wallWidth;
	}

	/**
	 * Sets the width of the posts.
	 * @param wallWidth The width of the posts in millimeters
	 */
	public void setWallWidth(double wallWidth) {
		this.wallWidth = wallWidth;
	}

	/**
	 * @return the height of the goal in millimeters.
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * Sets the height of the goal.
	 * @param height The height of the goal in millimeters.
	 */
	public void setHeight(double height) {
		this.height = height;
	}

	/**
	 * @return the most northern point of the goal facing the field
	 */
	public FieldPoint getFrontNorth() {
		return frontNorth;
	}

	/**
	 * @param frontNorth the most northern point of the goal facing the field
	 */
	public void setFrontNorth(FieldPoint frontNorth) {
		this.frontNorth = frontNorth;
	}

	/**
	 * @return the most southern point of the goal facing the field
	 */
	public FieldPoint getFrontSouth() {
		return frontSouth;
	}

	/**
	 * @param frontSouth the most southern point of the goal facing the field
	 */
	public void setFrontSouth(FieldPoint frontSouth) {
		this.frontSouth = frontSouth;
	}

	/**
	 * @return the most northern point of the goal facing away from the field
	 */
	public FieldPoint getBackNorth() {
		return backNorth;
	}

	/**
	 * @param backNorth the most northern point of the goal facing away from the field
	 */
	public void setBackNorth(FieldPoint backNorth) {
		this.backNorth = backNorth;
	}

	/**
	 * @return the most southern point of the goal facing away from the field
	 */
	public FieldPoint getBackSouth() {
		return backSouth;
	}

	/**
	 * @param backSouth the most southern point of the goal facing away from the field
	 */
	public void setBackSouth(FieldPoint backSouth) {
		this.backSouth = backSouth;
	}

	@Override
	public String toString() {
		return "Goal [frontNorth=" + frontNorth + ", frontSouth=" + frontSouth + ", backNorth=" + backNorth
				+ ", backSouth=" + backSouth + ", wallWidth=" + wallWidth + ", height=" + height + "]" + "\r\n";
	}

}
