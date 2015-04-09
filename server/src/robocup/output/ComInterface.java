package robocup.output;

/**
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
	 * @param messageType, MessageType 0 => robot instruction
	 * @param robotID The ID of the robot we want to send to.
	 * @param direction The direction we want our robot to move towards.
	 * @param directionSpeed The speed we want our robot to move at in mm/s
	 * @param rotationSpeed The speed we want our robot to turn at in mm/s
	 * @param kicker -1 to -100 for chipping, 1 to 100 for kicking.
	 * @param dribble true to start the dribbler, false otherwise
	 */
	public abstract void send(int messageType, int robotID, int direction, int directionSpeed,
			int rotationSpeed, int kicker, boolean dribble);

	/**
	 * 
	 * @param messageType	should be 0x7f (127)
	 * @param channelID		robotFrequency
	 */
	public abstract void send(int messageType, int channelID);
}
