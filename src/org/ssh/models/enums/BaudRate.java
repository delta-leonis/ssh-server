package org.ssh.models.enums;

import jssc.SerialPort;

/**
 * Represents different available baud rates for use with {@link SerialPort}
 *
 * @author Jeroen de Jong
 */
public enum BaudRate {
    BAUD_115200(115200),
    BAUD_57600(57600),
    BAUD_56000(56000),
    BAUD_38400(38400),
    BAUD_19200(19200),
    BAUD_14400(14400),
    BAUD_9600(9600),
    BAUD_4800(4800),
    BAUD_2400(2400),
    BAUD_1200(1200);
    
    /**
     * Intvalue linked to this baudvalue
     */
    private int rate;
    
    /**
     * Construct a enum with a given rate
     * 
     * @param rate
     *            rate linked to baudname
     */
    BaudRate(int rate) {
        this.rate = rate;
    }
    
    /**
     * @return the value linked to the BaudRate
     */
    public int getValue() {
        return rate;
    }
}
