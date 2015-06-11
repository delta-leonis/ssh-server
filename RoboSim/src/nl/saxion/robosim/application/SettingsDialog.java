package nl.saxion.robosim.application;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import nl.saxion.robosim.model.LogReader;
import nl.saxion.robosim.model.Model;
import nl.saxion.robosim.model.Settings;
import org.controlsfx.dialog.Dialog;

import java.awt.*;
import java.io.File;

/**
 * Created by Damon on 4-6-2015.
 */
public class SettingsDialog {
    Settings settings;
    TextArea outputIp, inputIp;
    TextArea outputPort, inputPort;
    Button cancelButton, acceptButton;
    Model model;
    Stage stage;
    Dialog dlg;

    public SettingsDialog() {
        model = Model.getInstance();
    }

    /**
     * Starts a settings dialog
     * @param stage the stage on which to show the dialog.
     */
    public void startDialog(Stage stage) {
        Settings settings = Settings.getInstance();
        this.stage = stage;
        dlg = new Dialog(stage, "Settings");
        dlg.setResizable(false);
        //input fields
        inputPort = new TextArea();
        inputPort.setPromptText("Port");
        inputPort.setText(settings.getIport());
        inputPort.setMaxHeight(30);
        inputPort.setMaxWidth(100);
        inputIp = new TextArea();
        inputIp.setPromptText("Ip-address");
        inputIp.setText(settings.getIip());
        inputIp.setMaxHeight(30);
        inputIp.setMaxWidth(200);
        outputIp = new TextArea();
        outputIp.setPromptText("Ip-address");
        outputIp.setText(settings.getOip());
        outputIp.setMaxHeight(30);
        outputIp.setMaxWidth(200);
        outputPort = new TextArea();
        outputPort.setPromptText("port");
        outputPort.setText(settings.getOport());
        outputPort.setMaxWidth(100);
        outputPort.setMaxHeight(30);


        GridPane grid = new GridPane();
        //add input fields to grid
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 10));
        grid.add(new Label("Incoming communication"), 0, 0);
        grid.add(inputIp, 0, 1);
        grid.add(inputPort, 1, 1);
        grid.add(new Label("Outgoing communication"), 0, 2);
        grid.add(outputIp,0,3);
        grid.add(outputPort,1,3);

        //grid.setMaxHeight(400);
        acceptButton = new Button("Accept");
        acceptButton.setDisable(true);
        cancelButton = new Button("Cancel");
        dlg.setMasthead("Communication settings");

        grid.add(acceptButton,2,4);
        grid.add(cancelButton,0,4);
        //onclick
        //onchange listeners, call checkInput
        inputIp.textProperty().addListener((observable, oldValue, newValue) -> {
            checkInput();
        });
        outputIp.textProperty().addListener((observable, oldValue, newValue) -> {
           checkInput();
        });
        inputPort.textProperty().addListener((observable, oldValue, newValue) -> {
            checkInput();
        });
        outputPort.textProperty().addListener((observable, oldValue, newValue) -> {
            checkInput();
        });

        acceptButton.setOnAction(event -> accept());
        cancelButton.setOnAction(event -> dlg.hide());
        dlg.setContent(grid);
        dlg.setResizable(false);
        checkInput();

        dlg.show();
    }

    /**
     * gets called when the accept button is pressed
     */
    private void accept() {
        Settings settings = Settings.getInstance();
        settings.setCommunicationSettings(inputIp.getText(), inputPort.getText(),outputIp.getText(),outputPort.getText());
        dlg.hide();
    }

    /**
     * Checks wether all input fields have data.
     */
    private void checkInput() {
        if(inputIp.getText().length() > 0 && inputPort.getText().length() > 0 && outputIp.getText().length() > 0 && outputPort.getText().length() > 0) {
            acceptButton.setDisable(false);
        } else {
            acceptButton.setDisable(true);
        }
    }


}






