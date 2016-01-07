package org.ssh.ui.components.overlay;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import org.ssh.util.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Class that describes a {@link LogRecord} in legible attributes. This class is used to
 * store as element in a list ({@link TableView}) of rows that are displayed as a table.
 *
 * @author Joost Overeem
 * @author Jeroen de Jong
 */
public class LogTableRow {
    /**
     * Last called method
     */
    @FXML
    private String methodname;

    /**
     * The unique id (to be retrieved from the sequenceNumber of {@link LogRecord}).
     */
    @FXML
    private int    id;
    /**
     * {@link Level} name of the log.
     */
    @FXML
    private String level;
    /**
     * The class name of the class that called the {@link Logger}.
     */
    @FXML
    private String location;
    /**
     * The time in a HH:mm:ss format.
     */
    @FXML
    private String time;
    /**
     * The description (message in {@link LogRecord}).
     */
    @FXML
    private String description;

    /**
     *
     * @param id
     *            Id as an int, from a {@link LogRecord} the
     *            {@link LogRecord#getSequenceNumber()}.
     * @param level
     *            {@link Level} from a {@link LogRecord} the
     *            {@link LogRecord#getLevel()}.
     * @param location
     *            Location, from a {@link LogRecord} the
     *            {@link LogRecord#getSourceClassName()}.
     * @param methodname
     *            Last called method
     * @param time
     *            Time, from a {@link LogRecord} the {@link LogRecord#getMillis()}, but then
     *            in a HH:mm:ss format.
     * @param description
     *            Description, from a {@link LogRecord} the {@link LogRecord#getMessage()}.
     */
    public LogTableRow(int id, String level, String location, String methodname, String time, String description) {
        this.id = id;
        this.level = level;
        this.location = location;
        this.methodname = methodname;
        this.time = time;
        this.description = description;
    }

    /**
     * Instansiates a new LogTableRow based on information in given {@link LogRecord}
     *
     * @param record record to parse
     */
    public LogTableRow(LogRecord record){
        this((int) record.getSequenceNumber(),
                // name of the log level
                record.getLevel().getName(),
                // name of the source class (not the whole path of course)
                record.getSourceClassName(),
                record.getSourceMethodName(),
                // the time in a legible format in stead of epoch
                (new SimpleDateFormat("HH:mm:ss")).format(new Date(record.getMillis())),
                // the message as description
                record.getMessage());
    }

    /**
     *
     * @return The unique id (to be retrieved from the sequenceNumber of {@link LogRecord}).
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return The name of the {@link Level} the log is.
     */
    public String getLevel() {
        return level;
    }

    /**
     *
     * @return The class name of the class that called the {@link Logger}.
     */
    public String getLocation() {
        return location;
    }

    /**
     *
     * @return The time in a HH:mm:ss format.
     */
    public String getTime() {
        return time;
    }

    /**
     *
     * @return The description (message in {@link LogRecord}).
     */
    public String getDescription() {
        return description;
    }
}