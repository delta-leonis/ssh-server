package application;

import java.util.ArrayList;
import java.util.Optional;

import util.Logger;

import model.Model;
import model.ModelController;

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
    private static Logger logger = Logger.getLogger();

    /**
     * This method finds a model and returns it as a Model.
     * @param modelName The name of the model you want to find.
     * @return          The requested model.
     */
    public static Optional<Model> get(String modelName) {
        return Models.modelController.get(modelName);
    }
    
    /**
     * This method finds all models matching the name and returns them as an ArrayList<Model>
     * @param modelName The (fuzzy) name of the model you want to find.
     * @return          The requested model.
     */
    public static ArrayList<Model> getAll(String modelName) {
        return Models.modelController.getAll(modelName);
    }

    /**
     * Gets the single instance of Models.
     *
     * @return single instance of Models
     */
    public static Object getInstance() {
        return Models.instance;
    }
    
    public static void add(Model model){
    	Models.modelController.add(model);
    }

    /**
     * This method instantiates a controller to run the store.
     */
    public static void start() {
        Models.logger.info("Starting Models...");
        Models.modelController = new ModelController();
    }

}