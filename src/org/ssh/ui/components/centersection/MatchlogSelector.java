package org.ssh.ui.components.centersection;

import com.sun.xml.internal.ws.util.StringUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.ssh.managers.manager.Models;
import org.ssh.managers.manager.UI;
import org.ssh.models.Settings;
import org.ssh.ui.UIComponent;

import javafx.fxml.FXML;
import org.ssh.ui.components.bottomsection.Timeslider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A {@link UIComponent component} used to select log files.
 * It generates a list of files ending with *.log, which can then be used to play log files.
 * These log files are loaded into the {@link Timeslider}
 * @author Thomas Hakkers
 */
public class MatchlogSelector extends UIComponent {

    @FXML
    private VBox rootPane;

    @FXML
    private Button selectLog;

    @FXML
    private TableView<LogRow> tableView;

    @FXML
    private TableColumn<LogRow, String> matchColumn;

    @FXML
    private TableColumn<LogRow, String> dateColumn;

    @FXML
    private TableColumn<LogRow, String> timeColumn;

    @FXML
    private TableColumn<LogRow, String> rawColumn;

    private ObservableList<LogRow> items;

    private String logFolderPath = "";

    /**
     * Creates a new {@link MatchlogSelector}, also generates the file list.
     */
    public MatchlogSelector() {
        super("matchlogselector", "centersection/matchlogselector.fxml");

        tableView.minWidthProperty().bind(rootPane.widthProperty());
        tableView.maxWidthProperty().bind(rootPane.widthProperty());
        selectLog.minWidthProperty().bind(rootPane.widthProperty());
        selectLog.maxWidthProperty().bind(rootPane.widthProperty());

        items = FXCollections.observableArrayList ();

        Models.<Settings>get("settings").ifPresent(settings -> logFolderPath = settings.getLogFolder());

        loadFiles();

        dateColumn.setCellValueFactory(
                new PropertyValueFactory<>("date")
        );
        matchColumn.setCellValueFactory(
                new PropertyValueFactory<>("match")
        );
        timeColumn.setCellValueFactory(
                new PropertyValueFactory<>("time")
        );
        rawColumn.setCellValueFactory(
                new PropertyValueFactory<>("raw")
        );

        tableView.setItems(items);

        selectLog.setOnAction(action -> selectFile());
    }

    /**
     * Function that gets called when the button is pressed. Runs the selected file in the timeslider.
     */
    private void selectFile(){
        if(tableView.getSelectionModel().getSelectedItem() != null)
            UI.<Timeslider>find("timeslider").forEach(timeslider ->
                    timeslider.loadGameLog(tableView.getSelectionModel().getSelectedItem().getRaw()));
    }

    /**
     * Loads all files into the item list
     */
    private void loadFiles(){
        items.clear();
        try {
            Files.walk(Paths.get(System.getProperty("user.dir") + logFolderPath))
                    // Check whether it's a valid file
                    .filter(Files::isRegularFile)
                    .filter(file -> file.toFile().getAbsolutePath().endsWith(".log"))
                    // Load the log files one by one
                    .forEach(log ->
                            items.add(new LogRow(log.toFile().toPath()))
                    );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * A row in the {@link MatchlogSelector}
     */
    public class LogRow {
        private SimpleStringProperty date;
        private SimpleStringProperty time;
        private SimpleStringProperty match;
        private SimpleStringProperty raw;

        /**
         * Constructor for the {@link LogRow} that converts the gives filename into date, time and match name
         * @param path file name of the log
         */
        public LogRow(Path path){
            raw = new SimpleStringProperty(path.toAbsolutePath().toString());
            String notPrettyName = path.getFileName().toString();
            if(notPrettyName.length() > 18) {
                date = new SimpleStringProperty(notPrettyName.substring(0, 10));
                time = new SimpleStringProperty(notPrettyName.substring(11, 13) + ":" + notPrettyName.substring(13, 15) + ":" + notPrettyName.substring(15, 17));
                String[] match = notPrettyName.substring(18, notPrettyName.length() - 4).split("_");
                this.match = new SimpleStringProperty(StringUtils.capitalize(match[0]) + " vs " + StringUtils.capitalize(match[1]));
            }
            else{
                match = new SimpleStringProperty("Incorrect filename");
                date = new SimpleStringProperty("YYYY-MM-DD");
                time = new SimpleStringProperty("time in hhmmss");
            }
        }

        public String getDate() {
            return date.get();
        }

        public void setDate(String date) {
            this.date.set(date);
        }

        public String getTime() {
            return time.get();
        }

        public void setTime(String time) {
            this.time.set(time);
        }

        public String getMatch() {
            return match.get();
        }

        public void setMatch(String match) {
            this.match.set(match);
        }

        public String getRaw() {
            return raw.get();
        }
    }
}

