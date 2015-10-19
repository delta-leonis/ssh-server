package application;

import java.util.ArrayList;

import javafx.stage.Stage;
import ui.UIController;
import ui.windows.MainWindow;
import util.Logger;

/**
 * The Class UI.
 * 
 * This class is one of the main components of the framework. UI.java gets
 * instantiated as lazy-loaded Singleton {@link https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom}.
 * This class holds references to all the windows and makes instantiating windows easier.
 *
 * @author Rimon Oz
 */
public final class UI {
    /**
     * The services store has a controller that runs the store.
     */
    private static ArrayList<UIController<?>> uiControllers;

    /** The instance. */
    private static final Object instance = new Object();

    // a logger for good measure
    private static Logger logger = Logger.getLogger();

    /**
     * Gets the Singleton instance of UI.
     *
     * @return The single instance.
     */
    public static Object getInstance() {
        return UI.instance;
    }

    /**
     * Starts the UI store.
     *
     * @param primaryStage The primary stage
     */
    public static boolean start(Stage primaryStage) {
        UI.uiControllers = new ArrayList<UIController<?>>();
        UI.logger.info("Instantiating new window with id mainWindow");
        return UI.uiControllers.add(new MainWindow("main", primaryStage));
    }
    
    /**
     * Adds a window to the UI store.
     * 
     * @param window The window to be added to the store.
     * @return       true, if successful
     */
    public static <T extends UIController<?>> boolean addWindow(T window) {
    	UI.logger.info("Adding new window named %s to the UI store", window.getName());
    	return UI.uiControllers.add(window);
    }

	public static UIController<?> get(String name) {
		UI.logger.info("Getting a window named %s from the UI store", name);
		return UI.uiControllers.stream().filter(controller -> controller.getName().equals(name)).findFirst().get();
	}

}
