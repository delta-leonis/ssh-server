package org.ssh.ui.components;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

/**
 * Animates a node on and off screen to the top, right, bottom or left side.
 *
 * Note: Works best in a {@link BorderPane}
 *
 * @author Thomas Hakkers Modified from: <a href=
 *         "http://blog.physalix.com/javafx2-borderpane-which-slides-in-and-out-on-command/">
 *         Physalix </a>
 *         
 */
public class BorderSlideBar extends BorderPane {
    
    private static final String style  = "-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 10;"; // TODO
                                                                                                                     // Move
                                                                                                                     // to
                                                                                                                     // css
                                                                                                                     // file
    private double              expandedSize;
    private Pos                 flapbarLocation;
    private Node                node;
    private final Button        controlButton;
    private boolean             stationaryButton;
    private Runnable            lambda = null;
                                       
    /**
     * Creates a sidebar panel, containing the given Node
     *
     * @param expandedSize
     *            The size of the panel.
     * @param button
     *            The button responsible to open/close slide bar.
     * @param location
     *            The location of the panel (TOP_CENTER, BOTTOM_CENTER, CENTER_RIGHT, CENTER_LEFT).
     * @param nodes
     *            Node inside the panel.
     */
    public BorderSlideBar(final double expandedSize, final Button button, final Pos location, final Node node) {
        this.init(expandedSize, location, node);
        this.setMaxHeight(0);
        this.controlButton = button;
        this.setCenter(node);
        this.setupButton();
    }
    
    /**
     * Creates a sidebar panel, containing the given Node
     *
     * @param expandedSize
     *            The size of the panel.
     * @param button
     *            The button responsible to open/close slide bar.
     * @param location
     *            The location of the panel (TOP_CENTER, BOTTOM_CENTER, CENTER_RIGHT, CENTER_LEFT).
     * @param nodes
     *            Node inside the panel.
     * @param lambda
     *            The function that gets called whenever the {@link Button} is pressed
     */
    public BorderSlideBar(final double expandedSize,
            final Button button,
            final Pos location,
            final Node node,
            final Runnable lambda) {
        this(expandedSize, button, location, node);
        this.lambda = lambda;
    }
    
    /**
     * Creates a sidebar panel in a BorderPane, which creates a default button for you.
     * 
     * @param expandedSize
     *            The size of the panel.
     * @param location
     *            The location of the panel (TOP_CENTER, BOTTOM_CENTER, CENTER_RIGHT, CENTER_LEFT).
     * @param nodes
     *            Nodes inside the panel.
     * @param stationaryButton
     *            True if the button should stay in place, false otherwise
     */
    public BorderSlideBar(final double expandedSize,
            final Pos location,
            final Node node,
            final boolean stationaryButton) {
        this.init(expandedSize, location, node);
        this.stationaryButton = stationaryButton;
        this.controlButton = new Button(".");
        this.setupForInternalButton();
        this.setupButton();
    }
    
    /**
     * @return the size this BorderSlideBar has when it's fully extended
     */
    public double getExpandedSize() {
        return this.expandedSize;
    }
    
    private void init(final double expandedSize, final Pos location, final Node node) {
        // setStyle(style);
        this.setExpandedSize(expandedSize);
        this.node = node;
        node.setVisible(false);
        // Set location
        if (location == null) {
            this.flapbarLocation = Pos.TOP_CENTER; // Set default location
        }
        this.flapbarLocation = location;
        this.setCenter(node);
        this.setMinWidth(0);
        this.setMinHeight(0);
    }
    
    /**
     * @param expandedSize
     *            the expandedSize to set
     */
    public void setExpandedSize(final double expandedSize) {
        this.expandedSize = expandedSize;
    }
    
    /**
     * Sets the function that gets called whenever the {@link Button} is pressed
     * 
     * @param lambda
     *            The function that gets called whenever the {@link Button} is pressed
     */
    public void setRunnable(final Runnable lambda) {
        this.lambda = lambda;
    }
    
    /**
     * Sets up the button that controls the sliding
     */
    private void setupButton() {
        this.controlButton.setOnAction(actionEvent -> {
            if (BorderSlideBar.this.lambda != null) BorderSlideBar.this.lambda.run();
            
            // Create an animation to hide the panel.
            final Animation hidePanel = new Transition() {
                
                {
                    this.setCycleDuration(Duration.millis(250));
                }
                
                @Override
                protected void interpolate(final double frac) {
                    final double size = BorderSlideBar.this.getExpandedSize() * (1.0 - frac);
                    BorderSlideBar.this.translateByPos(size);
                }
            };
            
            hidePanel.onFinishedProperty().set(actionEvent1 -> BorderSlideBar.this.node.setVisible(false));
            
            // Create an animation to show the panel.
            final Animation showPanel = new Transition() {
                
                {
                    this.setCycleDuration(Duration.millis(250));
                }
                
                @Override
                protected void interpolate(final double frac) {
                    final double size = BorderSlideBar.this.getExpandedSize() * frac;
                    BorderSlideBar.this.translateByPos(size);
                }
            };
            
            showPanel.onFinishedProperty().set(actionEvent1 -> BorderSlideBar.this.node.setVisible(true));
            
            if ((showPanel.statusProperty().get() == Animation.Status.STOPPED)
                    && (hidePanel.statusProperty().get() == Animation.Status.STOPPED)) {
                    
                if (BorderSlideBar.this.node.isVisible()) {
                    hidePanel.play();
                    
                }
                else {
                    BorderSlideBar.this.node.setVisible(true);
                    showPanel.play();
                }
            }
        });
    }
    
    /**
     * Sets up the button that's attached to the {@link BorderSlideBar}
     */
    private void setupForInternalButton() {
        switch (this.flapbarLocation) {
            case TOP_CENTER:
                this.controlButton.prefWidthProperty().bind(this.widthProperty());
                this.setMaxHeight(20);
                this.setCenter(this.node);
                if (this.stationaryButton)
                    this.setTop(this.controlButton);
                else
                    this.setBottom(this.controlButton);
                break;
            case BOTTOM_CENTER:
                this.controlButton.prefWidthProperty().bind(this.widthProperty());
                this.setMaxHeight(20);
                this.setCenter(this.node);
                if (this.stationaryButton)
                    this.setBottom(this.controlButton);
                else
                    this.setTop(this.controlButton);
                break;
            case CENTER_LEFT:
                this.controlButton.prefHeightProperty().bind(this.heightProperty());
                this.setMaxWidth(20);
                this.setCenter(this.node);
                if (this.stationaryButton)
                    this.setLeft(this.controlButton);
                else
                    this.setRight(this.controlButton);
                break;
            case CENTER_RIGHT:
                this.controlButton.prefHeightProperty().bind(this.heightProperty());
                this.setMaxWidth(20);
                this.setCenter(this.node);
                if (this.stationaryButton)
                    this.setRight(this.controlButton);
                else
                    this.setLeft(this.controlButton);
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
    private void translateByPos(final double size) {
        switch (this.flapbarLocation) {
            case TOP_CENTER:
                this.setMinHeight(size);
                this.setTranslateY(-this.getExpandedSize() + size);
                break;
            case BOTTOM_CENTER:
                this.setMinHeight(size);
                break;
            case CENTER_LEFT:
            case CENTER_RIGHT:
                this.setMinWidth(size);
                break;
            default:
                break;
        }
    }
    
}
