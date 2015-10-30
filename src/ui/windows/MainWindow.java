package ui.windows;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import ui.UIComponent;
import ui.UIController;

/**
 * The Class MainWindow.
 *
 * @author Rimon Oz
 * @author Joost Overeem
 */
public class MainWindow extends UIController<StackPane> {

	@FXML
	private GridPane basePane;
	@FXML
	private GridPane baseTop;
	@FXML
	private StackPane baseCenter;
	@FXML
	private GridPane baseBottom;
	@FXML
	private StackPane canvasfitAway;
	@FXML
	private Canvas ballcanvasAway;
	@FXML
	private StackPane canvasfitHome;
	@FXML
	private Canvas ballcanvasHome;

	/**
	 * Instantiates the main window.
	 *
	 * @param name
	 *            The name of the window
	 * @param primaryStage
	 *            The primary stage
	 */
	public MainWindow(final String name, final Stage primaryStage) {
		super(name, "main.fxml", primaryStage);
		this.setMinimumDimensions(600, 400);

		// When one screen is connected the application should be displayed on
		// fullscreen, otherwise it should be maximized and set op top (when not
		// set on top, the stage disappears on undesirable moments when using
		// utilities)
		if (Screen.getScreens().size() <= 1) {
			primaryStage.setFullScreen(true);
		} else {
			primaryStage.setAlwaysOnTop(true);
			primaryStage.setMaximized(true);
		}

		// setup in here
		// let's add a component
		// this.add(new WindowSpawnComponent("testcomponent"));
		// this.addToTop(new WindowSpawnComponent("testcomponent"), 0, 0);

		// FieldSubScene field in fieldbase
		// MatchlogSelector in baseCenter pickOnBounds="false"
		// Toolbox in baseCenter pickOnBounds="false"
		// Time slider in baseBottom(1,0 GridPane.hgrow="ALWAYS")
		// Profile menu in overlay(0,1)

		
		// field.heightProperty().bind(fieldbase.heightProperty());
		// field.widthProperty().bind(fieldbase.widthProperty());

		ballcanvasHome.heightProperty().bind(canvasfitHome.widthProperty());
		ballcanvasHome.widthProperty().bind(canvasfitHome.widthProperty());
		ballcanvasHome.widthProperty()
				.addListener(observable -> redrawCircle(ballcanvasHome, Color.BLUE, ballcanvasHome.getWidth()));
		ballcanvasHome.heightProperty()
				.addListener(observable -> redrawCircle(ballcanvasHome, Color.BLUE, ballcanvasHome.getWidth()));

		ballcanvasAway.heightProperty().bind(canvasfitAway.widthProperty());
		ballcanvasAway.widthProperty().bind(canvasfitAway.widthProperty());
		ballcanvasAway.widthProperty()
				.addListener(observable -> redrawCircle(ballcanvasAway, Color.YELLOW, ballcanvasAway.getWidth()));
		ballcanvasAway.heightProperty()
				.addListener(observable -> redrawCircle(ballcanvasAway, Color.YELLOW, ballcanvasAway.getWidth()));

		// spawn the window
		this.spawnWindow();

		// this handler makes sure the application shuts down when the main
		// window closes
		primaryStage.setOnCloseRequest(windowEvent -> {
			Platform.exit();
			System.exit(0);
		});
	}

	private void redrawCircle(Canvas canvas, Color color, double diameter) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(color);
		gc.fillOval(0, 0, diameter, diameter);
	}

	public <T extends UIComponent> void addToTop(T component, int column, int row) {
		this.baseTop.add(component, column, row);
	}

	public <T extends UIComponent> void addToBottom(T component, int column, int row) {
		this.baseBottom.add(component, column, row);
	}
}
