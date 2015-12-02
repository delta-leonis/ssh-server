package org.ssh.managers.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.ssh.ui.UIController;
import org.ssh.ui.windows.MainWindow;
import org.ssh.util.Logger;

import javafx.scene.layout.Pane;
import javafx.stage.Stage;

// TODO: Auto-generated Javadoc
/**
 * The Class UI.
 *
 * This class is one of the main components of the framework. UI.java gets instantiated as
 * lazy-loaded Singleton {@link https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom}
 * . This class holds references to all the windows and makes instantiating windows easier.
 *
 * @author Rimon Oz
 *         
 * @TODO add removeWindow()
 * @TODO addWindow() needs to check for duplicate window names
 */
public final class UI {
    
    /**
     * The services store has a controller that runs the store.
     */
    private static List<UIController<?>> uiControllers;
                                         
    /**
     * The single instance of the class (lazy-loaded singleton).
     */
    private static final Object          instance = new Object();
                                                  
    /** The Constant LOG. */
    // a logger for good measure
    private static final Logger          LOG      = Logger.getLogger();
                                                  
    /**
     * Private constructor to hide the implicit public one.
     */
    private UI() {
    }
    
    /**
     * Adds a window to the UI store.
     *
     * @param <T>
     *            The type of Pane used as a root Node for the window.
     * @param window
     *            The window to be added to the UI store.
     * @return true, if successful
     */
    public static <T extends UIController<?>> boolean addWindow(final T window) {
        UI.LOG.info("Adding new window named %s to the UI store", window.getName());
        return UI.uiControllers.add(window);
    }
    
    /**
     * Returns an Optional containing the window with the specified name.
     *
     * @param
     *            <P>
     *            the generic type
     * @param name
     *            The name of the requested window.
     * @return An Optional containing the window.
     */
    @SuppressWarnings ("unchecked")
    public static <U extends UIController<? extends Pane>> Optional<U> get(final String name) {
        UI.LOG.fine("Getting a window named %s from the UI store", name);
        // get a stream of all the windows
        return UI.uiControllers.stream()
                // filter out the window with the correct name
                .filter(controller -> controller.getName().equals(name)).map(controller -> (U) controller)
                // and return the first one in the list
                .findFirst();
    }
    
    /**
     * Gets the Singleton instance of the UI store.
     *
     * @return The single instance.
     */
    public static Object getInstance() {
        return UI.instance;
    }
    
    /**
     * Starts the UI store and spawns the main window.
     *
     * @param primaryStage
     *            The primary stage to be passed to the main window.
     * @return true, if successful
     */
    public static boolean start(final Stage primaryStage) {
        // set attributes
        UI.uiControllers = new ArrayList<UIController<?>>();
        UI.LOG.info("Instantiating new window with id mainWindow");
        // instantiate a new MainWindow and add it to the UI store
        return UI.uiControllers.add(new MainWindow("main", primaryStage));
    }
}
