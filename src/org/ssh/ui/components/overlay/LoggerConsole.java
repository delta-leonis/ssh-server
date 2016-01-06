package org.ssh.ui.components.overlay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.layout.Pane;
import org.ssh.managers.manager.UI;
import org.ssh.ui.UIComponent;
import org.ssh.util.Logger;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;

/**
 * Class for displaying data of the {@link Logger}. It consists of a {@link TabPane} where several
 * {@link LoggingTab}s are added to (for every package in org.ssh one and one for showing everything
 * together).
 * 
 * @author Joost Overeem
 * @author Jeroen de Jong
 *         
 */
public class LoggerConsole extends UIComponent {
    
    /**
     * {@link GridPane} that is the rowTableView in the fxml file, is used for binding the height and width
     * properties.
     */
    @FXML
    private GridPane rootPane;
                     
    /**
     * {@link TabPane} to place all the logging tabs in.
     */
    @FXML
    private TabPane  tabPane;
                     
    /**
     * Constructor for {@link LoggerConsole}. Search for all packages in org.ssh and makes a
     * {@link LoggingTab} for each package as displayed group of {@link Logger}s.
     */
    public LoggerConsole() {
        // Call super constructor with right fxml file
        super("loggerconsole", "overlay/loggerconsole.fxml");
        
        // Make a list to store the tabNames in while searching all the tabNames
        List<String> tabNames = new ArrayList<String>();
        // Get an enumeration with all the names of loggers
        Enumeration<String> loggerNames = LogManager.getLogManager().getLoggerNames();
        // Loop through all the names to search for the names of the packages in org.ssh
        while (loggerNames.hasMoreElements()) {
            // Get the name of the logger
            String p = loggerNames.nextElement();
            // Check if it starts with org.ssh.
            if (p.matches("^org\\.ssh\\.(.*?)$")) {
                // Take the word after org.ssh. as the tabName
                String tabName = p.split("\\.", 4)[2];
                // Check if there is already such a tabName in the list, add it when it is not there
                if (!tabNames.stream().filter(name -> name.equals(tabName)).findFirst().isPresent())
                    tabNames.add(tabName);
            }
        }
        // Add a tab to see all logging
        tabPane.getTabs().add(new Tab("all", new LoggingTab("all", Logger.getLogger("org.ssh")).getComponent()));
        // Add a tab for all packages in the list tabNames. These are all packages in org.ssh where
        // a Logger is present
        for (String tabname : tabNames)
            tabPane.getTabs().add(new Tab(tabname, new LoggingTab(tabname, Logger.getLogger("org.ssh." + tabname)).getComponent()));
    }
    
    /**
     * Class that describes a {@link Tab} in the {@link TabPane} of {@link LoggerConsole}. It
     * consists of a {@link TableView} where for every line of log data a {@link LogTableRow} can be
     * added.<br/>
     * Managing what log data is displayed is done by a {@link LoggerMemoryHandler}.
     * 
     * @see {@link LoggerMemoryHandler}.
     *      
     * @author Joost Overeem
     * @author Jeroen de Jong
     *         
     */
    protected class LoggingTab extends UIComponent<Pane> {

        @FXML
        private Pane rootPane;

        /**
         * The rowTableView in the fxml file. {@link LogTableRow}s can be displayed in here.
         */
        @FXML
        private TableView<LogTableRow> rowTableView;
                                    
        /**
         * {@link ChoiceBox} to choose the {@link Level} with that should be displayed.
         */
        @FXML
        private ChoiceBox<String>   levelDropdown;
                                    
        /**
         * {@link LoggerMemoryHandler} for handling the logging and managing what {@link Level} of
         * logging is displayed.
         */
        private LoggerMemoryHandler loggerHandler;
                                    
        /**
         * Constructor for {@link LoggingTab}. Makes the tab existing of a {@link TableView} with
         * {@link LogTableRow}s and adds a {@link LoggerMemoryHandler} to the {@link Logger} that
         * provides the {@link LogTableRow}s to be displayed.
         * 
         * @param logger
         *            {@link Logger} whose data is displayed.
         */
        public LoggingTab(String name, Logger logger) {
            // Call super constructor with right fxml file
            super("logtab " + name, "overlay/loggertab.fxml");

            UI.bindSize(rowTableView, rootPane);
            // Make a memory handler for the logger.
            loggerHandler = new LoggerMemoryHandler();
            // Add the to the logger this tab displays
            logger.addHandler(loggerHandler);

            //filter data to contain everything
            FilteredList<LogTableRow> filteredData = new FilteredList<>(loggerHandler.getLogrecords(),
                    p -> true);

            // create a handler which will filter based on selected LEVEL in table
            levelDropdown.getSelectionModel().selectedItemProperty()
                    .addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(row -> Level.parse(row.getLevel()).intValue() >= Level.parse(newValue).intValue());
            });

            // wrap in a sortedlist
            SortedList<LogTableRow> sortedData = new SortedList<>(filteredData);
            // this way we can bind it to the comperator in #rowTableView
            sortedData.comparatorProperty().bind(rowTableView.comparatorProperty());

            // add the observable list to #rowTableView
            rowTableView.setItems(sortedData);
        }
    }

    /**
     * Class that describes a {@link LogRecord} in legible attributes. This class is used to
     * store as element in a list ({@link TableView}) of rows that are displayed as a table.
     *
     * @author Joost Overeem
     * @author Jeroen de Jong
     */
    protected class LogTableRow {
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
         * @param time
         *            Time, from a {@link LogRecord} the {@link LogRecord#getMillis()}, but then
         *            in a HH:mm:ss format.
         * @param description
         *            Description, from a {@link LogRecord} the {@link LogRecord#getMessage()}.
         */
        public LogTableRow(int id, String level, String location, String time, String description) {
            this.id = id;
            this.level = level;
            this.location = location;
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
    /**
     * {@link Handler} that stores all published {@link LogRecord}s from a {@link Logger} in
     * {@link List}s of {@link LogRecord}s (one for every {@link Level}).<br />
     *
     * @author Joost Overeem
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
         * Adds the record to the correct list of log records
         *
         * @param record
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

}
