package nl.saxion.robosim.application;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.controlsfx.dialog.Dialog;

/**
 * Created by Damon on 11-6-2015.
 */
public class DialogStyle {

    public static Dialog style(Dialog dialog, GridPane grid) {

        dialog.getStyleClass().add(Dialog.STYLE_CLASS_UNDECORATED);

        for (int i = 0; i < grid.getChildren().size(); i++) {

            if (grid.getChildren().get(i) instanceof Label) {
                grid.getChildren().get(i).setStyle("-fx-text-fill: #ffffff;");

            }

        }


        dialog.setContent(grid);
        dialog.getContent().setStyle("-fx-background-color: #545458;");


        return dialog;
    }
}
