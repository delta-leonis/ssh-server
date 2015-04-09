package robocup.output;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Properties;

public class RobotCom extends ComInterface {
	private DatagramSocket serverSocket;
	private InetAddress ipAddress;
	private int port;

	public RobotCom() {

		final Properties configFile = new Properties();
		try {
			configFile.load(new FileInputStream("config/config.properties"));
			String ipAddress = configFile.getProperty("outputAddress");
			int port = Integer.parseInt(configFile.getProperty("ownTeamOutputPort"));
			try {
				this.ipAddress = InetAddress.getByName(ipAddress);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.port = port;
			try {
				serverSocket = new DatagramSocket();
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			
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
	 * {@inheritDoc}
	 */
	@Override
	public void send(int messageType, int robotID, int direction, int directionSpeed, 
			int rotationSpeed, int shootKicker, boolean dribble) {

		// use LOGGER instead of this
		// if(robotID == 0xB || robotID == 0x3){
		// System.out.println("Message:");
		// System.out.println("Robot: " + robotID);
		// System.out.println("Direction: " + direction);
		// System.out.println("DirectionSpeed: " + directionSpeed);
		// System.out.println("travelDistance: " + travelDistance);
		// System.out.println("rotationAngle: " + rotationAngle);
		// System.out.println("rotationSpeed: " + rotationSpeed);
		// System.out.println("shootKicker: " + shootKicker);
		// System.out.println("dribble: " + dribble);
		// }
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
