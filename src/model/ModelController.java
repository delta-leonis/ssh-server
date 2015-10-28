package model;

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

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import application.Models;
import util.Logger;
import util.Reflect;

/**
 * The Class ModelController. manages all {@link Model Models}. Also contains
 * the factory ({@link #create(Class, Object...)}) to create instances for
 * {@link Model Models} that will register to the {@link ModelController}
 * 
 * @author Jeroen
 */
public class ModelController {
	// Respective logger
	private static Logger logger = Logger.getLogger();
	/** List with all Models */
	private ArrayList<Model> models = new ArrayList<Model>();
	/** Builder for reading and writing Objects to Json **/
	private Gson gson = new GsonBuilder().setPrettyPrinting().create();
	/**
	 * Settings for this specific controller ({@link #add(Model)} adds the
	 * settings)
	 **/
	private Settings settings;

	/**
	 * Instantiates a new model controller.
	 */
	public ModelController() {
	}

	/**
	 * Add a model to this controller
	 * 
	 * @param model
	 */
	public void add(Model model) {
		logger.info("Adding %s", model.getClass());
		// whenever it is a settings model, overwrite our own settings
		if (model instanceof Settings)
			settings = (Settings) model;

		// add it tothe arraylist
		models.add(model);
	}

	/**
	 * Create a Model instance based on given class, with given arguments.<br />
	 * Registers the created instance by {@link Models#add(Model) Models} and
	 * {@link Models#initialize(Model) initializes} the model.
	 * 
	 * @param clazz
	 * @param args
	 * @return
	 */
	public static Model create(Class<?> clazz, Object... args) {
		try {
			// get all Types for the arguments
			Class<?>[] cArgs = new Class[args.length];
			for (int i = 0; i < args.length; i++)
				cArgs[i] = args[i].getClass();

			// call constructor and cast instance
			Model model = (Model) clazz.getDeclaredConstructor(cArgs).newInstance(args);

			// add model to ModelController
			Models.add(model);
			// initialize this model
			Models.initialize(model);
			return model;
		} catch (Exception e) {
			// either clazz isn't a model, or the constructor doesn't exist
			logger.warning("Could not create Model %s", clazz);
			return null;
		}
	}

	/**
	 * This method finds a model with the given name and returns it as a Model.
	 * 
	 * @param modelName
	 *            The name of the model you want to find.
	 * @return The requested model.
	 */
	public Optional<Model> get(String name) {
		return models.stream().filter(model -> model.getFullName().trim().equals(name.trim())).findFirst();
	}

	/**
	 * This method finds all models matching the name and returns them as an
	 * ArrayList<Model>
	 * 
	 * @param modelName
	 *            The (fuzzy) name of the model you want to find.
	 * @return The requested model.
	 */
	public ArrayList<Model> getAll(String name) {
		return (ArrayList<Model>) models.stream().filter(model -> model.getFullName().equals(name))
				.collect(Collectors.toList());
	}

	/**
	 * @return all Models currently in the modelcontroller
	 */
	public ArrayList<Model> getAll() {
		return models;
	}

	/**
	 * Save the current state of the model in profiles path
	 * 
	 * @param model
	 *            model to save
	 * @return success value
	 */
	public boolean save(Model model) {
		return saveAs(settings.getProfilePath() + model.getConfigName(), model);
	}

	/**
	 * Save given model as default for this model type
	 * 
	 * @param model
	 *            to save
	 * @return succes value
	 */
	public boolean saveAsDefault(Model model) {
		Settings settings = (Settings) Models.get("settings").get();
		return saveAs(settings.getDefaultPath() + model.getConfigName(), model);
	}

	/**
	 * Save given model to a specific filepath
	 * 
	 * @param filePath
	 *            filepath to save to
	 * @param model
	 *            model to save
	 * @return succes value
	 */
	public boolean saveAs(String filePath, Model model) {
		try {
			logger.info("Saving as %s", filePath);
			// open configfile
			File configFile = new File(filePath);
			// make dirs recursively
			configFile.getParentFile().mkdirs();
			// create file if it doesn't already
			if (!configFile.exists())
				configFile.createNewFile();
			// write gson-object of all data in model to configFile
			Files.write(gson.toJson(model), configFile, Charset.defaultCharset());
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Initialize all values in the configfile for each model
	 * 
	 * @return success value
	 */
	public boolean initializeAll() {
		return models.stream().map(model -> load(model)).reduce(true, (accumulator, succes) -> succes && accumulator);
	}

	/**
	 * Set all non-{@link Modifier#TRANSIENT transient} fields of given model to
	 * null, and reload values from configfile
	 * 
	 * @param model
	 *            to reinitialize
	 * @return success value
	 */
	public boolean reinitialize(Model model) {
		model.update(generateFieldMap(model));
		return load(model);
	}

	/**
	 * Set all non-{@link Modifier#TRANSIENT transient} fields for all models to
	 * null, and reload values from configfile for every model
	 * 
	 * @return success value
	 */
	public boolean reinitializeAll() {
		return models.stream().map(model -> reinitialize(model)).reduce(true,
				(accumulator, succes) -> succes && accumulator);
	}

	private Map<String, Object> generateFieldMap(Object obj) {
		return Stream.of(obj.getClass().getDeclaredFields())
				.filter(field -> !Modifier.isTransient(field.getModifiers()))
				.collect(Collectors.toMap(Field::getName, null));
	}

	/**
	 * loads all values in the configfile for given model
	 * 
	 * @param model
	 *            model to initialize
	 * @return success value
	 */
	public boolean load(Model model) {
		// try to find a configfile
		Optional<Path> configFile = findValidPath(model.getConfigName());

		// check whether a config has been found
		if (!configFile.isPresent()) {
			logger.info("No configfile found for %s", model.getConfigName());
			return false;
		}

		// try to read the values
		Optional<Map<String, Object>> map = readConfig(configFile.get(), model.getClass());
		logger.info("Reading configfile %s", configFile.get());

		// reading failed?
		if (!map.isPresent()) {
			logger.info("Reading failed for %s", configFile);
			return false;
		}

		// everything should be pushed towards the model
		return model.update(map.get());
	}

	/**
	 * Read a configfile, and parse the gson-objects to the corresponding
	 * object-types to fields in given class
	 * 
	 * @param configFile
	 *            configfile to read
	 * @param clazz
	 *            class for reference objecttypes
	 * @return Optional.empty() when reading failed, Map otherwise
	 */
	private Optional<Map<String, Object>> readConfig(Path configFile, Class<?> clazz) {
		try {
			Type type = new TypeToken<Map<String, Object>>() {
			}.getType();

			// Typetoken ensures right type, so suppresWarnings is authorized
			// here
			@SuppressWarnings("unchecked")
			Map<String, Object> map = ((Map<String, Object>)
			// read everything and cast to a Entry<String, Object> in which
			// String represents the fieldname
			// and Object represents the Gson object, that represents the Object
			// in the field
			gson.fromJson(new String(java.nio.file.Files.readAllBytes(configFile)), type)).entrySet().stream()
					// filter everything that hasn't got a field
					.filter(e -> Reflect.getField(e.getKey(), clazz).isPresent()).collect(
							// key stays the same
							Collectors.toMap(e -> e.getKey(),
									// Typecast 'gson'-Object to a Java-Object
									// for use in Model
									e -> (Object) gson.fromJson(e.getValue().toString(),
											Reflect.getField(e.getKey(), clazz).get().getType())));
			return Optional.of(map);

		} catch (JsonSyntaxException | IOException e) {
			return Optional.empty();
		}
	}

	/**
	 * Try to find the path for the configfile. Will search in the profilePath
	 * first, and defaultPath second as defined in Settings
	 * 
	 * @param filename
	 *            filename to find
	 * @return filename if found, otherwise Optional.empty()
	 */
	private Optional<Path> findValidPath(String filename) {
		logger.info("Searching for configfile %s", filename);
		// check for a custom file
		File config = new File(settings.getProfilePath() + filename);
		if ((config.exists() && !config.isDirectory()))
			return Optional.of(config.toPath());

		// check for a default one
		config = new File(settings.getDefaultPath() + filename);
		if ((config.exists() && !config.isDirectory()))
			return Optional.of(config.toPath());

		// none found
		return Optional.empty();
	}
}
