package robocup.output;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Properties;

/**
 * TODO: Suggestion: Turn into Interface (instead of abstract class) and move singleton to {@link RobotCom}
 */
public class ComInterface {
	private DatagramSocket serverSocket;
	private InetAddress ipAddress;
	private int port;

	private static ComInterface instance;

	@SuppressWarnings("rawtypes")
	public static ComInterface getInstance() {
		if (instance == null) {
			instance = new ComInterface();
		}
		return instance;
	}
	
	protected ComInterface(){
		final Properties configFile = new Properties();
		try {
			configFile.load(new FileInputStream("config/config.properties"));
			String ipAddress = configFile.getProperty("outputAddress");
			int port = Integer.parseInt(configFile.getProperty("ownTeamOutputPort"));
				this.ipAddress = InetAddress.getByName(ipAddress);
			this.port = port;
				serverSocket = new DatagramSocket();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Sending a message with only one argument,
	 * mostly used with messagetype 127 (highest signed 8_int) for setting a new channel frequency
	 *
	 * @param messageType
	 * @param channel_freq
	 */
	public void send(int messageType, int channel_freq) {
		send(messageType, 0, channel_freq,0,0,0,false);
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
	public void send(int messageType, int robotID, int direction, int directionSpeed, 
			int rotationSpeed, int shootKicker, boolean dribble) {
		byte[] dataPacket = createByteArray(messageType, robotID, direction, directionSpeed, rotationSpeed, shootKicker, dribble);
		DatagramPacket sendPacket = new DatagramPacket(dataPacket, dataPacket.length, ipAddress, port);

		try {
			serverSocket.send(sendPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private byte[] createByteArray(int messageType, int robotID, int direction, int directionSpeed,
			int rotationSpeed, int shootKicker, boolean dribble) {
		ByteBuffer dataBuffer = ByteBuffer.allocate(15);
		dataBuffer.order(ByteOrder.LITTLE_ENDIAN);
		dataBuffer.put((byte) messageType);
		dataBuffer.put((byte) robotID);
		dataBuffer.putShort((short) direction);
		dataBuffer.putShort((short) directionSpeed);
		dataBuffer.putShort((short) rotationSpeed);
		dataBuffer.put((byte) shootKicker);
		if (dribble) {
			dataBuffer.put((byte) 1);
		} else {
			dataBuffer.put((byte) 0);
		}
		addChecksum(dataBuffer);
		return dataBuffer.array();
	}

	/**
	 * calculate checksum and add to array
	 * 
	 * @param array
	 * @return
	 */
	private void addChecksum(ByteBuffer bytebuffer) {
		int checksum = 0;
		for (int i = 0; i < bytebuffer.capacity() - 1; i++) {
			checksum ^= bytebuffer.get(i);
		}
		checksum &= 0xff;
		bytebuffer.put((byte) checksum);
	}
}
