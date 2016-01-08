package org.ssh.util;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.ssh.ui.components.overlay.LogTableRow;

import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * {@link Handler} that stores all published {@link LogRecord}s from a {@link Logger} in
 * {@link List}s of {@link LogRecord}s (one for every {@link Level}).<br />
 *
 * @author Joost Overeem
 * @author Jeroen de Jong
 *
 */
public class LoggerMemoryHandler extends Handler {

    /**
     * List of all records
     */
    private ObservableList<LogTableRow> records;

    /**
     * Constructor for {@link LoggerMemoryHandler}.
     */
    public LoggerMemoryHandler() {
        records = FXCollections.observableArrayList();
    }

    /**
     * Converts the {@link LogRecord} to a {@link LogTableRow} and stores it in a list
     *
     * @param record description of the log event. A null record is
     *                 silently ignored and is not published
     */
    @Override
    public void publish(LogRecord record) {
        Platform.runLater(() ->
                records.add(new LogTableRow(record)));
    }

    /**
     * Clears all log records.
     */
    @Override
    public void flush() {
        Platform.runLater(records::clear);
    }

    @Override
    public void close() throws SecurityException {
        // Nothing to close
    }

    /**
     * @return reference to observable list containing all {@link LogTableRow}s.
     */
    public ObservableList<LogTableRow> getLogrecords(){
        return records;
    }
}