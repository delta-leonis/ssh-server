package ui.windows;

import javafx.application.Platform;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import ui.UIController;
import ui.components.WindowSpawnComponent;
/**
 * The Class MainWindow.
 *
 * @author Rimon Oz
 */
public class MainWindow extends UIController<BorderPane> {

	/**
	 * Instantiates the main window.
	 *
	 * @param name         The name of the window
	 * @param primaryStage The primary stage
	 */
	public MainWindow(final String name, final Stage primaryStage) {
		super(name, "main.fxml", primaryStage);
		this.setMinimumDimensions(600, 400);

		// setup in here
		// let's add a component
		this.add(new WindowSpawnComponent("testcomponent"));

		// spawn the window
		this.spawnWindow();

		// this handler makes sure the application shuts down when the main
		// window closes
		primaryStage.setOnCloseRequest(windowEvent -> {
			Platform.exit();
			System.exit(0);
		});
	}
}
