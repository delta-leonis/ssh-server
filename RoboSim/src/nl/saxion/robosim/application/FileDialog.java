package nl.saxion.robosim.application;


import java.io.File;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import nl.saxion.robosim.controller.Renderer;
import nl.saxion.robosim.model.LogReader;
import nl.saxion.robosim.model.Model;
import nl.saxion.robosim.model.Settings;

import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.ProgressDialog;



/**
 * Created by Damon on 3-6-2015.
 */
public class FileDialog {
    private final Renderer renderer;
    Settings settings;
    Button fileButton, acceptButton, cancelButton;
    CheckBox yellowBox, blueBox;
    TextArea fileloc;
    Model model;
    Dialog dlg;
    Stage stage;
    GridPane grid;

    public FileDialog(Renderer renderer) {
        this.renderer = renderer;
        model = Model.getInstance();
    }


    /**
     * Starts the filepicker dialog
     * @param stage the stage on which to show the dialog
     */
    public void startDialog(Stage stage) {
        this.stage = stage;
        dlg = new Dialog(stage, "Settings");
        //dlg = Dialogs.create().styleClass(Dialog.STYLE_CLASS_UNDECORATED).masthead("select log file and teams");



        grid = new GridPane();

        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 10));

        //create widgets
        fileButton   = new Button("Choose file");
        fileloc      = new TextArea();
        fileloc.setMaxHeight(50);
        fileloc.setMaxWidth(300);
        fileloc.setEditable(false);
        fileloc.setText("");
        acceptButton = new Button("Accept");
        acceptButton.setDisable(true);
        cancelButton = new Button("Cancel");
        yellowBox  = new CheckBox();
        blueBox    = new CheckBox();


        acceptButton.setStyle("-fx-background-color: #5cb808; -fx-background-radius: 0; -fx-text-fill: #ffffff; -fx-padding: 15px 25px;");
        cancelButton.setStyle("-fx-background-color: #101010; -fx-background-radius: 0; -fx-text-fill: #ffffff; -fx-padding: 15px 25px;");
        fileButton.setStyle("-fx-background-color: #5cb808; -fx-background-radius: 0; -fx-text-fill: #ffffff; -fx-padding: 15px 25px;");

        //add widgets to grid.
        grid.add(fileButton, 0, 0);
        grid.add(yellowBox, 1, 2);
        grid.add(new Label("Team Yellow?"), 0, 2);
        grid.add(blueBox, 1, 3);
        grid.add(new Label("Team blue?"), 0, 3);
        grid.add(acceptButton, 0,4);
        grid.add(cancelButton, 4, 4);

        dlg.setMasthead("Select log file and team(s)");

        dlg.getMasthead().setStyle("-fx-background-color: #101010;   -fx-text-fill: #ffffff;");
        dlg.getMasthead().lookup(".label").setStyle("  -fx-text-fill: #ffffff;");
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setMinHeight(200);
        grid.setMinWidth(200);
        grid.add(fileloc, 1 , 0);



        //onclick events
        acceptButton.setOnAction(event -> accept());
        cancelButton.setOnAction(event -> dlg.hide());
        yellowBox.setOnAction(event -> checkInput());
        blueBox.setOnAction(event -> checkInput());
        fileButton.setOnAction(event -> openFile());



        dlg.setResizable(false);
       // dlg.setContent(grid);

        dlg.setContent(grid);
//        dlg = DialogStyle.style(dlg, grid);
        dlg.show();
    }

    /**
     * Open file chooser
     */
    private void openFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select a .log File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TEXT files (*.log)", "*.log");
        chooser.getExtensionFilters().add(extFilter);


        File f = chooser.showOpenDialog((Stage)dlg.getContent().getScene().getWindow());
        if(f != null) {
            fileloc.setText(f.getPath());
        }
        checkInput();
    }

    /**
     * Called when the accept button is pressed
     */
    private void accept() {
        Settings settings = Settings.getInstance();
        settings.setSimulationSettings(blueBox.isSelected(), yellowBox.isSelected(), fileloc.getText());
        dlg.hide();
        loadingDialog(fileloc.getText());
    }

    /**
     * Checks wether all input fields have data.
     */
    private void checkInput() {
        System.out.println(yellowBox.isSelected() + " - " + blueBox.isSelected() + " - " + fileloc.getText().length());
        if((yellowBox.isSelected() || blueBox.isSelected()) && fileloc.getText().length() > 0) {
            acceptButton.setDisable(false);
        } else {
            acceptButton.setDisable(true);
        }


    }

    /**
     * Starts the logreader in a Service, and shows a loading dialog while this is busy.
     * @param file The filename to the log which needs to be read.
     */
    private void loadingDialog(String file) {

        Service<Void> service = new Service<Void>() {

            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws InterruptedException {
                        new LogReader(file);
                        return null;
                    }
                };
            }
        };


        ProgressDialog dlg = new ProgressDialog(service);
        dlg.initOwner(stage);
        dlg.setTitle("");
        dlg.setHeaderText("Please wait, reading log");

        service.start();
    }
}



