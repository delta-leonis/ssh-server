package robocup.output;

/**
 * @author Erik Hubers, Gerbrand Bosch
 * TODO: Suggestion: Turn into Interface (instead of abstract class) and move singleton to {@link RobotCom}
 */
public abstract class ComInterface {

	private static ComInterface instance;

	@SuppressWarnings("rawtypes")
	public static ComInterface getInstance(Class comInterface) {
		if (instance == null) {
			try {
				instance = (ComInterface) comInterface.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return instance;
	}

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
	public abstract void send(int messageType, int robotID, int direction, int directionSpeed, int travelDistance,
			int rotationAngle, int rotationSpeed, int kicker, boolean dribble);

}
