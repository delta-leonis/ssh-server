package org.ssh.managers.controllers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ssh.managers.manager.Models;
import org.ssh.models.Model;
import org.ssh.models.Settings;
import org.ssh.util.Logger;
import org.ssh.util.Reflect;

import com.google.common.io.Files;
import com.google.common.reflect.Reflection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * The Class ModelController. manages all {@link Model Models}. Also contains the factory (
 * {@link #create(Class, Object...)}) to create instances for {@link Model Models} that will
 * register to the {@link ModelController}.
 *
 * @TODO implement fuzzy searching
 * @TODO replace {@link Reflect} with {@link Reflection} in {@link #readConfig(Path, Class)}
 *       
 * @author Jeroen de Jong
 */
public class ModelController {
    
    /** List with all Models */
    private final List<Model>   models;
    /** Builder for reading and writing Objects to Json **/
    private final Gson          gson;
                                
    /**
     * Settings for this specific controller ({@link #add(Model)} adds the settings)
     **/
    private Settings            settings;
    // Respective logger
    private static final Logger LOG = Logger.getLogger();
                                    
    /**
     * Create a Model instance based on given class, with given arguments.<br />
     * Registers the created instance by {@link Models#add(Model) Models} and
     * {@link Models#initialize(Model) initializes} the models.
     * 
     * @param clazz
     *            class to create
     * @param args
     *            arguments that will be passed to the constructor (supply them in the right order).
     * @return a created Model
     */
    public static Model create(final Class<?> clazz, final Object... args) {
        try {
            // get all Types for the arguments
            final Class<?>[] cArgs = new Class[args.length];
            for (int i = 0; i < args.length; i++)
                cArgs[i] = args[i].getClass();
                
            // call constructor and cast instance
            final Model model = (Model) clazz.getDeclaredConstructor(cArgs).newInstance(args);
            
            // add org.ssh.models to ModelController
            Models.add(model);
            // initialize this org.ssh.models
            Models.initialize(model);
            return model;
        }
        catch (final Exception exception) {
            ModelController.LOG.exception(exception);
            // either clazz isn't a org.ssh.models, or the constructor doesn't exist
            ModelController.LOG.warning("Could not create Model %s", clazz);
            return null;
        }
    }
    
    /**
     * Instantiates a new org.ssh.models controller.
     */
    public ModelController() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        models = new ArrayList<Model>();
    }
    
    /**
     * Add a models to this controller
     * 
     * @param models
     */
    public void add(final Model model) {
        ModelController.LOG.info("Adding %s", model.getClass());
        // whenever it is a settings models, overwrite our own settings
        if (model instanceof Settings) this.settings = (Settings) model;
        
        // add it to the arraylist
        this.models.add(model);
    }
    
    /**
     * Try to find the path for the configfile. Will search in the profilePath first, and
     * defaultPath second as defined in Settings
     * 
     * @param filename
     *            filename to find
     * @return filename if found, otherwise Optional.empty()
     */
    private Optional<Path> findValidPath(final String filename) {
        ModelController.LOG.info("Searching for configfile %s", filename);
        // check for a custom file
        File config = new File(this.settings.getProfilePath() + filename);
        if ((config.exists() && !config.isDirectory())) return Optional.of(config.toPath());
        
        // check for a default one
        config = new File(this.settings.getDefaultPath() + filename);
        if ((config.exists() && !config.isDirectory())) return Optional.of(config.toPath());
        
        // none found
        return Optional.empty();
    }
    
    /**
     * Generate a map of all non-transient {@link Field fields}, and fill them with null
     * 
     * @param obj
     *            Object to generate map for
     * @return a map compaitble with update method in Model (see {@link Models.update(map)})
     */
    private Map<String, Object> generateFieldMap(final Object obj) {
        return Stream.of(obj.getClass().getDeclaredFields())
                // filter all non-transient fields
                .filter(field -> !Modifier.isTransient(field.getModifiers()))
                // collect them to a map (key = field, value = null)
                .collect(Collectors.toMap(Field::getName, null));
    }
    
    /**
     * This method finds a models with the given name and returns it as a Model.
     * 
     * @param modelName
     *            The name of the models you want to find.
     * @return The requested models.
     */
    public Optional<Model> get(final String name) {
        return this.models.stream()
                // trim the fullname, and compare it to the argument
                .filter(model -> model.getFullName().trim().equals(name.trim()))
                // find the first one
                .findFirst();
    }
    
    /**
     * @return all Models currently in the modelcontroller
     */
    public List<?> getAll() {
        return this.models;
    }
    
    /**
     * This method finds all models matching the name and returns them as an ArrayList<Model>
     * 
     * @param modelName
     *            The (fuzzy) name of the models you want to find.
     * @return The requested models.
     */
    public List<?> getAll(final String name) {
        return this.models.stream().filter(model -> model.getName().equals(name)).collect(Collectors.toList());
    }
    
    /**
     * Initialize all values in the configfile for each models
     * 
     * @return success value
     */
    public boolean initializeAll() {
        return this.models.stream().map(model -> this.load(model)).reduce(true,
                (accumulator, succes) -> succes && accumulator);
    }
    
    /**
     * loads all values in the configfile for given models
     * 
     * @param models
     *            models to initialize
     * @return success value
     */
    public boolean load(final Model model) {
        // try to find a configfile
        final Optional<Path> configFile = this.findValidPath(model.getConfigName());
        
        // check whether a config has been found
        if (!configFile.isPresent()) {
            ModelController.LOG.info("No configfile found for %s", model.getConfigName());
            return false;
        }
        
        // try to read the values
        final Optional<Map<String, Object>> map = this.readConfig(configFile.get(), model.getClass());
        ModelController.LOG.info("Reading configfile %s", configFile.get());
        
        // reading failed?
        if (!map.isPresent()) {
            ModelController.LOG.info("Reading failed for %s", configFile);
            return false;
        }
        
        // everything should be pushed towards the models
        return model.update(map.get());
    }
    
    /**
     * Read a configfile, and parse the gson-objects to the corresponding object-types to fields in
     * given class
     * 
     * @param configFile
     *            configfile to read
     * @param clazz
     *            class for reference objecttypes
     * @return Optional.empty() when reading failed, Map otherwise
     */
    private Optional<Map<String, Object>> readConfig(final Path configFile, final Class<?> clazz) {
        try {
            final Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            
            // Typetoken ensures right type, so suppresWarnings is authorized
            // here
            @SuppressWarnings ("unchecked")
            final Map<String, Object> map = ((Map<String, Object>)
            // read everything and cast to a Entry<String, Object> in which
            // String represents the fieldname
            // and Object represents the Gson object, that represents the Object
            // in the field
            this.gson.fromJson(new String(java.nio.file.Files.readAllBytes(configFile)), type)).entrySet().stream()
                    // filter everything that hasn't got a field
                    .filter(entry -> Reflect.getField(entry.getKey(), clazz).isPresent()).collect(
                            // key stays the same
                            Collectors.toMap(entry -> entry.getKey(),
                                    // Typecast 'gson'-Object to a Java-Object
                                    // for use in Model
                                    entry -> (Object) this.gson.fromJson(entry.getValue().toString(),
                                            Reflect.getField(entry.getKey(), clazz).get().getType())));
            return Optional.of(map);
            
        }
        catch (JsonSyntaxException | IOException exception) {
            ModelController.LOG.exception(exception);
            ModelController.LOG.warning("Could not read '%s' for model '%s'.", configFile, clazz.getName());
            return Optional.empty();
        }
    }
    
    /**
     * Set all non-{@link Modifier#TRANSIENT transient} fields of given models to null, and reload
     * values from configfile.
     * 
     * @param models
     *            to reinitialize
     * @return success value
     */
    public boolean reinitialize(final Model model) {
        model.update(this.generateFieldMap(model));
        return this.load(model);
    }
    
    /**
     * Set all non-{@link Modifier#TRANSIENT transient} fields for all models to null, and reload
     * values from configfile for every models.
     * 
     * @return success value
     */
    public boolean reinitializeAll() {
        return this.models.stream().map(model -> this.reinitialize(model)).reduce(true,
                (accumulator, succes) -> succes && accumulator);
    }
    
    /**
     * Save the current state of the models in profiles path.
     * 
     * @param models
     *            models to save
     * @return success value
     */
    public boolean save(final Model model) {
        return this.saveAs(this.settings.getProfilePath() + model.getConfigName(), model);
    }
    
    /**
     * Save given models to a specific filepath.
     * 
     * @param filePath
     *            filepath to save to
     * @param models
     *            models to save
     * @return succes value
     */
    public boolean saveAs(final String filePath, final Model model) {
        try {
            ModelController.LOG.info("Saving %s as %s", model.getClass().getName(), filePath);
            // open configfile
            final File configFile = new File(filePath);
            // make dirs recursively
            if (!configFile.getParentFile().isDirectory())
                if (!configFile.getParentFile().mkdirs()) throw new IOException();
            // create file if it doesn't already
            if (!configFile.exists()) configFile.createNewFile();
            // write gson-object of all data in models to configFile
            Files.write(this.gson.toJson(model), configFile, Charset.defaultCharset());
            return true;
        }
        catch (final IOException exception) {
            // either the creation of the dir(s) failed, or the writing
            // to a file failed.
            ModelController.LOG.setLevel(Level.FINEST);
            ModelController.LOG.exception(exception);
            ModelController.LOG.warning("Could not save %s.", filePath);
            return false;
        }
    }
    
    /**
     * Save given models as default for this models type.
     * 
     * @param models
     *            to save
     * @return succes value
     */
    public boolean saveAsDefault(final Model model) {
        return this.saveAs(this.settings.getDefaultPath() + model.getConfigName(), model);
    }
}