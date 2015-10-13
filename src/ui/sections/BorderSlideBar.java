package ui.sections;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

/**
 * Animates a node on and off screen to the top, right, bottom or left side.
 * 
 * @author Thomas Hakkers E-mail: ThomasHakkers@hotmail.com
 * Modified from: <a href="http://blog.physalix.com/javafx2-borderpane-which-slides-in-and-out-on-command/"> Physalix </a>
 * 
 */
public class BorderSlideBar extends BorderPane {
    private double expandedSize;
    private Pos flapbarLocation;
	private Node node;
	private Button controlButton;
	private boolean stationaryButton;
	private static final String style = "-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 10;";	//TODO Move to css file

    /**
     * Creates a sidebar panel, containing an horizontal alignment
     * of the given nodes.
     * @param expandedSize The size of the panel.
     * @param button The button responsible to open/close slide bar.
     * @param location The location of the panel (TOP_CENTER, BOTTOM_CENTER, CENTER_RIGHT, CENTER_LEFT).
     * @param nodes Node inside the panel.
     */
    public BorderSlideBar(double expandedSize, Button button, Pos location, Node node) {
    	init(expandedSize, location, node);
        setMaxHeight(0);
        controlButton = button;
        setCenter(node);
        setupButton();
    }
    
    /**
     * Creates a sidebar panel in a BorderPane, which creates a default button for you.
     * @param expandedSize The size of the panel.
     * @param location The location of the panel (TOP_CENTER, BOTTOM_CENTER, CENTER_RIGHT, CENTER_LEFT).
     * @param nodes Nodes inside the panel.
     * @param stationaryButton True if the button should stay in place, false otherwise
     */
    public BorderSlideBar(double expandedSize, Pos location, Node node, boolean stationaryButton) {
    	init(expandedSize, location, node);
    	this.stationaryButton = stationaryButton;
        controlButton = new Button(".");
        setupForInternalButton();
        setupButton();
    }
    
    private void init(double expandedSize, Pos location, Node node){
    	setStyle(style);
    	setExpandedSize(expandedSize);
        this.node = node;
        node.setVisible(false);
        // Set location
        if (location == null) {
            flapbarLocation = Pos.TOP_CENTER; // Set default location
        }
        flapbarLocation = location;
        setCenter(node);
    }
    
    /**
     * Sets up the button that controls the sliding
     */
    private void setupButton(){
    	controlButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                // Create an animation to hide the panel.
                final Animation hidePanel = new Transition() {
                    {
                        setCycleDuration(Duration.millis(250));
                    }

                    @Override
                    protected void interpolate(double frac) {
                        final double size = getExpandedSize() * (1.0 - frac);
                        translateByPos(size);
                    }
                };

                hidePanel.onFinishedProperty().set(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                    	node.setVisible(false);
                    }
                });

                // Create an animation to show the panel.
                final Animation showPanel = new Transition() {
                    {
                        setCycleDuration(Duration.millis(250));
                    }

                    @Override
                    protected void interpolate(double frac) {
                        final double size = getExpandedSize() * frac;
                        translateByPos(size);
                    }
                };

                showPanel.onFinishedProperty().set(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                    	node.setVisible(true);
                    }
                });

                if (showPanel.statusProperty().get() == Animation.Status.STOPPED
                        && hidePanel.statusProperty().get() == Animation.Status.STOPPED) {

                    if (node.isVisible()) {
                        hidePanel.play();

                    } else {
                    	node.setVisible(true);
                        showPanel.play();
                    }
                }
            }
        });
    }
    
    /**
     * Sets up the button that's attached to the {@link BorderSlideBar}
     */
    private void setupForInternalButton(){
    	switch (flapbarLocation) {
        case TOP_CENTER:
        	controlButton.prefWidthProperty().bind(widthProperty());
            setMaxHeight(20);
            setCenter(node);
            if(stationaryButton)
            	setTop(controlButton);
            else
            	setBottom(controlButton);
            break;
        case BOTTOM_CENTER:
    		controlButton.prefWidthProperty().bind(widthProperty());
            setMaxHeight(20);
            setCenter(node);
            if(stationaryButton)
            	setBottom(controlButton);
            else	
            	setTop(controlButton);
            break;
        case CENTER_LEFT:
        	controlButton.prefHeightProperty().bind(heightProperty());
            setMaxWidth(20);
            setCenter(node);
            if(stationaryButton)
            	setLeft(controlButton);
            else
            	setRight(controlButton);
            break;
        case CENTER_RIGHT:
    		controlButton.prefHeightProperty().bind(heightProperty());
            setMaxWidth(20);
            setCenter(node);
            if(stationaryButton)
            	setRight(controlButton);
            else
            	setLeft(controlButton);
            break;
		default:
			break;
    	}
    }

    /**
     * Translate the VBox according to location Pos.
     *
     * @param size
     */
    private void translateByPos(double size) {
        switch (flapbarLocation) {
            case TOP_CENTER:
                setMinHeight(size);
                break;
            case BOTTOM_CENTER:
                setMinHeight(size);
                break;
            case CENTER_LEFT:
            case CENTER_RIGHT:
                setMinWidth(size);
                break;
		default:
			break;
        }
    }  

    /**
     * @return the size this BorderSlideBar has when it's fully extended
     */
    public double getExpandedSize() {
        return expandedSize;
    }

    /**
     * @param expandedSize the expandedSize to set
     */
    public void setExpandedSize(double expandedSize) {
        this.expandedSize = expandedSize;
    }
   
}
