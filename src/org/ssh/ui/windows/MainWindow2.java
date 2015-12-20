package org.ssh.ui.windows;

import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.ssh.ui.UIComponent2;
import org.ssh.ui.UIController;
import org.ssh.ui.components2.TopSection;

/**
 * The Class MainWindow.
 *
 * @author Rimon Oz
 * @author Joost Overeem
 */
public class MainWindow2 extends UIController<StackPane> {

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

		//add top section
		this.add(new TopSection(), 0, 0);

		// spawn the window
		this.spawnWindow();

		// this handler makes sure the stage shuts down when the main window closes
		primaryStage.setOnCloseRequest(windowEvent -> {

			// Shut down javafx platform
			Platform.exit();
			// Shut down program
			System.exit(0);
		});
	}

	public <C extends UIComponent2<?>> void add(C component, int row, int column){
		((GridPane)getRootNode()).add(component.getComponent(), row, column);
	}
}
