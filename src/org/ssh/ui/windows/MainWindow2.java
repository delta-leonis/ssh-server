package org.ssh.ui.windows;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.ssh.ui.UIComponent2;
import org.ssh.ui.UIController;
import org.ssh.ui.components2.*;

/**
 * The Class MainWindow.
 *
 * @author Rimon Oz
 * @author Joost Overeem
 */
public class MainWindow2 extends UIController<StackPane> {

	@FXML
	Pane profilemenuWrapper;

	/**
	 * Instantiates the main window.
	 *
	 * @param name
	 *            The name of the window
	 * @param primaryStage
	 *            The primary stage
	 */
	public MainWindow2(final String name, final Stage primaryStage) {
		super(name, "main2.fxml", primaryStage);

		setRootNode((Pane)getRootNode().lookup("#basePane"));

		// If your screen has a lower resolution than this, you are too inferior
		// to use the software
		this.setMinimumDimensions(900, 850);

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
		this.add(new TopSection(), 0, 0);
		//add center section
		this.add(new CenterSection(), 0, 1);
		// add bottom section
        this.add(new BottomSection(), 0, 2);

		// add overlay for profile menu
        this.addOverlay(new ProfilemenuOverlay());
		// add overlay for Matchlogmenu
        this.addOverlay(new MatchlogmenuOverlay());
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

    public <C extends UIComponent2<?>> void addOverlay(C component){
        ((StackPane)getRootNode().getParent())
                .getChildren().add(component.getComponent());
    }

	public <C extends UIComponent2<?>> void add(C component, int row, int column){
		((GridPane)getRootNode()).add(component.getComponent(), row, column);
	}
}
