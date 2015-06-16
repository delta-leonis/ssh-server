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
 * Singleton class whose instance allows to communicate to the basestation and thus to the individual robots on the field
 */
public class ComInterface {
	private DatagramSocket serverSocket;
	private InetAddress ipAddress;
	private int port;

	private static ComInterface instance;

	/**
	 * @return the singleton instance of {@link ComInterface}
	 */
	public static ComInterface getInstance() {
		if (instance == null) {
			instance = new ComInterface();
		}
		return instance;
	}
	
	/**
	 * Constructs ComInterface by loading properties from 'config/config.properties'
	 */
	protected ComInterface(){
		final Properties configFile = new Properties();
		try {
			configFile.load(new FileInputStream("config/config.properties"));
			ipAddress = InetAddress.getByName(configFile.getProperty("outputAddress"));
			port = Integer.parseInt(configFile.getProperty("ownTeamOutputPort"));;
			serverSocket = new DatagramSocket();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Sending a message to the basestation,<br>
	 * mostly used with messagetype 127 (highest signed 8_int) for setting a new channel frequency
	 *
	 * @param messageType describes type of message
	 * @param channel_freq new channel frequency
	 */
	public void send(int messageType, int channel_freq) {
		send(messageType, 0, channel_freq,0,0,0,false);
	}

	/**
	 * @param messageType MessageType 0 => robot instruction
	 * @param robotID The ID of the robot we want to send to.
	 * @param direction The direction we want our robot to move towards.
	 * @param directionSpeed The speed we want our robot to move at in mm/s
	 * @param rotationSpeed The speed we want our robot to turn at in mm/s
	 * @param kicker -1 to -100 for chipping, 1 to 100 for kicking.
	 * @param dribble true to start the dribbler, false otherwise
	 */
	public void send(int messageType, int robotID, int direction, int directionSpeed, 
			int rotationSpeed, int shootKicker, boolean dribble) {
		byte[] dataPacket = createByteArray(messageType, robotID, direction, directionSpeed, rotationSpeed, (int)(shootKicker*0.8), dribble);
		DatagramPacket sendPacket = new DatagramPacket(dataPacket, dataPacket.length, ipAddress, port);

		try {
			serverSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param messageType, MessageType 0 => robot instruction
	 * @param robotID The ID of the robot we want to send to.
	 * @param direction The direction we want our robot to move towards.
	 * @param directionSpeed The speed we want our robot to move at in mm/s
	 * @param rotationSpeed The speed we want our robot to turn at in mm/s
	 * @param kicker -1 to -100 for chipping, 1 to 100 for kicking.
	 * @param dribble true to start the dribbler, false otherwise
	 * @return bytearray to send to the Basestation
	 */
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
	 * calculate checksum and appends it to the {@link ByteBuffer}
	 * 
	 * @param bytebuffer array that will be modified
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
