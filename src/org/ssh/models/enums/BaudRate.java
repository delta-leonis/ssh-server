package org.ssh.models.enums;
/**
 * Represents different aviable baudrates for use with {@link SerialPort}<br>
 * I'm deeply sorry for the getValue() implementation. But i won't change it, because i'm stupid.
 * 
 * @author Jeroen
 *
 */
public enum BaudRate {
	BAUD_115200 ,
	BAUD_57600 ,
	BAUD_56000 ,
	BAUD_38400 ,
	BAUD_19200 ,
	BAUD_14400 ,
	BAUD_9600 ,
	BAUD_4800 ,
	BAUD_2400 ,    
	BAUD_1200;

	/**
	 * @return the value linked to the BaudRate 
	 */
	public int getValue(){
		//sorry man
		//het is gewoon kut
		//sorry okee
		return Integer.valueOf(this.toString().substring(5));
	}
}
