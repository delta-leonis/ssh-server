package org.ssh.ui.windows;

import org.ssh.ui.UIController;

import javafx.scene.layout.GridPane;

/**
 * The Class WidgetWindow.
 *
 * @author Rimon Oz
 * @author Joost Overeem
 */
public class WidgetWindow extends UIController<GridPane> {
    
    /**
     * Instantiates a new WidgetWindow.
     *
     * @param name
     *            The name of the window
     */
    public WidgetWindow(final String name) {
        super(name, "widgetwindow.fxml", 800, 600);
        // setup in here
        
        // spawn the window
        this.spawnWindow();
    }
    
    /**
     * Instantiates a new widget window.
     *
     * @param name
     *            The name of the window
     * @param width
     *            The width of the window
     * @param height
     *            The height of the window
     */
    public WidgetWindow(final String name, final int width, final int height) {
        super(name, "widgetwindow.fxml", width, height);
        // setup in here
        
        // spawn the window
        this.spawnWindow();
    }
    
}
