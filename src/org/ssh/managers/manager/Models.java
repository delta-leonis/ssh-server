package org.ssh.managers.manager;

import org.ssh.managers.ManagerInterface;
import org.ssh.managers.controllers.ModelController;
import org.ssh.models.AbstractModel;
import org.ssh.models.Settings;
import org.ssh.ui.lua.console.AvailableInLua;
import org.ssh.util.Logger;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The Class Models. Contains the manager for all {@link AbstractModel Models} that are in use. Also contains the factory (
 * {@link #create(Class, Object...)} ) to create instances for {@link AbstractModel Models} that will
 * register to the {@link ModelController}.
 *
 * @author Jeroen de Jong
 * @see #create(Class, Object...)
 * @see #get(String)
 *
 */
@AvailableInLua
public final class Models implements ManagerInterface<AbstractModel> {

    /**
     * The models store has a controller that runs the store.
     */
    private static ModelController controller;

    /** The instance. */
    private static final Object    instance = new Object();

    /** The Constant LOG. */
    // respective logger
    private static final Logger    LOG      = Logger.getLogger();

    /**
     * Private constructor to hide the implicit public one.
     */
    private Models() {
    }

    /**
     * This method instantiates a controller to run the store.
     */
    public static void start() {
        Models.LOG.info("Starting Models...");

        // create controller
        Models.controller = new ModelController();
        // create a settings models (will self-assign in the factory)
        Models.create(Settings.class);
    }
    /**
     * Create a Model instance based on given class, with given arguments.<br />
     * Registers the created instance by {@link Models#add(AbstractModel) Models} and
     * {@link Models#initialize(AbstractModel) initializes} the models.
     *
     * @param clazz
     *            class to create
     * @param args
     *            arguments that will be passed to the constructor (supply them in the right order).
     * @return a created Model
     */
    public static <M extends AbstractModel> M create(final Class<?> clazz, final Object... args) {
        Class<?>[] cArgs = new Class[args.length];
        try {
            // get all Types for the arguments
            for (int i = 0; i < args.length; i++)
                cArgs[i] = args[i].getClass();

            // call constructor and cast instance
            M model = (M) clazz.getDeclaredConstructor(cArgs).newInstance(args);
            LOG.info("created %s", clazz);

            // add model to ModelController
            Models.add(model);
            // initialize this models
            if(!Models.initialize(model))
                Models.LOG.info("Could not initialize %s.", clazz.getSimpleName());
            return model;
        }
        catch (java.lang.NoSuchMethodException exception) {
            Models.LOG.exception(exception);
            // either clazz isn't a models, or the constructor doesn't exist
            Models.LOG.warning("Could not create Model %s%nDoes the constructor %s(%s) exist?",
                    clazz, clazz.getSimpleName(),
                    cArgs.length > 0 ? Arrays.toString(cArgs).replace("class ", "") : "" );
        }
        catch (Exception exception) {
            Models.LOG.exception(exception);
            Models.LOG.warning("Could not create Model %s", clazz);
        }
        return null;
    }

    /**
     * adds a models to this manager
     *
     * @param model
     *            the model
     */
    public static void add(final AbstractModel model) {
        Models.LOG.info("Toegevoegt met id: %s", model.getIdentifier());
        Models.controller.put(model.getIdentifier(), model);
    }

    /**
     * This method finds a models and returns it as a Model.
     *
     * @param modelName
     *            The full name of the model you want to find.
     * @return The requested model.
     */
    public static <M extends AbstractModel> Optional<M> get(final String modelName) {
        return Models.controller.<M> get(modelName);
    }

    /**
     * @return all Models currently in the modelcontroller
     */
    public static <M extends AbstractModel> List<M> getAll() {
        return Models.controller.getAll();
    }

    public static <M extends AbstractModel> List<M> getAll(String type) {
        return (List<M>) Models.getAll().stream()
                .filter(model -> model.getName().equals(type))
                .collect(Collectors.toList());
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
     * Initialize all values in the configfile for given models
     *
     * @param model
     *            model to initialize
     * @return success value
     */
    public static boolean initialize(final AbstractModel model) {
        model.initialize();
        return Models.controller.load(model);
    }

    /**
     * Initialize all values in the configfile for each models
     *
     * @return success value
     */
    public static boolean initializeAll() {
        return Models.controller.initializeAll();
    }

    /**
     * Set all non-{@link Modifier#TRANSIENT transient} fields of given models to null, and
     * reload values from configfile
     *
     * @param model
     *            to reinitialize
     * @return success value
     */
    public static boolean reinitialize(final AbstractModel model) {
        return Models.controller.reinitialize(model);
    }

    /**
     * Set all non-{@link Modifier#TRANSIENT transient} fields for all models to null, and reload
     * values from configfile for every models
     *
     * @return success value
     */
    public static boolean reinitializeAll() {
        return Models.controller.reinitializeAll();
    }

    /**
     * Save the current state of the models in profiles path
     *
     * @param model
     *            the model
     * @return success value
     */
    public static boolean save(final AbstractModel model) {
        return Models.controller.save(model);
    }

    /**
     * Save given models as default for this models type
     *
     * @param model
     *            the model
     * @return succes value
     */
    public static boolean saveAsDefault(final AbstractModel model) {
        return Models.controller.saveAsDefault(model);
    }

    /**
     * Remove a certain model from the face of this earth.
     *
     * @param model model to remove
     * @return model if it exists (otherwise null)
     */
    public static <M extends AbstractModel> M remove(final M model){
        return Models.controller.remove(model);
    }

    /**
     * Removes a {@link AbstractModel} with the specified key from the list of Manageables.
     *
     * @param name The key belonging to the Manageable.
     * @param <M>  The type of Manageable requested by the user.
     * @return The removed Manageable.
     */
    public static <M extends AbstractModel> M remove(final String name) {
        return Models.controller.remove(name);
    }

    /**
     * Finds all the Models whose true name matches the given pattern.
     * @param pattern   The pattern to match on.
     * @param <M>       The type of Model requested by the user.
     * @return          The list of Models matching the given pattern.
     */
    public static <M extends AbstractModel> List<M> find(final String pattern) {
        return Models.controller.find(pattern);
    }
}