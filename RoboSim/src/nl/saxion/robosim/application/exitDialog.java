package nl.saxion.robosim.application;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import nl.saxion.robosim.controller.UIController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class exitDialog extends Stage implements Initializable {

    private Parent p;
    private    UIController uiCont;

    public exitDialog(Parent parent, UIController uiCont) throws Exception {
        p = parent;
        this.uiCont=uiCont;
        this.initStyle(StageStyle.UNDECORATED);
        this.initModality(Modality.WINDOW_MODAL);
        this.initOwner(parent.getScene().getWindow());

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/dialog.fxml"));
        fxmlLoader.setController(this);

        try {
            setScene(new Scene((Parent) fxmlLoader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }


    public void exitDialog() {
        this.close();
    }

    public void exitApplication() {
        uiCont.aiStop();
        Platform.exit();
    }
}

