package org.ssh.managers;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;

import org.ssh.models.Model;
import org.ssh.models.ModelController;
import org.ssh.models.Settings;
import org.ssh.util.Logger;

/**
 * The Class Models. Contains the manager for all {@link Model Models} that are in use.
 *
 * @TODO fuzzy searching for models
 *       
 * @see #create(Class, Object...)
 * @see #get(String)
 * 
 * @author Jeroen de Jong
 */
public final class Models {
    
    /**
     * The org.ssh.models store has a controller that runs the store.
     */
    private static ModelController modelController;
                                   
    /** The instance. */
    private static final Object    instance = new Object();
                                            
    // respective logger
    private static final Logger    LOG      = Logger.getLogger();
                                            
    /**
     * adds a org.ssh.models to this manager
     * 
     * @param org.ssh.models
     */
    public static void add(final Model model) {
        Models.modelController.add(model);
    }
    
    public static Model create(final Class<?> clazz, final Object... args) {
        return ModelController.create(clazz, args);
    }
    
    /**
     * This method finds a org.ssh.models and returns it as a Model.
     * 
     * @param modelName
     *            The name of the model you want to find.
     * @return The requested model.
     */
    public static Optional<Model> get(final String modelName) {
        return Models.modelController.get(modelName);
    }
    
    /**
     * @return all Models currently in the modelcontroller
     */
    public static List<Model> getAll() {
        return Models.modelController.getAll();
    }
    
    /**
     * This method finds all models matching the name and returns them as an List<Model>
     * 
     * @param modelName
     *            The (fuzzy) name of the model you want to find.
     * @return The requested models.
     */
    public static List<Model> getAll(final String modelName) {
        return Models.modelController.getAll(modelName);
    }
    
    /**
     * Gets the Singleton instance of Models.
     *
     * @return The single instance.
     */
    public static Object getInstance() {
        return Models.instance;
    }
    
    /**
     * Initialize all values in the configfile for given org.ssh.models
     * 
     * @param model
     *            model to initialize
     * @return success value
     */
    public static boolean initialize(final Model model) {
        return Models.modelController.load(model);
    }
    
    /**
     * Initialize all values in the configfile for each org.ssh.models
     * 
     * @return success value
     */
    public static boolean initializeAll() {
        return Models.modelController.initializeAll();
    }
    
    /**
     * Set all non-{@link Modifier#TRANSIENT transient} fields of given org.ssh.models to null, and
     * reload values from configfile
     * 
     * @param model
     *            to reinitialize
     * @return success value
     */
    public static boolean reinitialize(final Model model) {
        return Models.modelController.reinitialize(model);
    }
    
    /**
     * Set all non-{@link Modifier#TRANSIENT transient} fields for all models to null, and reload
     * values from configfile for every org.ssh.models
     * 
     * @return success value
     */
    public static boolean reinitializeAll() {
        return Models.modelController.reinitializeAll();
    }
    
    /**
     * Save the current state of the org.ssh.models in profiles path
     * 
     * @param org.ssh.models
     *            org.ssh.models to save
     * @return success value
     */
    public static boolean save(final Model model) {
        return Models.modelController.save(model);
    }
    
    /**
     * Save given org.ssh.models as default for this org.ssh.models type
     * 
     * @param org.ssh.models
     * @return succes value
     */
    public static boolean saveAsDefault(final Model model) {
        return Models.modelController.saveAsDefault(model);
    }
    
    /**
     * This method instantiates a controller to run the store.
     */
    public static void start() {
        Models.LOG.info("Starting Models...");
        
        // create modelController
        Models.modelController = new ModelController();
        // create a settings models (will self-assign in the factory)
        Models.create(Settings.class);
    }
}