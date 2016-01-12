package org.ssh.ui.windows;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.ssh.field3d.FieldGame;
import org.ssh.ui.UIComponent;
import org.ssh.ui.UIController;
import org.ssh.ui.components.bottomsection.BottomSection;
import org.ssh.ui.components.centersection.CenterSection;
import org.ssh.ui.components.overlay.LoggerConsoleOverlay;
import org.ssh.ui.components.overlay.ProfilemenuOverlay;
import org.ssh.ui.components.topsection.TopSection;

/**
 * The Class MainWindow.
 *
 * @author Rimon Oz
 * @author Joost Overeem
 * @author Jeroen de Jong
 */
public class MainWindow extends UIController<StackPane> {

    @FXML
    private Pane profilemenuWrapper;

    private CenterSection center;

    /**
     * Instantiates the main window.
     *
     * @param name         The name of the window
     * @param primaryStage The primary stage
     */
    public MainWindow(final String name, final Stage primaryStage) {
        super(name, "main.fxml", primaryStage);

        setRootNode((Pane) getRootNode().lookup("#basePane"));

        // If your screen has a lower resolution than this, you are too inferior
        // to use the software
        this.setMinimumDimensions(900, 680);

        // When one screen is connected the stage should be displayed
        // on fullscreen, otherwise it should be maximized and set op TopSection (when not
        // set on TopSection, the stage disappears on undesirable moments when using
        // utilities)
        if (Screen.getScreens().size() <= 1) {
            primaryStage.setFullScreen(true);
        } else {
            primaryStage.setAlwaysOnTop(true);
            primaryStage.setMaximized(true);
        }

        //add top section
        this.add(new TopSection(), "topSection", true);
        //add center section
        center = new CenterSection();
        this.add(center, "centerSection", true);
        // add bottom section
        this.add(new BottomSection(), "bottomSection", true);

        // add overlay for profile menu
        this.addOverlay(new ProfilemenuOverlay());
        // add overlay for Matchlogmenu
        this.addOverlay(new LoggerConsoleOverlay());

        //Building is finished, lets spawn the window
        this.spawnWindow();

        // this handler makes sure the stage shuts down when the main window closes
        primaryStage.setOnCloseRequest(windowEvent -> {
            // Shut down javafx platform
            Platform.exit();
            // Shut down program
            System.exit(0);
        });
    }

    public <C extends UIComponent<?>> void addOverlay(C component) {
        ((StackPane) getRootNode().getParent())
                .getChildren().add(component.getComponent());
    }

    /**
     * ultra mega beun, but it will disappear
     *
     * @return
     */
    public FieldGame getFieldGame() {
        return center.getFieldGame();
    }
}
