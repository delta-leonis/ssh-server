package examples;

import javafx.application.Application;
import javafx.stage.Stage;
import org.ssh.managers.manager.Models;
import org.ssh.managers.manager.Services;
import org.ssh.ui.components.centersection.gamescene.GameSceneCamera;

/**
 * @author Jeroen
 * @date 26-1-2016
 */
public class test extends Application {

    public static void main(final String[] args) {
        /** java fx start **/
        Application.launch(args);
    }

    /*
     * (non-Javadoc)
     *
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    public void start(final Stage primaryStage) throws Exception {
        Services.start();
        Models.start();

        Models.create(GameSceneCamera.class).save();
    }
}
