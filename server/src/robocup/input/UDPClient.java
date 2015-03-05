package robocup.input;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Logger;

import robocup.Main;

/**
 * abstract class UDPClient.
 * method startListining has to be overridden
 */
public abstract class UDPClient implements Runnable {

	protected ProtoParser protoParser;
	private String host;
	private int port;
	private MulticastSocket multicastSocket;
	private InetAddress group;
	private static Logger LOGGER = Logger.getLogger(Main.class.getName());

	public UDPClient(String host, int port) {
		protoParser = ProtoParser.getInstance();
		init(host, port);
	}

	/**
	 * Init
	 * @param host
	 * @param port
	 */
	public void init(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * connect when the Thread is started.
	 * When something fails the Thread is stopped.
	 */
	@Override
	public void run() {
		try {
			connect();
			startListening();
		} catch (IOException e) {
			e.printStackTrace();
			disconnect();
		}
	}

	/**
	 * this method has to be override
	 * @throws IOException 
	 */
	public abstract void startListening() throws IOException;

	public void connect() throws IOException {
		group = InetAddress.getByName(host);
		multicastSocket = new MulticastSocket(port);
		multicastSocket.joinGroup(group);
	}

	/**
	 * Leave the multicast group
	 */
	public void disconnect() {
		try {
			multicastSocket.leaveGroup(group);
		} catch (IOException e) {
			LOGGER.warning("Disconnecting not possible:");
			LOGGER.warning(e.toString());
		}
	}

	/**
	 * receive an UDP packet
	 * @return
	 * @throws IOException
	 */
	public ByteArrayInputStream receive() throws IOException {
		byte[] buf = new byte[1024];
		DatagramPacket recv = new DatagramPacket(buf, buf.length);
		multicastSocket.receive(recv);
		ByteArrayInputStream input = new ByteArrayInputStream(buf, 0, recv.getLength());
		return input;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host
	 *            the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

}
