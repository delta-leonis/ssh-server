package input;

import input.protobuf.MessagesRobocupSslWrapper.SSL_WrapperPacket;
import input.protobuf.Referee.SSL_Referee;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.LinkedTransferQueue;

/**
 * Handles creation and buffer of MessageFrame objects.
 * 
 * @author Erik Hubers, Gerbrand Bosch
 * 
 */
public class ProtoParser {

	private static ProtoParser instance;
	private LinkedTransferQueue<Object> inputBuffer;

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
	 * parse a byte array to a detection or a geometry object
	 * 
	 * @param data
	 */
	public void parseVision(ByteArrayInputStream data) {
		
		try {
			SSL_WrapperPacket wrapper = SSL_WrapperPacket.parseFrom(data);
			if (wrapper.hasDetection()) {
				
				addObject(wrapper.getDetection());
			} else if (wrapper.hasGeometry()) {
				addObject(wrapper.getGeometry());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Parse a byte array to a referee message object
	 * 
	 * @param input
	 *            ByteArrayInputStream with UDP packet
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
	 * @return MessageFrame object as Object
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
	 * @param Object
	 */
	public void addObject(Object o) {
		inputBuffer.add(o);
	}
}
