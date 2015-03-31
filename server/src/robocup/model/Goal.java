package robocup.model;

public class Goal {
	private FieldPoint frontLeft;
	private FieldPoint frontRight;
	private FieldPoint backLeft;
	private FieldPoint backRight;
	private int wallWidth; // Width of goal post. Goal post is outside the
							// defined goal area
	private int height;

	// _______________
	// |W|<<<<>>>>|W|
	// (W = wallWidth)

	public Goal(FieldPoint frontLeft, FieldPoint frontRight, FieldPoint backLeft, FieldPoint backRight, int wallWidth, int height) {
		this.frontLeft = frontLeft;
		this.frontRight = frontRight;
		this.backLeft = backLeft;
		this.backRight = backRight;
		this.wallWidth = wallWidth;
		this.height = height;
	}
	
	/**
	 * @return goal width excluding post width
	 */
	public double getWidth(){
		return (Math.abs(frontLeft.getY() - frontRight.getY()));
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
	 * @return the frontLeft
	 */
	public FieldPoint getFrontLeft() {
		return frontLeft;
	}

	/**
	 * @param frontLeft the frontLeft to set
	 */
	public void setFrontLeft(FieldPoint frontLeft) {
		this.frontLeft = frontLeft;
	}

	/**
	 * @return the frontRight
	 */
	public FieldPoint getFrontRight() {
		return frontRight;
	}

	/**
	 * @param frontRight the frontRight to set
	 */
	public void setFrontRight(FieldPoint frontRight) {
		this.frontRight = frontRight;
	}

	/**
	 * @return the backLeft
	 */
	public FieldPoint getBackLeft() {
		return backLeft;
	}

	/**
	 * @param backLeft the backLeft to set
	 */
	public void setBackLeft(FieldPoint backLeft) {
		this.backLeft = backLeft;
	}

	/**
	 * @return the backRight
	 */
	public FieldPoint getBackRight() {
		return backRight;
	}

	/**
	 * @param backRight the backRight to set
	 */
	public void setBackRight(FieldPoint backRight) {
		this.backRight = backRight;
	}

	@Override
	public String toString() {
		return "Goal [frontLeft=" + frontLeft + ", frontRight=" + frontRight + ", backLeft=" + backLeft
				+ ", backRight=" + backRight + ", wallWidth=" + wallWidth + ", height=" + height + "]" + "\r\n";
	}

}
