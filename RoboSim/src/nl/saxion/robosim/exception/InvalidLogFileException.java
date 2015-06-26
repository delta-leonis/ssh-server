package nl.saxion.robosim.exception;

import java.io.IOException;

/**
 * Created by Fieldhof on 23-6-2015.
 */
public class InvalidLogFileException extends IOException {

    public InvalidLogFileException() { super(); }
    public InvalidLogFileException(String message) { super(message); }
    public InvalidLogFileException(String message, Throwable cause) { super(message, cause); }
    public InvalidLogFileException(Throwable cause) { super(cause); }

}
