package robocup.model;

public class Goal {
	private Point frontLeft;
	private Point frontRight;
	private Point backLeft;
	private Point backRight;
	private int wallWidth; // Width of goal post. Goal post is outside the
							// defined goal area
	private int height;

	// _______________
	// |W|<<<<>>>>|W|
	// (W = wallWidth)

	public Goal(Point frontLeft, Point frontRight, Point backLeft, Point backRight, int wallWidth, int height) {
		this.frontLeft = frontLeft;
		this.frontRight = frontRight;
		this.backLeft = backLeft;
		this.backRight = backRight;
		this.wallWidth = wallWidth;
		this.height = height;

		System.out.println(height);
		System.out.println(wallWidth);
		System.out.println("FL: " + frontLeft + ", RF" + frontRight);
	}
	
	/**
	 * @return goal width excluding post width
	 */
	public int getWidth(){
		return (int) (Math.abs(frontLeft.getY() - frontRight.getY()));
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
	public Point getFrontLeft() {
		return frontLeft;
	}

	/**
	 * @param frontLeft the frontLeft to set
	 */
	public void setFrontLeft(Point frontLeft) {
		this.frontLeft = frontLeft;
	}

	/**
	 * @return the frontRight
	 */
	public Point getFrontRight() {
		return frontRight;
	}

	/**
	 * @param frontRight the frontRight to set
	 */
	public void setFrontRight(Point frontRight) {
		this.frontRight = frontRight;
	}

	/**
	 * @return the backLeft
	 */
	public Point getBackLeft() {
		return backLeft;
	}

	/**
	 * @param backLeft the backLeft to set
	 */
	public void setBackLeft(Point backLeft) {
		this.backLeft = backLeft;
	}

	/**
	 * @return the backRight
	 */
	public Point getBackRight() {
		return backRight;
	}

	/**
	 * @param backRight the backRight to set
	 */
	public void setBackRight(Point backRight) {
		this.backRight = backRight;
	}

	@Override
	public String toString() {
		return "Goal [frontLeft=" + frontLeft + ", frontRight=" + frontRight + ", backLeft=" + backLeft
				+ ", backRight=" + backRight + ", wallWidth=" + wallWidth + ", height=" + height + "]" + "\r\n";
	}

}
