package org.ssh.models;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ssh.managers.Models;
import org.ssh.util.Logger;
import org.ssh.util.Reflect;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * The Class ModelController. manages all {@link Model Models}. Also contains the factory (
 * {@link #create(Class, Object...)}) to create instances for {@link Model Models} that will
 * register to the {@link ModelController}
 *
 * @author Jeroen
 */
public class ModelController {
    
    // Respective logger
    private static Logger logger = Logger.getLogger();
    
    /**
     * Create a Model instance based on given class, with given arguments.<br />
     * Registers the created instance by {@link Models#add(Model) Models} and
     * {@link Models#initialize(Model) initializes} the org.ssh.models.
     * 
     * @param clazz
     * @param args
     * @return
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
        catch (final Exception e) {
            // either clazz isn't a org.ssh.models, or the constructor doesn't exist
            ModelController.logger.warning("Could not create Model %s", clazz);
            return null;
        }
    }
    
    /** List with all Models */
    private final ArrayList<Model> models = new ArrayList<Model>();
    /** Builder for reading and writing Objects to Json **/
    private final Gson             gson   = new GsonBuilder().setPrettyPrinting().create();
                                          
    /**
     * Settings for this specific controller ({@link #add(Model)} adds the settings)
     **/
    private Settings               settings;
                                   
    /**
     * Instantiates a new org.ssh.models controller.
     */
    public ModelController() {
    }
    
    /**
     * Add a org.ssh.models to this controller
     * 
     * @param org.ssh.models
     */
    public void add(final Model model) {
        ModelController.logger.info("Adding %s", model.getClass());
        // whenever it is a settings org.ssh.models, overwrite our own settings
        if (model instanceof Settings) this.settings = (Settings) model;
        
        // add it tothe arraylist
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
        ModelController.logger.info("Searching for configfile %s", filename);
        // check for a custom file
        File config = new File(this.settings.getProfilePath() + filename);
        if ((config.exists() && !config.isDirectory())) return Optional.of(config.toPath());
        
        // check for a default one
        config = new File(this.settings.getDefaultPath() + filename);
        if ((config.exists() && !config.isDirectory())) return Optional.of(config.toPath());
        
        // none found
        return Optional.empty();
    }
    
    private Map<String, Object> generateFieldMap(final Object obj) {
        return Stream.of(obj.getClass().getDeclaredFields())
                .filter(field -> !Modifier.isTransient(field.getModifiers()))
                .collect(Collectors.toMap(Field::getName, null));
    }
    
    /**
     * This method finds a org.ssh.models with the given name and returns it as a Model.
     * 
     * @param modelName
     *            The name of the org.ssh.models you want to find.
     * @return The requested org.ssh.models.
     */
    public Optional<Model> get(final String name) {
        return this.models.stream().filter(model -> model.getFullName().trim().equals(name.trim())).findFirst();
    }
    
    /**
     * @return all Models currently in the modelcontroller
     */
    public ArrayList<Model> getAll() {
        return this.models;
    }
    
    /**
     * This method finds all models matching the name and returns them as an ArrayList<Model>
     * 
     * @param modelName
     *            The (fuzzy) name of the org.ssh.models you want to find.
     * @return The requested org.ssh.models.
     */
    public ArrayList<Model> getAll(final String name) {
        return (ArrayList<Model>) this.models.stream().filter(model -> model.getFullName().equals(name))
                .collect(Collectors.toList());
    }
    
    /**
     * Initialize all values in the configfile for each org.ssh.models
     * 
     * @return success value
     */
    public boolean initializeAll() {
        return this.models.stream().map(model -> this.load(model)).reduce(true,
                (accumulator, succes) -> succes && accumulator);
    }
    
    /**
     * loads all values in the configfile for given org.ssh.models
     * 
     * @param org.ssh.models
     *            org.ssh.models to initialize
     * @return success value
     */
    public boolean load(final Model model) {
        // try to find a configfile
        final Optional<Path> configFile = this.findValidPath(model.getConfigName());
        
        // check whether a config has been found
        if (!configFile.isPresent()) {
            ModelController.logger.info("No configfile found for %s", model.getConfigName());
            return false;
        }
        
        // try to read the values
        final Optional<Map<String, Object>> map = this.readConfig(configFile.get(), model.getClass());
        ModelController.logger.info("Reading configfile %s", configFile.get());
        
        // reading failed?
        if (!map.isPresent()) {
            ModelController.logger.info("Reading failed for %s", configFile);
            return false;
        }
        
        // everything should be pushed towards the org.ssh.models
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
                    .filter(e -> Reflect.getField(e.getKey(), clazz).isPresent()).collect(
                            // key stays the same
                            Collectors.toMap(e -> e.getKey(),
                                    // Typecast 'gson'-Object to a Java-Object
                                    // for use in Model
                                    e -> (Object) this.gson.fromJson(e.getValue().toString(),
                                            Reflect.getField(e.getKey(), clazz).get().getType())));
            return Optional.of(map);
            
        }
        catch (JsonSyntaxException | IOException e) {
            return Optional.empty();
        }
    }
    
    /**
     * Set all non-{@link Modifier#TRANSIENT transient} fields of given org.ssh.models to null, and
     * reload values from configfile
     * 
     * @param org.ssh.models
     *            to reinitialize
     * @return success value
     */
    public boolean reinitialize(final Model model) {
        model.update(this.generateFieldMap(model));
        return this.load(model);
    }
    
    /**
     * Set all non-{@link Modifier#TRANSIENT transient} fields for all models to null, and reload
     * values from configfile for every org.ssh.models
     * 
     * @return success value
     */
    public boolean reinitializeAll() {
        return this.models.stream().map(model -> this.reinitialize(model)).reduce(true,
                (accumulator, succes) -> succes && accumulator);
    }
    
    /**
     * Save the current state of the org.ssh.models in profiles path
     * 
     * @param org.ssh.models
     *            org.ssh.models to save
     * @return success value
     */
    public boolean save(final Model model) {
        return this.saveAs(this.settings.getProfilePath() + model.getConfigName(), model);
    }
    
    /**
     * Save given org.ssh.models to a specific filepath
     * 
     * @param filePath
     *            filepath to save to
     * @param org.ssh.models
     *            org.ssh.models to save
     * @return succes value
     */
    public boolean saveAs(final String filePath, final Model model) {
        try {
            ModelController.logger.info("Saving as %s", filePath);
            // open configfile
            final File configFile = new File(filePath);
            // make dirs recursively
            configFile.getParentFile().mkdirs();
            // create file if it doesn't already
            if (!configFile.exists()) configFile.createNewFile();
            // write gson-object of all data in org.ssh.models to configFile
            Files.write(this.gson.toJson(model), configFile, Charset.defaultCharset());
            return true;
        }
        catch (final IOException e) {
            return false;
        }
    }
    
    /**
     * Save given org.ssh.models as default for this org.ssh.models type
     * 
     * @param org.ssh.models
     *            to save
     * @return succes value
     */
    public boolean saveAsDefault(final Model model) {
        final Settings settings = (Settings) Models.get("settings").get();
        return this.saveAs(settings.getDefaultPath() + model.getConfigName(), model);
    }
}
