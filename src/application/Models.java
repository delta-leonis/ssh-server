package application;

import java.util.logging.Logger;

import javafx.collections.ObservableList;
import models.Model;
import models.ModelController;
import models.ui.ListModel;

/**
 * The Class Models.
 */
public final class Models {
    /**
     * The model store has a controller that runs the store.
     */
    private static ModelController modelController;

    /** The instance. */
    private static final Object instance = new Object();

    // a logger for good measure
    private static Logger logger = Logger.getLogger(Models.class.toString());

    /**
     * This method adds a {@link ListModel} to the model store.
     *
     * @param stringBank the string bank
     */
    public static void addListModel(ListModel<String> stringBank) {
        Models.logger.info("Adding new ListModel: " + stringBank.getClass().getName());
        Models.modelController.addListModel(stringBank);

    }

    /**
     * This method finds a model and returns it as a Model.
     * @param modelName The name of the model you want to find.
     * @return          The requested model.
     */
    public static Model get(String modelName) {
        return Models.modelController.get(modelName);
    }

    /**
     * Gets the single instance of Models.
     *
     * @return single instance of Models
     */
    public static Object getInstance() {
        return Models.instance;
    }

    /**
     * This method instantiates a controller to run the store.
     */
    public static void start() {
        Models.logger.info("Starting Models...");
        Models.modelController = new ModelController();
    }

    /**
     * This method retrieves all of the list data that's in the store.
     * @return All the List data in the model store.
     */
    public ObservableList<?> getAllListData() {
        return Models.modelController.getAllListData();
    }

}