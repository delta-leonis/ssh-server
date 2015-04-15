package robocup.model;

/**
 * Describes a goal on the {@link Field}
 */
public class Goal {
	private FieldPoint frontNorth;
	private FieldPoint frontSouth;
	private FieldPoint backNorth;
	private FieldPoint backSouth;
	private int wallWidth; //Goal posts are outside the defined goal area
	private int height;

	/**
	 * @param frontNorth	most north point on facing the field
	 * @param frontSouth	most south point on facing the field
	 * @param backNorth		most north point on facing away from the field
	 * @param backSouth		most south point on facing away from the field
	 * @param wallWidth		width of the posts
	 * @param height		height of the goal
	 */
	public Goal(FieldPoint frontNorth, FieldPoint frontSouth, FieldPoint backNorth, FieldPoint backSouth, int wallWidth, int height) {
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
	 * @return the wallWidth
	 */
	public int getWallWidth() {
		return wallWidth;
	}

	/**
	 * @param wallWidth the wallWidth to set
	 */
	public void setWallWidth(int wallWidth) {
		this.wallWidth = wallWidth;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * @return the frontNorth
	 */
	public FieldPoint getFrontNorth() {
		return frontNorth;
	}

	/**
	 * @param frontNorth the frontNorth to set
	 */
	public void setFrontNorth(FieldPoint frontNorth) {
		this.frontNorth = frontNorth;
	}

	/**
	 * @return the frontSouth
	 */
	public FieldPoint getFrontSouth() {
		return frontSouth;
	}

	/**
	 * @param frontSouth the frontSouth to set
	 */
	public void setFrontSouth(FieldPoint frontSouth) {
		this.frontSouth = frontSouth;
	}

	/**
	 * @return the backNorth
	 */
	public FieldPoint getBackNorth() {
		return backNorth;
	}

	/**
	 * @param backNorth the backNorth to set
	 */
	public void setBackNorth(FieldPoint backNorth) {
		this.backNorth = backNorth;
	}

	/**
	 * @return the backSouth
	 */
	public FieldPoint getBackSouth() {
		return backSouth;
	}

	/**
	 * @param backSouth the backSouth to set
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
