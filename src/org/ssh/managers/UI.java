package org.ssh.managers;

import java.util.ArrayList;

import org.ssh.ui.UIController;
import org.ssh.ui.windows.MainWindow;
import org.ssh.util.Logger;

import javafx.stage.Stage;

/**
 * The Class UI.
 *
 * This class is one of the main components of the framework. UI.java gets instantiated as
 * lazy-loaded Singleton {@link https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom}
 * . This class holds references to all the windows and makes instantiating windows easier.
 *
 * @author Rimon Oz
 */
public final class UI {
    
    /**
     * The services store has a controller that runs the store.
     */
    private static ArrayList<UIController<?>> uiControllers;
                                              
    /**
     * The instance.
     */
    private static final Object               instance = new Object();
                                                       
    // a logger for good measure
    private static Logger                     logger   = Logger.getLogger();
                                                       
    /**
     * Adds a window to the UI store.
     *
     * @param <T>
     *            The type of Pane used as a root Node for the window.
     * @param window
     *            The window to be added to the store.
     * @return true, if successful
     */
    public static <T extends UIController<?>> boolean addWindow(final T window) {
        UI.logger.info("Adding new window named %s to the UI store", window.getName());
        return UI.uiControllers.add(window);
    }
    
    /**
     * Gets a window with the specified name.
     *
     * @param name
     *            The name of the requested window.
     * @return The window itself.
     */
    public static UIController<?> get(final String name) {
        UI.logger.info("Getting a window named %s from the UI store", name);
        return UI.uiControllers.stream().filter(controller -> controller.getName().equals(name)).findFirst().get();
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
     * Starts the UI store.
     *
     * @param primaryStage
     *            The primary stage
     * @return true, if successful
     */
    public static boolean start(final Stage primaryStage) {
        UI.uiControllers = new ArrayList<UIController<?>>();
        UI.logger.info("Instantiating new window with id mainWindow");
        return UI.uiControllers.add(new MainWindow("main", primaryStage));
    }
    
}
