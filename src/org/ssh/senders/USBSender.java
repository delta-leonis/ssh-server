package org.ssh.senders;

import org.ssh.util.Logger;

import org.ssh.models.enums.BaudRate;
import org.ssh.models.enums.SendMethod;

import com.google.protobuf.Message;

import jssc.SerialPort;
import jssc.SerialPortException;

/**
 * Implements {@link SendMethod SendMethod.USB}. Currently it has no purpose other than showcasing
 * the epic {@link SenderInterface}.
 *
 * @author Jeroen de Jong
 */
public class USBSender implements SenderInterface {
    
    /**
     * Logger for this specific class
     */
    private final static Logger LOG = Logger.getLogger();
    /**
     * Serialport that will be written to
     */
    private SerialPort          serialPort;
                                
    /**
     * Instansiates USBSender and opens the serial connection to the COM-port
     * 
     * @param portName
     *            name of the port (i.e. on windows: COM1, linux: /dev/ttyacm0)
     * @param baudRate
     */
    public USBSender(final String portName, final BaudRate baudRate) {
        try {
            // creating port
            this.serialPort = new SerialPort(portName);
            // opening port
            this.serialPort.openPort();
            // setting initial parameters for r/w later on
            this.serialPort.setParams(baudRate.getValue(),
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
        }
        catch (final SerialPortException spe) {
            // something broke, go fix it
            USBSender.LOG.severe(String.format("Could not open port %s", portName));
        }
    }
    
    @Override
    public boolean send(final Message genericMessage) {
        try {
            
            // check if the port is open at all
            if (!this.serialPort.isOpened()) {
                USBSender.LOG.warning("USB port is closed, can't send message");
                return false;
            }
            
            // write bytes to serialport
            this.serialPort.writeBytes(genericMessage.toByteArray());
            
        }
        catch (final SerialPortException spe) {
            USBSender.LOG.warning("Could not send message over USB");
            return false;
        }
        
        // here it is safe to assume everything went fine
        USBSender.LOG.info("Send message over USB");
        return true;
    }
    
    /**
     * {@inheritDoc} Close the used com-port.
     */
    @Override
    public boolean unregister() {
        try {
            // check if the port is open to begin with
            if (!this.serialPort.isOpened()) {
                USBSender.LOG.info("Serialport was closed before");
                return true;
            }
            
            return this.serialPort.closePort();
            
        }
        catch (final SerialPortException e) {
            USBSender.LOG.warning("Can't close serial connection");
            return false;
        }
    }
    
}
