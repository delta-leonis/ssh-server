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
import java.util.logging.Logger;

import javafx.scene.layout.Pane;
import org.ssh.managers.manager.UI;
import org.ssh.ui.UIComponent;
import org.ssh.util.MaxSizeArrayList;


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
 *         
 */
public class LoggerConsole extends UIComponent {
    
    /**
     * {@link GridPane} that is the ruleTableView in the fxml file, is used for binding the height and width
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

        LoggingTab logtab = new LoggingTab("all", Logger.getLogger("org.ssh"));
        logtab.getComponent().setStyle("-fx-background-color: red;");
        tabPane.getTabs().add(new Tab("all", logtab.getComponent()));
//        tabPane.setStyle("-fx-background-color: red;");
        // Add a tab for all packages in the list tabNames. These are all packages in org.ssh where
        // a Logger is present
        for (int i = 0; i < tabNames.size(); i++) {
            LoggingTab tab = new LoggingTab(tabNames.get(i), Logger.getLogger("org.ssh." + tabNames.get(i)));
            tabPane.getTabs().add(new Tab(tabNames.get(i),
                    tab.getComponent()));
        }
    }
    
    /**
     * Class that describes a {@link Tab} in the {@link TabPane} of {@link LoggerConsole}. It
     * consists of a {@link TableView} where for every line of log data a {@link LogRule} can be
     * added.<br/>
     * Managing what log data is displayed is done by a {@link LoggerMemoryHandler}.
     * 
     * @see {@link LoggerMemoryHandler}.
     *      
     * @author Joost Overeem
     *         
     */
    protected class LoggingTab extends UIComponent<Pane> {
        
        /**
         * The {@link org.ssh.util.Logger} of this class.
         */
        private final org.ssh.util.Logger LOG = org.ssh.util.Logger.getLogger();

        @FXML
        private Pane rootPane;

        /**
         * The ruleTableView in the fxml file. {@link LogRule}s can be displayed in here.
         */
        @FXML
        private TableView<LogRule> ruleTableView;
                                    
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
         * {@link LogRule}s and adds a {@link LoggerMemoryHandler} to the {@link Logger} that
         * provides the {@link LogRule}s to be displayed.
         * 
         * @param logger
         *            {@link Logger} whose data is displayed.
         */
        public LoggingTab(String name, Logger logger) {
            // Call super constructor with right fxml file
            super("logtab " + name, "overlay/loggertab.fxml");

            UI.bindSize(ruleTableView, rootPane);
            // Make a memory handler for the logger.
            loggerHandler = new LoggerMemoryHandler(this);
            // Add the to the logger this tab displays
            logger.addHandler(loggerHandler);
            
            levelDropdown.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                loggerHandler.changeLogLevel(Level.parse(newValue));
            });
        }

        protected TableView getRuleTableView() {
            return ruleTableView;
        }

        /**
         * Empties the list of {@link LogRule}s that are displayed currently.
         */
        protected void flushLogRules() {
            // Get the displayed list
            ObservableList<LogRule> data = ruleTableView.getItems();
            // Clear it
            Platform.runLater(data::clear);
        }
        
        /**
         * Adds a {@link LogRule} to the {@link #ruleTableView table}.
         * 
         * @param record
         *            {@link LogRecord} to add to the {@link #ruleTableView table}.
         */
        protected void addLogRule(LogRecord record) {
            LogRule rule = new LogRule(
                    // with sequence number as id
                    (int) record.getSequenceNumber(),
                    // name of the log level
                    record.getLevel().getName(),
                    // name of the source class (not the whole path of course)
                    record.getSourceClassName(),
                    // the time in a legible format in stead of epoch
                    dateFormat(record.getMillis()),
                    // the message as description
                    record.getMessage());
            try {
                ruleTableView.getItems().add(rule);
            }
            catch (Exception exception) {
                LOG.exception(exception);
            }
            try {
                //Platform.runLater(() -> ruleTableView.sort());
            }
            catch (Exception exception) {
                LOG.exception(exception);
            }
        }
        
        /**
         * Function to format {@link System#currentTimeMillis()} to readable output.
         * 
         * @param millis
         *            Milliseconds since 1970 (normal Unix timestamp).
         * @return {@link String} with time in HH:mm:ss format.
         */
        public String dateFormat(long millis) {
            return (new SimpleDateFormat("HH:mm:ss")).format(new Date(millis));
        }
        
        /**
         * Class that describes a {@link LogRecord} in legible attributes. This class is used to
         * store as element in a list ({@link TableView}) of rows that are displayed as a table.
         * 
         * @author Joost Overeem
         *         
         */
        protected class LogRule {
            
            /**
             * The unique id (to be retrieved from the sequenceNumber of {@link LogRecord}).
             */
            private int    id;
            /**
             * The name of the {@link Level} the log is.
             */
            private String level;
            /**
             * The class name of the class that called the {@link Logger}.
             */
            private String location;
            /**
             * The time in a HH:mm:ss format.
             */
            private String time;
            /**
             * The description (message in {@link LogRecord}).
             */
            private String description;
                           
            /**
             * 
             * @param id
             *            Id as an int, from a {@link LogRecord} the
             *            {@link LogRecord#getSequenceNumber()}.
             * @param level
             *            {@link Level} in String, from a {@link LogRecord} the
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
            public LogRule(int id, String level, String location, String time, String description) {
                this.id = id;
                this.level = level;
                this.location = location;
                this.time = time;
                this.description = description;
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
    }
    
    /**
     * {@link Handler} that stores all published {@link LogRecord}s from a {@link Logger} in
     * {@link List}s of {@link LogRecord}s (one for every {@link Level}).<br />
     * The {@link LoggerMemoryHandler} manages a {@link LoggingTab} by calling its
     * {@link LoggingTab#flushLogRules()} and {@link LoggingTab#addLogRule(LogRecord)}.
     * 
     * @author Joost Overeem
     *         
     */
    public class LoggerMemoryHandler extends Handler {
        
        /**
         * The {@link org.ssh.util.Logger} of this class.
         */
        private final org.ssh.util.Logger LOG = org.ssh.util.Logger.getLogger();
                                              
        /**
         * The {@link LoggingTab} that is used to push records visualization to.
         */
        private LoggingTab                loggingTab;
                                          
        /**
         * {@link Level} at which a {@link LogRecord} should be pushed to the {@link LoggingTab}.
         */
        private Level                     pushLevel;
                                          
        /**
         * {@link List} of {@link LogRecord}s with the {@link Level} {@link Level#SEVERE}.
         */
        private List<LogRecord>           logrecordsSevere;
        /**
         * {@link List} of {@link LogRecord}s with the {@link Level} {@link Level#WARNING}.
         */
        private List<LogRecord>           logrecordsWarning;
        /**
         * {@link List} of {@link LogRecord}s with the {@link Level} {@link Level#INFO}.
         */
        private List<LogRecord>           logrecordsInfo;
        /**
         * {@link List} of {@link LogRecord}s with the {@link Level} {@link Level#CONFIG}.
         */
        private List<LogRecord>           logrecordsConfig;
        /**
         * {@link List} of {@link LogRecord}s with the {@link Level} {@link Level#FINE}.
         */
        private List<LogRecord>           logrecordsFine;
        /**
         * {@link List} of {@link LogRecord}s with the {@link Level} {@link Level#FINER}.
         */
        private List<LogRecord>           logrecordsFiner;
        /**
         * {@link List} of {@link LogRecord}s with the {@link Level} {@link Level#FINEST}.
         */
        private List<LogRecord>           logrecordsFinest;
                                          
        /**
         * Constructor for {@link LoggerMemoryHandler}. Initializes private {@link List}s as
         * {@link MaxSizeArrayList}s.
         */
        public LoggerMemoryHandler(LoggingTab loggingTab) {
            // Check if the argument is not null
            if (loggingTab != null) {
                this.loggingTab = loggingTab;
                // Initialize all the log records lists as MaxSizeArrayLists with the default max
                // size
                logrecordsSevere = new MaxSizeArrayList<LogRecord>();
                logrecordsWarning = new MaxSizeArrayList<LogRecord>();
                logrecordsInfo = new MaxSizeArrayList<LogRecord>();
                logrecordsConfig = new MaxSizeArrayList<LogRecord>();
                logrecordsFine = new MaxSizeArrayList<LogRecord>();
                logrecordsFiner = new MaxSizeArrayList<LogRecord>();
                logrecordsFinest = new MaxSizeArrayList<LogRecord>();
                // Set the pushLevel default on warning
                pushLevel = Level.INFO;
            }
            else {
                // if null, tell via the logger there is a null pointer
                LOG.warning("Nullpointer argument at LoggerMemoryHandler");
            }
        }
        
        /**
         * Adds the record to the correct list of log records
         * 
         * @param record
         */
        @Override
        public void publish(LogRecord record) {
            // Switch on the loglevel and then add it to the right list
            switch (record.getLevel().getName()) {
                case "FINEST":
                    logrecordsFinest.add(record);
                    break;
                case "FINER":
                    break;
                case "FINE":
                    logrecordsFine.add(record);
                    break;
                case "CONFIG":
                    logrecordsConfig.add(record);
                    break;
                case "INFO":
                    logrecordsInfo.add(record);
                    break;
                case "WARNING":
                    logrecordsWarning.add(record);
                    break;
                case "SEVERE":
                    logrecordsSevere.add(record);
                    break;
                default:
                    // Tell via the logger that an unknown level was queried
                    LOG.info("An unkown Logger Level was queried with getLogrecords(");
                    break;
            }
            // If the push level is equal or lower to the record level, we should add the record
            // to the displayed logs in the tab
            if (record.getLevel().intValue() >= pushLevel.intValue()) loggingTab.addLogRule(record);
        }
        
        /**
         * Clears all log records.
         */
        @Override
        public void flush() {
            // clear all the lists
            logrecordsSevere.clear();
            logrecordsWarning.clear();
            logrecordsInfo.clear();
            logrecordsConfig.clear();
            logrecordsFine.clear();
            logrecordsFiner.clear();
            logrecordsFinest.clear();
        }
        
        @Override
        public void close() throws SecurityException {
            // Nothing to close
        }
        
        /**
         * Changes the {@Link #pushLevel} and updates the data displayed by the {@link #loggingTab}
         * by flushing it and adding all the relevant log data.
         * 
         * @param newLevel
         *            The new {@link Level} of {@link LoggingTab.LogRule}s that is to be displayed
         *            by the {@link #loggingTab}.
         */
        public void changeLogLevel(Level newLevel) {
            // Set the new pushLevel
            pushLevel = newLevel;
            // Clear the current list of displayed logs
            loggingTab.flushLogRules();
            // Loop through the records that should be added
            for (LogRecord record : getLogrecords(newLevel)) {
                // and add them as a LogRule to the tab
                loggingTab.addLogRule(record);
            }
        }
        
        /**
         * Getter function for the lists of logging levels.
         * 
         * @param loglevel
         *            The {@link Level} of the {@link LogRecord}s you want to get.
         * @return {@link List} of {@link LogRecord}s of the {@link Level} your parameter is
         *         including the higher {@link Level}s.
         */
        public List<LogRecord> getLogrecords(Level loglevel) {
            // Make an ArrayList to place the LogRecords in
            ArrayList<LogRecord> result = new ArrayList<LogRecord>();
            // Prevent null pointers in the list of records
            if (loglevel != null) {
                // Switch on the name of the loglevel. No breaks are added so that the right level
                // including all the higher loglevels are added to the list
                switch (loglevel.getName()) {
                    case "FINEST":
                        result.addAll(logrecordsFinest);
                    case "FINER":
                        result.addAll(logrecordsFiner);
                    case "FINE":
                        result.addAll(logrecordsFine);
                    case "CONFIG":
                        result.addAll(logrecordsConfig);
                    case "INFO":
                        result.addAll(logrecordsInfo);
                    case "WARNING":
                        result.addAll(logrecordsWarning);
                    case "SEVERE":
                        result.addAll(logrecordsSevere);
                        break;
                    default:
                        // Tell via the logger that an unknown level was queried
                        LOG.info("An unkown Logger Level was queried with getLogrecords");
                        break;
                }
            }
            else {
                // Tell the logger there is a null pointer argument
                LOG.info("Nullpointer argument at getLogrecords");
            }
            // Return the list
            return result;
        }
    }
    
}
