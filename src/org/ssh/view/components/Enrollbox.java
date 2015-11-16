package org.ssh.view.components;

import org.ssh.ui.UIComponent;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.util.Duration;

// TODO Fix it that enrollbox automatically resizes in parent bounds (Works now, but not in every
// case)
public class Enrollbox extends BorderPane {
    
    public static enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
    
    private double       expandedSize;
    private final Region node;
    private Button       controlButton;
    private boolean      stationaryButton;
                         
    private Direction    slideDirection;
                         
    public Enrollbox(Direction slideDirection, final UIComponent content) {
        this.node = content;
        this.node.setStyle("-fx-background-color: rgba(0, 100, 100, 1.0); -fx-background-radius: 10 10 10 10;");
        this.slideDirection = slideDirection;
        this.expandedSize = 250;
        this.setExpandedSize(this.expandedSize);
        this.node.setVisible(false);
        if (slideDirection == null) {
            // Set default location
            slideDirection = Direction.DOWN;
        }
        this.slideDirection = slideDirection;
        this.setCenter(this.node);
        this.setMaxHeight(0);
        switch (slideDirection) {
            case DOWN:
                this.setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 0 0 10 10;");
                break;
            case LEFT:
                this.setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 10 0 0 10;");
                break;
            case RIGHT:
                this.setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 0 10 10 0;");
                break;
            case UP:
                this.setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 10 10 0 0;");
                break;
            default:
                break;
        }
    }
    
    /**
     * Creates a sidebar panel in a BorderPane, containing an horizontal alignment of the given
     * nodes.
     * 
     * @param expandedSize
     *            The size of the panel.
     * @param controlButton
     *            The button responsible to open/close slide bar.
     * @param location
     *            The location of the panel (TOP_LEFT, BOTTOM_LEFT, BASELINE_RIGHT, BASELINE_LEFT).
     * @param nodes
     *            Nodes inside the panel.
     */
    public Enrollbox(final double expandedSize, final Button button, final Direction location, final Region node) {
        this.setExpandedSize(expandedSize);
        this.node = node;
        node.setVisible(false);
        // Set location
        if (location == null) {
            this.slideDirection = Direction.DOWN; // Set default location
        }
        this.slideDirection = location;
        
        this.setMaxHeight(0);
        this.controlButton = button;
        this.setCenter(node);
        
        this.controlButton.setOnAction(event -> Enrollbox.this.handleRolling(event));
        switch (this.slideDirection) {
            case DOWN:
                this.setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 0 0 10 10;");
                break;
            case LEFT:
                this.setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 10 0 0 10;");
                break;
            case RIGHT:
                this.setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 0 10 10 0;");
                break;
            case UP:
                this.setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 10 10 0 0;");
                break;
            default:
                break;
        }
    }
    
    /**
     * Creates a sidebar panel in a BorderPane, which creates a default button for you.
     * 
     * @param expandedSize
     *            The size of the panel.
     * @param location
     *            The location of the panel (TOP_LEFT, BOTTOM_LEFT, BASELINE_RIGHT, BASELINE_LEFT).
     * @param nodes
     *            Nodes inside the panel.
     * @param stationaryButton
     *            True if the button should stay in place, false otherwise
     */
    public Enrollbox(final double expandedSize,
            final Direction location,
            final Region node,
            final boolean stationaryButton) {
        this.stationaryButton = stationaryButton;
        this.setExpandedSize(expandedSize);
        this.node = node;
        this.setMinWidth(0.0);
        node.setVisible(false);
        // Set location
        if (location == null) {
            this.slideDirection = Direction.DOWN; // Set default location
        }
        this.slideDirection = location;
        this.setCenter(node);
        this.controlButton = new Button(".");
        this.controlButton.setMinWidth(35);
        this.controlButton.setMinHeight(35);
        this.setup();
        
        this.controlButton.setOnAction(event -> Enrollbox.this.handleRolling(event));
        switch (this.slideDirection) {
            case DOWN:
                this.setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 0 0 10 10;");
                break;
            case LEFT:
                this.setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 10 0 0 10;");
                break;
            case RIGHT:
                this.setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 0 10 10 0;");
                break;
            case UP:
                this.setStyle("-fx-background-color: rgba(0, 100, 100, 0.5); -fx-background-radius: 10 10 0 0;");
                break;
            default:
                break;
        }
    }
    
    /**
     * @return the expandedSize
     */
    public double getExpandedSize() {
        return this.expandedSize;
    }
    
    public void handleRolling(final ActionEvent actionEvent) {
        // Create an animation to hide the panel.
        final Animation hidePanel = new Transition() {
            
            {
                this.setCycleDuration(Duration.millis(250));
            }
            
            @Override
            protected void interpolate(final double frac) {
                final double size = Enrollbox.this.getExpandedSize() * (1.0 - frac);
                Enrollbox.this.translateByPos(size);
            }
        };
        
        hidePanel.onFinishedProperty().set(actionEvent1 -> Enrollbox.this.node.setVisible(false));
        
        // Create an animation to show the panel.
        final Animation showPanel = new Transition() {
            
            {
                this.setCycleDuration(Duration.millis(250));
            }
            
            @Override
            protected void interpolate(final double frac) {
                final double size = Enrollbox.this.getExpandedSize() * frac;
                Enrollbox.this.translateByPos(size);
            }
        };
        
        showPanel.onFinishedProperty().set(actionEvent1 -> Enrollbox.this.node.setVisible(true));
        
        if ((showPanel.statusProperty().get() == Animation.Status.STOPPED)
                && (hidePanel.statusProperty().get() == Animation.Status.STOPPED)) {
                
            if (this.node.isVisible()) {
                hidePanel.play();
                
            }
            else {
                this.node.setVisible(true);
                showPanel.play();
            }
        }
    }
    
    /**
     * @param expandedSize
     *            the expandedSize to set
     */
    public void setExpandedSize(final double expandedSize) {
        this.expandedSize = expandedSize;
    }
    
    public void setExpandSize(final double expandSize) {
        if ((this.slideDirection == Direction.UP) || (this.slideDirection == Direction.DOWN)) {
            this.setHeight(expandSize);
        }
        else {
            this.setWidth(expandSize);
        }
        this.expandedSize = expandSize;
    }
    
    public void setup() {
        switch (this.slideDirection) {
            case DOWN:
                this.controlButton.prefWidthProperty().bind(this.widthProperty());
                this.setMaxHeight(35);
                this.setCenter(this.node);
                if (this.stationaryButton)
                    this.setTop(this.controlButton);
                else
                    this.setBottom(this.controlButton);
                break;
            case UP:
                this.controlButton.prefWidthProperty().bind(this.widthProperty());
                this.setMaxHeight(35);
                this.setCenter(this.node);
                if (this.stationaryButton)
                    this.setBottom(this.controlButton);
                else
                    this.setTop(this.controlButton);
                break;
            case RIGHT:
                this.controlButton.prefHeightProperty().bind(this.heightProperty());
                this.setMaxWidth(35);
                this.setCenter(this.node);
                if (this.stationaryButton)
                    this.setLeft(this.controlButton);
                else
                    this.setRight(this.controlButton);
                break;
            case LEFT:
                this.controlButton.prefHeightProperty().bind(this.heightProperty());
                this.setMaxWidth(35);
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
        switch (this.slideDirection) {
            case DOWN:
                this.setMinHeight(size);
                break;
            case UP:
                this.setMinHeight(size);
                break;
            case RIGHT:
            case LEFT:
                this.setMinWidth(size);
                break;
            default:
                break;
        }
    }
}
