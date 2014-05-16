package output;

/**
 * @author Erik Hubers, Gerbrand Bosch
 *
 */
public interface ComInterface {

	
	/**
	 * @param messageType, MessageType 0 => robot instructie
	 * @param robotID
	 * @param direction
	 * @param directionSpeed , degrees/sec
	 * @param travelDistance
	 * @param rotationAngle
	 * @param rotationSpeed
	 * @param kicker
	 * @param dribble
	 */
	public void send(int messageType, int robotID, int direction, int directionSpeed, int travelDistance,
			int rotationAngle, int rotationSpeed, int kicker, boolean dribble);

}
