package robocup.model;

public abstract class FieldObject {

	private Point positionCam0;
	private Point positionCam1;
	private Point position;
	//LastUpdateTime = time off the day in sec
	protected double lastUpdateTime;
	private int direction;
	private double speed;
//	private int lastCamUpdateNo;

	public FieldObject() {
		lastUpdateTime = 0;
		position = null;
		positionCam0 = null;
		positionCam1 = null;
//		lastCamUpdateNo = -1;
	}

	/**
	 * Updates FieldObject
	 * 
	 * @param updateTime
	 *            Update timestamp
	 * @param newPosition
	 *            New positiont point.
	 * @post Updated position, direction and speed.
	 */
	public void update(Point newPosition, double updateTime, int camUpdateNo) {
		double newTime = updateTime;
		
		//System.out.println(newPosition);
		Point tmpPosition;
		if (!correctCamSide(newPosition.getX(), newPosition.getY(), camUpdateNo)) {
			if (camUpdateNo == 0 && positionCam1 == null){
				;
			} else if (camUpdateNo == 1 && positionCam0 == null){
				;
			} else if (positionCam0 == null && positionCam1 == null){
				;
			} else{
				;
			}
		}
		if (camUpdateNo == 0) {
			positionCam0 = new Point(newPosition.getX(), newPosition.getY());
			//System.out.println(camUpdateNo + "# " + positionCam0.getX());
		} 
		if (camUpdateNo == 1) {
			positionCam1 = new Point(newPosition.getX(), newPosition.getY());
			//System.out.println(camUpdateNo + "# " + positionCam1.getX());
		}
		
		

//		if(this instanceof Ball){
		if(World.getInstance().getField().getCameraOverlapZoneWidth() > Math.abs(newPosition.getX())){
			if ((positionCam0 != null && positionCam1 != null)) {
				
				float newX = positionCam0.getX();
				float newY = positionCam0.getY();
				if(Math.abs(positionCam1.getX()) > Math.abs(newX) && Math.abs(positionCam1.getY()) > Math.abs(newY) )
				{
					newX = positionCam1.getX();
					newY = positionCam1.getY();
				}
				tmpPosition = new Point(newX, newY);
			} else {
				tmpPosition = newPosition;
			}
		} else {
			tmpPosition = newPosition;
		}

		
//		lastCamUpdateNo = camUpdateNo;
		if(position != null){
			setDirection(tmpPosition);
			setSpeed(newTime, tmpPosition);
		}
		
		position = tmpPosition;
		
		lastUpdateTime = newTime;			
	}

	public boolean correctCamSide(float x, float y, int camNo) {
		if (x > 0 && camNo == 1)
			return true;
		if (x < 0 && camNo == 0)
			return true;
		return false;
	}

	/**
	 * @param direction
	 *            the direction to set
	 */
	public void setDirection(Point newPosition) {
		if(position != null){
			double deltaDistance = position.getDeltaDistance(newPosition);

			if (deltaDistance > 1.5) {
				direction = position.getAngle(newPosition);
				//System.out.println(direction);
			}
		}
	}

	/**
	 * @return the direction
	 */
	public float getDirection() {
		return direction;
	}

	/**
	 * Calculates speed of FieldObject using
	 * 
	 * @param updateTime
	 * @param newPosition
	 */
	private void setSpeed(double updateTime, Point newPosition) {
		double deltaDistance = position.getDeltaDistance(newPosition);
		double deltaTime = updateTime - lastUpdateTime;
		if (deltaDistance > 1.5) {
			speed = Math.abs((deltaDistance / deltaTime));// Ik hoop dat dit
															// gaat werken
		}
	}

	/**
	 * @return the speed
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * @return the lastUpdateTime
	 */
	public double getLastUpdateTime() {
		return lastUpdateTime;
	}

	/**
	 * @param lastUpdateTime
	 *            the lastUpdateTime to set
	 */
	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	/**
	 * @return the position
	 */
	public Point getPosition() {
		return position;
	}

	/**
	 * @param position
	 *            the position to set
	 */
	public void setPosition(Point position) {
		this.position = position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "position=" + position + ", lastUpdateTime=" + lastUpdateTime + ", direction=" + direction + ", speed="
				+ speed + "]";
	}

}
