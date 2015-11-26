package org.ssh.ui.windows;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.SceneAntialiasing;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import org.ssh.field3d.FieldGame;
import org.ssh.ui.UIController;
import org.ssh.ui.components.Enroller;
import org.ssh.ui.components.LoggerConsole;
import org.ssh.ui.components.MatchlogSelector;
import org.ssh.ui.components.Profilemenu;
import org.ssh.ui.components.Timeslider;
import org.ssh.ui.components.Toolbox;
import org.ssh.ui.components.Enroller.ExtendDirection;

/**
 * The Class MainWindow.
 *
 * @author Rimon Oz
 * @author Joost Overeem
 */
public class MainWindow extends UIController<StackPane> {

	/**
	 * {@link StackPane where field, matchlogselector and toolbox are stacked in.
	 */
	@FXML
	private StackPane baseCenter;
	/**
	 * {@link StackPane} to fit a {@link Canvas} with collored balls on for the home team.
	 */
	@FXML
	private StackPane canvasfitHome;
	/**
	 * {@link StackPane} to fit a {@link Canvas} with collored balls on for the away team.
	 */
	@FXML
	private StackPane canvasfitAway;

	/**
	 * Pane that is used to bind sizing to.
	 */
	@FXML
	private GridPane topElementswrapper;

	/**
	 * {@link Pane} to wrap the whole center in (BaseCenter).
	 */
	@FXML
	private Pane centerwrapper;
	/**
	 * {@link Pane} to wrap the timeslider in and bind its sizeproperties to.
	 */
	@FXML
	private Pane timesliderWrapper;
	/**
	 * {@link Pane} to wrap the loggingconsole in and bind its sizeproperties to.
	 */
	@FXML
	private Pane loggingconsoleWrapper;
	/**
	 * {@link Pane} to wrap the toolbox in and bind its sizeproperties to.
	 */
	@FXML
	private Pane toolboxWrapper;
	/**
	 * {@link Pane} to wrap the profilemenu in and bind its sizeproperties to.
	 */
	@FXML
	private Pane profilemenuWrapper;
	/**
	 * {@link Pane} to wrap the matchlog in and bind its sizeproperties to.
	 */
	@FXML
	private Pane matchlogWrapper;
	/**
	 * {@link Pane} to bind the sizeproperties of the matchlog enrollment button to.
	 */
	@FXML
	private Pane matchlogButtonSizer;

	/**
	 * {@link Canvas} to draw the teamcolloured balls on.
	 */
	@FXML
	private Canvas ballcanvasAway;
	/**
	 * {@link Canvas} to draw the teamcolloured balls on.
	 */
	@FXML
	private Canvas ballcanvasHome;

	/**
	 * {@link Group} to put the 3d field in. A group automaticcaly autosizes its children.
	 */
	@FXML
	private Group fieldBase;

	/**
	 * {@link ImageView} where the enrollment icon is displayed in. When extending Loggerconsole, the icon can be flipped.
	 */
	@FXML
	private ImageView enrollLogconsoleImage;
	/**
	 * {@link ImageView} where the enrollment icon is displayed in. When extending Toolbox, the icon can be flipped.
	 */
	@FXML
	private ImageView enrollToolboxImage;

	/**
	 * {@link Enroller} for the loggingconsole
	 */
	private Enroller loggingconsoleEnroller;
	/**
	 * {@link Enroller} for the profilemenu
	 */
	private Enroller profilemenuEnroller;
	/**
	 * {@link Enroller} for the toolbox
	 */
	private Enroller toolboxEnroller;

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
		// If your screen has a lower resolution than this, you are too inferior
		// to use the software
		this.setMinimumDimensions(800, 730);

		// When one screen is connected the stage should be displayed
		// on fullscreen, otherwise it should be maximized and set op top (when not
		// set on top, the stage disappears on undesirable moments when using
		// utilities)
		if (Screen.getScreens().size() <= 1) {
			primaryStage.setFullScreen(true);
		} else {
			primaryStage.setAlwaysOnTop(true);
			primaryStage.setMaximized(true);
		}


		// Add the field
		FieldGame field = new FieldGame(new Group(), 500, 500, SceneAntialiasing.BALANCED);
		// Call the big initialise function that provides the complete field
		field.internalInitialize();
		// Add the field SubScene to the Group defined in the fxml
		this.fieldBase.getChildren().add(field);
		// Bind the height and width properties of the field to the basecenter
		// so that the field is sized in
		// the middlemost vertical 73% of the stage
		field.heightProperty().bind(baseCenter.heightProperty());
		field.widthProperty().bind(baseCenter.widthProperty());
		// Some property binding to keep the field within the middlemost
		// vertical 73% of the stage
		baseCenter.minHeightProperty().bind(centerwrapper.heightProperty());
		baseCenter.maxHeightProperty().bind(centerwrapper.heightProperty());
		baseCenter.minWidthProperty().bind(centerwrapper.widthProperty());
		baseCenter.maxWidthProperty().bind(centerwrapper.widthProperty());


		// Add the logger an Enroller for enlarging when you want to see more lines at a time.
		loggingconsoleEnroller = new Enroller(new LoggerConsole(), ExtendDirection.DOWN,
				loggingconsoleWrapper.widthProperty(), topElementswrapper.heightProperty(),
				loggingconsoleWrapper.heightProperty(), false);
		// Set a style class for the loggingconsoleEnroller
		loggingconsoleEnroller.getStyleClass().add("loggingconsoleEnroller");
		loggingconsoleWrapper.getChildren().add(loggingconsoleEnroller);

		// Toolbox wrapped in an Enroller for fancy up and down sliding
		toolboxEnroller = new Enroller(new Toolbox(), ExtendDirection.UP, toolboxWrapper.widthProperty(),
				toolboxWrapper.heightProperty());
		// Set a style class for the toolboxEnroller
		toolboxEnroller.getStyleClass().add("toolboxEnroller");
		this.toolboxWrapper.getChildren().add(toolboxEnroller);

		// Profilemenu wrapped in an Enroller for fancy up and down sliding
		profilemenuEnroller = new Enroller(new Profilemenu(), ExtendDirection.DOWN, profilemenuWrapper.widthProperty(),
				profilemenuWrapper.heightProperty());
		// Set a style class for the profilemenuEnroller
		profilemenuEnroller.getStyleClass().add("profilemenuEnroller");
		this.profilemenuWrapper.getChildren().add(profilemenuEnroller);

		// MatchlogSelector wrapped in an Enroller for fancy up and down sliding
		Enroller matchlogEnroller = new Enroller(new MatchlogSelector(), ExtendDirection.RIGHT, matchlogWrapper.heightProperty(),
				matchlogButtonSizer.widthProperty(), matchlogWrapper.widthProperty(), true);
		// Set a style class for the profilemenuEnroller
		matchlogEnroller.getStyleClass().add("matchlogEnroller");
		this.matchlogWrapper.getChildren().add(matchlogEnroller);

		// Make a timeslider and add it in its wrapper
		Timeslider timeslider = new Timeslider();
		timesliderWrapper.getChildren().add(timeslider);
		// Bind the timesliders height and width for correct resizing
		timeslider.minHeightProperty().bind(timesliderWrapper.heightProperty());
		timeslider.maxHeightProperty().bind(timesliderWrapper.heightProperty());
		timeslider.minWidthProperty().bind(timesliderWrapper.widthProperty());
		timeslider.maxWidthProperty().bind(timesliderWrapper.widthProperty());


		// Do some binding to fit the little teamcollored balls next to the
		// teamnames in their canvas
		// Both height- and widthproperty are bound to the widthproperty because
		// that keeps the balls round in stead of oval
		ballcanvasHome.heightProperty().bind(canvasfitHome.widthProperty());
		ballcanvasHome.widthProperty().bind(canvasfitHome.widthProperty());
		// Redraw them every sizechange to prevent the balls from growing huge
		// like yo mama
		ballcanvasHome.widthProperty()
				.addListener(observable -> redrawCircle(ballcanvasHome, Color.BLUE, ballcanvasHome.getWidth()));
		ballcanvasHome.heightProperty()
				.addListener(observable -> redrawCircle(ballcanvasHome, Color.BLUE, ballcanvasHome.getWidth()));
		// Now all the same binding, but for the other ball
		ballcanvasAway.heightProperty().bind(canvasfitAway.widthProperty());
		ballcanvasAway.widthProperty().bind(canvasfitAway.widthProperty());
		ballcanvasAway.widthProperty()
				.addListener(observable -> redrawCircle(ballcanvasAway, Color.YELLOW, ballcanvasAway.getWidth()));
		ballcanvasAway.heightProperty()
				.addListener(observable -> redrawCircle(ballcanvasAway, Color.YELLOW, ballcanvasAway.getWidth()));


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

	/**
	 * Draws a filled circle in the given {@link Canvas}.
	 * 
	 * @param canvas {@link Canvas} where the circle should be drawn on.
	 * @param color {@link Color} the circle should get.
	 * @param diameter The diameter of the circle
	 */
	private void redrawCircle(Canvas canvas, Color color, double diameter) {
		// Set context to draw in
		GraphicsContext gc = canvas.getGraphicsContext2D();
		// Set the filling color
		gc.setFill(color);
		// Draw the circle starting at (0,0) with diameter as width and height
		gc.fillOval(0, 0, diameter, diameter);
	}

	/**
	 * Extends and collapses the toolbox
	 */
	@FXML
	private void enrollToolbox() {
		// Call Enroller function to handle enrollment
		toolboxEnroller.handleEnrollment(enrollToolboxImage);
	}

	/**
	 * Extends and collapses the profilemenu
	 */
	@FXML
	private void enrollProfilemenu() {
		// Call Enroller function to handle enrollment
		profilemenuEnroller.handleEnrollment();
	}

	/**
	 * Extends and collapses the loggingconsole
	 */
	@FXML
	private void enrollLoggingconsole() {
		// Call Enroller function to handle enrollment
		loggingconsoleEnroller.handleEnrollment(enrollLogconsoleImage);
	}

	/**
	 * Function to switch between fullscreen and normal window. Is called by the fullscreen button in main.fxml
	 */
	@FXML
	private void switchFullscreen() {
		// Toggle the fullscreen of this stage.
		getStage().setFullScreen(!getStage().isFullScreen());
	}

	/**
	 * Function to iconize the stage. Is called by the iconize button in main.fxml
	 */
	@FXML
	private void iconize() {
		// Minimize the stage
		getStage().setIconified(true);
	}

	/**
	 * Function to shut down the program. Is called by the exit button in main.fxml
	 */
	@FXML
	private void exit() {
		// Shut down javafx platform
		Platform.exit();
		// Shut down program
		System.exit(0);
	}
}
