package org.ssh.ui.windows;

import org.ssh.field3d.FieldGame;
import org.ssh.ui.UIComponent;
import org.ssh.ui.UIController;
import org.ssh.ui.components.MatchlogSelector;
import org.ssh.ui.components.Profilemenu;
import org.ssh.ui.components.Timeslider;
import org.ssh.ui.components.Toolbox;
import org.ssh.view.components.Enrollbox;
import org.ssh.view.components.Enrollbox.Direction;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.SceneAntialiasing;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * The Class MainWindow.
 *
 * @author Rimon Oz
 * @author Joost Overeem
 */
public class MainWindow extends UIController<StackPane> {
    
    @FXML
    private StackPane       rootNode, baseCenter, canvasfitHome, canvasfitAway;
    @FXML
    private GridPane        basePane, baseTop, baseBottom;
    @FXML
    private BorderPane      matchlog, toolbox, profilemenu;
    @FXML
    private Pane            centerwrapper, timesliderContainer;
    @FXML
    private Canvas          ballcanvasAway, ballcanvasHome;
    @FXML
    private Group           fieldBase;
                            
    private final Enrollbox toolboxEnroller, profilemenuEnroller;
                            
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
        // if your screen has a lower resolution than this, you are to inferior
        // to use the software
        this.setMinimumDimensions(800, 730);
        
        // When one screen is connected the org.ssh.managers should be displayed on
        // fullscreen, otherwise it should be maximized and set op top (when not
        // set on top, the stage disappears on undesirable moments when using
        // utilities)
        if (Screen.getScreens().size() <= 1) {
            primaryStage.setFullScreen(true);
        }
        else {
            primaryStage.setAlwaysOnTop(true);
            primaryStage.setMaximized(true);
        }
        
        // setup in here
        // let's add a component
        final FieldGame field = new FieldGame(new Group(), 500, 500, SceneAntialiasing.BALANCED);
        // Call the big initialise function that provides the complete field
        field.InternalInitialize();
        // Add the field SubScene to the Group defined in the fxml
        this.fieldBase.getChildren().add(field);
        // Bind the height and width properties of the field to the basecenter
        // so that the field is sized in
        // the middlemost vertical 73% of the stage
        field.heightProperty().bind(this.baseCenter.heightProperty());
        field.widthProperty().bind(this.baseCenter.widthProperty());
        // Some property binding to keep the field within the middlemost
        // vertical 73% of the stage
        this.baseCenter.minHeightProperty().bind(this.centerwrapper.heightProperty());
        this.baseCenter.maxHeightProperty().bind(this.centerwrapper.heightProperty());
        this.baseCenter.minWidthProperty().bind(this.centerwrapper.widthProperty());
        this.baseCenter.maxWidthProperty().bind(this.centerwrapper.widthProperty());
        
        // Toolbox wrapped in an Enrollbox for fancy up and down sliding
        this.toolboxEnroller = new Enrollbox(Direction.UP, new Toolbox());
        // MinHeight is set to 0.0, otherwise the slider would be partially
        // visible when slider is collapsed
        this.toolboxEnroller.setMinHeight(0.0);
        // Add the enroller to the pane defined in the fxml
        this.toolbox.setBottom(this.toolboxEnroller);
        
        // Profile menu wrapped in an Enrollbox for fancy up and down sliding
        this.profilemenuEnroller = new Enrollbox(Direction.DOWN, new Profilemenu());
        // MinHeight is set to 0.0, otherwise the slider would be partially
        // visible when slider is collapsed
        this.profilemenuEnroller.setMinHeight(0.0);
        // Add the enroller to the pane defined in the fxml
        this.profilemenu.setTop(this.profilemenuEnroller);
        // Time slider in baseBottom(1,0 GridPane.hgrow="ALWAYS")
        this.profilemenu.heightProperty()
                .addListener((arg0, oldValue, newValue) -> this.profilemenuEnroller.setExpandSize((double) newValue));
                
        // Matchlog selector wrapped in Enrollbox for fancy horizontal sliding.
        // This is an enrollbox with the button included and 250 pixels width.
        this.matchlog.setLeft(new Enrollbox(250, Direction.RIGHT, new MatchlogSelector(), false));
        
        // Do some binding to fit the little teamcollored balls next to the
        // teamnames in their canvas
        // Both height- and widthproperty are bound to the widthproperty because
        // that keeps the balls round in stead of oval
        this.ballcanvasHome.heightProperty().bind(this.canvasfitHome.widthProperty());
        this.ballcanvasHome.widthProperty().bind(this.canvasfitHome.widthProperty());
        // Redraw them every sizechange to prevent the balls from growing huge
        // like yo mama
        this.ballcanvasHome.widthProperty().addListener(
                observable -> this.redrawCircle(this.ballcanvasHome, Color.BLUE, this.ballcanvasHome.getWidth()));
        this.ballcanvasHome.heightProperty().addListener(
                observable -> this.redrawCircle(this.ballcanvasHome, Color.BLUE, this.ballcanvasHome.getWidth()));
                
        // Now all the same binding, but for the other ball
        this.ballcanvasAway.heightProperty().bind(this.canvasfitAway.widthProperty());
        this.ballcanvasAway.widthProperty().bind(this.canvasfitAway.widthProperty());
        this.ballcanvasAway.widthProperty().addListener(
                observable -> this.redrawCircle(this.ballcanvasAway, Color.YELLOW, this.ballcanvasAway.getWidth()));
        this.ballcanvasAway.heightProperty().addListener(
                observable -> this.redrawCircle(this.ballcanvasAway, Color.YELLOW, this.ballcanvasAway.getWidth()));
                
        final Timeslider timeslider = new Timeslider();
        this.timesliderContainer.getChildren().add(timeslider);
        timeslider.minHeightProperty().bind(this.timesliderContainer.heightProperty());
        timeslider.maxHeightProperty().bind(this.timesliderContainer.heightProperty());
        timeslider.minWidthProperty().bind(this.timesliderContainer.widthProperty());
        timeslider.maxWidthProperty().bind(this.timesliderContainer.widthProperty());
        // spawn the window
        this.spawnWindow();
        
        // this handler makes sure the org.ssh.managers shuts down when the main
        // window closes
        primaryStage.setOnCloseRequest(windowEvent -> {
            Platform.exit();
            System.exit(0);
        });
    }
    
    public <T extends UIComponent> void addToBottom(final T component, final int column, final int row) {
        this.baseBottom.add(component, column, row);
    }
    
    public <T extends UIComponent> void addToTop(final T component, final int column, final int row) {
        this.baseTop.add(component, column, row);
    }
    
    @FXML
    private void enrollProfilemenu(final ActionEvent e) {
        this.profilemenuEnroller.handleRolling(e);
    }
    
    @FXML
    private void enrollToolbox(final ActionEvent e) {
        this.toolboxEnroller.handleRolling(e);
    }
    
    @FXML
    private void exit(final ActionEvent e) {
        Platform.exit();
        System.exit(0);
    }
    
    @FXML
    private void iconize(final ActionEvent e) {
        ((Stage) this.getScene().getWindow()).setIconified(true);
    }
    
    @FXML
    private void minimize(final ActionEvent e) {
        ((Stage) this.getScene().getWindow()).setFullScreen(!((Stage) this.getScene().getWindow()).isFullScreen());
    }
    
    private void redrawCircle(final Canvas canvas, final Color color, final double diameter) {
        final GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(color);
        gc.fillOval(0, 0, diameter, diameter);
    }
}
