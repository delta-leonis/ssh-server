package org.ssh.managers.controllers;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.beans.property.*;
import org.ssh.controllers.ControllerLayout;
import org.ssh.controllers.ControllerLayoutSerializer;
import org.ssh.managers.AbstractManageable;
import org.ssh.managers.AbstractManagerController;
import org.ssh.models.AbstractModel;
import org.ssh.models.PropertyTypeAdapter;
import org.ssh.models.Settings;
import org.ssh.util.Logger;
import org.ssh.util.Reflect;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

/**
 * The Class ModelController. manages all {@link AbstractModel Models}.
 *
 *       
 * @author Jeroen de Jong
 */
public class ModelController extends AbstractManagerController<AbstractModel> {
    
    /** Builder for reading and writing Objects to Json **/
    private final Gson          gson;
                                
    /**
     * Settings for this specific controller ({@link #add(AbstractModel)} adds the settings)
     **/
    private Settings            settings;
    // Respective logger
    private static final Logger LOG = Logger.getLogger();
    
    /**
     * {@inheritDoc}
     * 
     * When a {@link Settings} model is added, it is being set as default settings for this
     * controller
     */
    @Override
    public boolean put(final String name, final AbstractModel manageable) {
        if (manageable instanceof Settings)
            this.settings = (Settings) manageable;
        
        return super.put(name, manageable);
    }

    /**
     * Instantiates a new models controller.
     */
    public ModelController() {
        this.gson = new GsonBuilder()
            .registerTypeAdapter(ControllerLayout.class, new ControllerLayoutSerializer())
            .registerTypeAdapter(BooleanProperty.class, new PropertyTypeAdapter<>())
            .registerTypeAdapter(IntegerProperty.class, new PropertyTypeAdapter<>())
            .registerTypeAdapter(LongProperty.class, new PropertyTypeAdapter<>())
            .registerTypeAdapter(FloatProperty.class, new PropertyTypeAdapter<>())
            .registerTypeAdapter(DoubleProperty.class, new PropertyTypeAdapter<>())
            .registerTypeAdapter(StringProperty.class, new PropertyTypeAdapter<>())
            .registerTypeAdapter(ObjectProperty.class, new PropertyTypeAdapter<>())
            .registerTypeAdapter(ListProperty.class, new PropertyTypeAdapter<>())
            .setPrettyPrinting()
            .create();
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
        
        // for the settings.json
        File config = new File(this.settings.getProfilesPath() + filename);
        if ((config.exists() && !config.isDirectory())) return Optional.of(config.toPath());
        
        // check for a custom file
        config = new File(this.settings.getCurrentWorkspacePath() + filename);
        if ((config.exists() && !config.isDirectory())) return Optional.of(config.toPath());
        
        // check for a default one
        config = new File(this.settings.getDefaultPath() + filename);
        if ((config.exists() && !config.isDirectory())) return Optional.of(config.toPath());
        
        // none found
        return Optional.empty();
    }

    /**
     * Initialize all values in the configfile for each models
     * 
     * @return success value
     */
    public boolean initializeAll() {
        return this.manageables.values().stream().map(model -> this.load(model)).reduce(true,
                (accumulator, success) -> success && accumulator);
    }
    
    /**
     * loads all values in the configfile for given model
     * 
     * @param model
     *            model to initialize
     * @return success value
     */
    public boolean load(final AbstractModel model) {
        // try to find a configfile
        final Optional<Path> configFile = this.findValidPath(model.getConfigName());
        
        // check whether a config has been found
        if (!configFile.isPresent()) {
            ModelController.LOG.info("No configfile found for %s", model.getConfigName());
            return false;
        }

        return load(model, configFile.get());
    }
    
    /**
     * Loads a certain configfile to a model
     * 
     * @param model
     *            model to use
     * @param configFile
     *            configfile to read
     * @return succes value
     */
    public boolean load(final AbstractModel model, Path configFile) {
        try {
            // read all JSON to a reader
            JsonReader configReader = new JsonReader(
                    new StringReader(new String(java.nio.file.Files.readAllBytes(configFile), StandardCharsets.UTF_8).trim()));
            // chill out will ya ?
            configReader.setLenient(true);
            // load everything into a new model
            AbstractModel newModel = this.gson.fromJson(configReader, model.getClass());
            // create a updatemap from the newModel, and update the given model with all these
            // changes.
            // !!! This way all transient fields will be untouched, as is preferable
            return model.update(newModel.toMap());
        }
        catch (JsonSyntaxException exception) {
            ModelController.LOG.exception(exception);
            ModelController.LOG.warning(
                    "Could not parse gson in '%s' for model '%s'.%nPlease check that no primitives fields are being used.",
                    configFile,
                    model.getName());
            return false;
        }
        catch (IOException exception) {
            ModelController.LOG.exception(exception);
            ModelController.LOG.warning("Could not read '%s' for model '%s'.", configFile, model.getName());
            return false;
        }
        catch(Exception exception) {
            ModelController.LOG.exception(exception);
            ModelController.LOG.warning("Could not read '%s' for model '%s', Generic exception occured", configFile, model.getName());
            return false;
        }
    }

    
    /**
     * Set all non-{@link Modifier#TRANSIENT transient} fields of given models to null, and reload
     * values from configfile.
     * 
     * @param model
     *            to reinitialize
     * @return success value
     */
    public boolean reinitialize(final AbstractModel model) {
        model.reset(Reflect.fieldList(model.getClass()));
        model.initialize();
        return this.load(model);
    }
    
    /**
     * Set all non-{@link Modifier#TRANSIENT transient} fields for all models to null, and reload
     * values from configfile for every models.
     * 
     * @return success value
     */
    public boolean reinitializeAll() {
        return this.manageables.values().stream().map(model -> this.reinitialize(model)).reduce(true,
                (accumulator, success) -> success && accumulator);
    }
    
    /**
     * Save the current state of the models in profiles path.
     * 
     * @param model
     *            models to save
     * @return success value
     */
    public boolean save(final AbstractModel model) {
        return this.saveAs(this.settings.getCurrentWorkspacePath() + model.getConfigName(), model);
    }
    
    /**
     * Save given models to a specific filepath.
     * 
     * @param filePath
     *            filepath to save to
     * @param model
     *            models to save
     * @return succes value
     */
    public boolean saveAs(final String filePath, final AbstractModel model) {
        try {
            ModelController.LOG.info("Saving %s as %s", model.getClass().getName(), filePath);
            // open configfile
            final File configFile = new File(filePath);
            // make dirs recursively
            if (!configFile.getParentFile().isDirectory())
                if (!configFile.getParentFile().mkdirs()) 
                    throw new IOException("Could not create (parent) dirs.");
            // create file if it doesn't already
            if (!configFile.exists())
                if (!configFile.createNewFile()) 
                    throw new IOException("Could not create configfile.");
            // write gson-object of all data in models to configFile
            Files.write(this.gson.toJson(model), configFile, Charset.defaultCharset());
            return true;
        }
        catch (final IOException exception) {
            // either the creation of the dir(s) failed, or the writing
            // to a file failed.
            ModelController.LOG.exception(exception);
            ModelController.LOG.warning("Could not save %s.", filePath);
            return false;
        }
    }
    
    /**
     * Save given models as default for this models type.
     * 
     * @param model
     *            to save
     * @return succes value
     */
    public boolean saveAsDefault(final AbstractModel model) {
        return this.saveAs(this.settings.getDefaultPath() + model.getConfigName(), model);
    }
}