package application;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Optional;

import model.Model;
import model.ModelController;
import model.Settings;
import util.Logger;

/**
 * The Class Models. Contains the manager for all {@link Model Models} that are in use. 
 * 
 * TODO fuzzy searching for models
 * 
 * @see #create(Class, Object...)
 * @see #get(String)
 * @author Jeroen
 */
public final class Models {
	/**
	 * The model store has a controller that runs the store.
	 */
	private static ModelController modelController;

    /** The instance. */
    private static final Object instance = new Object();

	// respective logger
	private static Logger logger = Logger.getLogger();


    /**
     * Gets the Singleton instance of Models.
     *
     * @return The single instance.
     */
    public static Object getInstance() {
        return Models.instance;
    }
    
	public static Model create(Class<?> clazz, Object... args){
		return ModelController.create(clazz, args);
	}
	/**
	 * This method finds a model and returns it as a Model.
	 * 
	 * @param modelName
	 *            The name of the model you want to find.
	 * @return The requested model.
	 */
	public static Optional<Model> get(String modelName) {
		return Models.modelController.get(modelName);
	}

	/**
	 * @return all Models currently in the modelcontroller
	 */
	public static ArrayList<Model> getAll() {
		return Models.modelController.getAll();
	}

	/**
	 * This method finds all models matching the name and returns them as an
	 * ArrayList<Model>
	 * 
	 * @param modelName
	 *            The (fuzzy) name of the model you want to find.
	 * @return The requested model.
	 */
	public static ArrayList<Model> getAll(String modelName) {
		return Models.modelController.getAll(modelName);
	}

	/**
	 * adds a model to this manager
	 * 
	 * @param model
	 */
	public static void add(Model model) {
		Models.modelController.add(model);
	}

	/**
	 * This method instantiates a controller to run the store.
	 */
	public static void start() {
		Models.logger.info("Starting Models...");

		// create modelController
		Models.modelController = new ModelController();
		// create a settings model (will self-assign in the factory)
		Models.create(Settings.class);
	}

	/**
	 * Save the current state of the model in profiles path
	 * 
	 * @param model
	 *            model to save
	 * @return success value
	 */
	public static boolean save(Model model) {
		return Models.modelController.save(model);
	}

	/**
	 * Save given model as default for this model type
	 * 
	 * @param model
	 * @return succes value
	 */
	public static boolean saveAsDefault(Model model) {
		return modelController.saveAsDefault(model);
	}

	/**
	 * Initialize all values in the configfile for given model
	 * 
	 * @param model
	 *            model to initialize
	 * @return success value
	 */
	public static boolean initialize(Model model) {
		return modelController.load(model);
	}

	/**
	 * Initialize all values in the configfile for each model
	 * 
	 * @return success value
	 */
	public static boolean initializeAll() {
		return modelController.initializeAll();
	}

	/**
	 * Set all non-{@link Modifier#TRANSIENT transient} fields of given model to
	 * null, and reload values from configfile
	 * 
	 * @param model
	 *            to reinitialize
	 * @return success value
	 */
	public static boolean reinitialize(Model model) {
		return modelController.reinitialize(model);
	}

	/**
	 * Set all non-{@link Modifier#TRANSIENT transient} fields for all models to
	 * null, and reload values from configfile for every model
	 * 
	 * @return success value
	 */
	public static boolean reinitializeAll() {
		return modelController.reinitializeAll();
	}
}