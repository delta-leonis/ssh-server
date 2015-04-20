package robocup.input;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.LinkedTransferQueue;

import robocup.input.protobuf.MessagesRobocupSslWrapper.SSL_WrapperPacket;
import robocup.input.protobuf.Referee.SSL_Referee;

/**
 * Handles creation and buffer of MessageFrame objects.
 */
public class ProtoParser {

	private static ProtoParser instance;
	private LinkedTransferQueue<Object> inputBuffer;

	/**
	 * Instantiates protoparser by creating a {@link LinkedTransferQueue}
	 */
	private ProtoParser() {
		inputBuffer = new LinkedTransferQueue<Object>();
	}

	public static ProtoParser getInstance() {
		if (instance == null) {
			instance = new ProtoParser();
		}
		return instance;
	}

	/**
	 * Parse a byte array to a detection or a geometry object
	 * 
	 * @param data : {@link ByteArrayInputStream} of data received from {@link SSLVisionClient}
	 */
	public void parseVision(ByteArrayInputStream data) {

		try {
			SSL_WrapperPacket wrapper = SSL_WrapperPacket.parseFrom(data);
			if (wrapper.hasDetection()) {
				addObject(wrapper.getDetection());
			}
			if (wrapper.hasGeometry()) {
				addObject(wrapper.getGeometry());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Parse a byte array to a referee message object
	 * 
	 * @param input A {@link ByteArrayInputStream} with UDP packet received from {@link RefereeClient}
	 */
	public void parseReferee(ByteArrayInputStream input) {
		try {
			SSL_Referee referee = SSL_Referee.parseFrom(input);
			addObject(referee);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns Head of message inputBuffer in FIFO manner
	 * 
	 * @return MessageFrame object as Object. May return null.
	 */
	public Object getHeadObject() {
		Object retValue = null;
		try {
			retValue = inputBuffer.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return retValue;
	}

	/**
	 * Add Object to inputBuffer
	 * 
	 * @param Object The Object we wish to add to the inputBuffer.
	 */
	public void addObject(Object o) {
		inputBuffer.add(o);
	}
}
