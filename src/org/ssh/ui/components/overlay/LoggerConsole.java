package org.ssh.ui.components.overlay;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.ssh.managers.manager.UI;
import org.ssh.ui.UIComponent;
import org.ssh.util.Logger;
import org.ssh.util.LoggerMemoryHandler;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.stream.Stream;

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
        List<String> tabNames = new ArrayList<>();
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

        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Add a tab to see all logging
        tabPane.getTabs().add(new Tab("all", new LoggingTab("all", Logger.getLogger("org.ssh")).getComponent()));
        // Add a tab for all packages in the list tabNames. These are all packages in org.ssh where
        // a Logger is present
        for (String tabName : tabNames)
            tabPane.getTabs().add(new Tab(tabName, new LoggingTab(tabName, Logger.getLogger("org.ssh." + tabName)).getComponent()));
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

            Optional<Handler> oHandler = Stream.of(logger.getHandlers())
                    //filter for the right listnener
                    .filter(handler -> handler instanceof LoggerMemoryHandler)
                    //return one
                    .findAny();

            //make sure it has been found
            if(!oHandler.isPresent()) {
                UIComponent.LOG.warning("Could not find LoggerMemoryHandler to bind to LoggerConsole for %s", logger.getName());
                return;
            }

            //filter data to contain everything
            FilteredList<LogTableRow> filteredData = new FilteredList<>(((LoggerMemoryHandler)oHandler.get()).getLogrecords(),
                    row -> Level.parse(row.getLevel()).intValue() >= Level.parse(levelDropdown.getValue()).intValue());

            // create a handler which will filter based on selected LEVEL in table
            levelDropdown.getSelectionModel().selectedItemProperty()
                    .addListener((observable, oldValue, newValue) ->
                filteredData.setPredicate(row -> Level.parse(row.getLevel()).intValue() >= Level.parse(newValue).intValue())
            );

            // wrap in a sortedlist
            SortedList<LogTableRow> sortedData = new SortedList<>(filteredData);
            // this way we can bind it to the comperator in #rowTableView
            sortedData.comparatorProperty().bind(rowTableView.comparatorProperty());

            // add the observable list to #rowTableView
            rowTableView.setItems(sortedData);
        }
    }
}