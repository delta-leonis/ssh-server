package application;

import java.util.ArrayList;
import java.util.logging.Logger;

import javafx.stage.Stage;
import ui.UIController;
import ui.windows.MainWindow;

/**
 * The Class UI.
 */
public final class UI {
    /**
     * The services store has a controller that runs the store.
     */
    private static ArrayList<UIController> uiControllers;

    /** The instance. */
    private static final Object instance = new Object();

    // a logger for good measure
    private static Logger logger = Logger.getLogger(Services.class.toString());

    /**
     * Gets the single instance of UI.
     *
     * @return single instance of UI
     */
    public static Object getInstance() {
        return UI.instance;
    }

    /**
     * Start.
     *
     * @param primaryStage the primary stage
     */
    public static void start(Stage primaryStage) {
        UI.uiControllers = new ArrayList<UIController>();
        UI.logger.info("Instantiating new window with id mainWindow");
        UI.uiControllers.add(new MainWindow("main", primaryStage));
    }

}
