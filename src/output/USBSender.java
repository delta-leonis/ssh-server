package output;

import java.util.logging.Logger;

import com.google.protobuf.Message;

import jssc.SerialPort;
import jssc.SerialPortException;
import model.enums.BaudRate;

/**
 * Implements {@link SendMethod SendMethod.USB}. Currently it has no purpose other than showcasing the epic {@link SenderInterface}. 
 * 
 * @author Jeroen
 */
public class USBSender implements SenderInterface{
	/**
	 * Logger for this specific class
	 */
	private Logger 			logger = Logger.getLogger(USBSender.class.toString());
	/**
	 * Serialport that will be written to
	 */
	private SerialPort 		serialPort;
	
	/**
	 * Instansiates USBSender and opens the serial connection to the COM-port
	 * @param portName	name of the port (i.e. on windows: COM1, linux: /dev/ttyacm0)
	 * @param baudRate	
	 */
	public USBSender(String portName, BaudRate baudRate){
		try{
			//creating port
			serialPort = new SerialPort(portName);
			//opening port
			serialPort.openPort();
			//setting initial parameters for r/w later on
			serialPort.setParams(
					baudRate.getValue(), 
					SerialPort.DATABITS_8, 
					SerialPort.STOPBITS_1, 
					SerialPort.PARITY_NONE
			);
		} catch(SerialPortException spe){
			//something broke, go fix it
			logger.severe(String.format("Could not open port %s", portName));
		}
	}
	
	@Override
	public boolean send(Message genericMessage) {
		try{

			//check if the port is open at all
			if(!serialPort.isOpened()){
				logger.warning("USB port is closed, can't send message");
				return false;
			}

			//write bytes to serialport
			serialPort.writeBytes(genericMessage.toByteArray());

		}catch(SerialPortException spe){
			logger.warning("Could not send message over USB");
			return false;
		}
		
		//here it is safe to assume everything went fine
		logger.info("Send message over USB");
		return true;
	}

	/**
	 * {@inheritDoc}
	 * Close the used com-port.
	 */
	@Override
	public boolean unregister() {
		try {
			//check if the port is open to begin with
			if(!serialPort.isOpened()){
				logger.info("Serialport was closed before");
				return true;
			}
		
			return serialPort.closePort();

		} catch (SerialPortException e) {
			logger.warning("Can't close serial connection");
			return false;
		}
	}

}
