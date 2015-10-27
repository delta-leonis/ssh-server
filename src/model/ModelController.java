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
import java.util.Map.Entry;
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
 * The Class ModelController.
 */
public class ModelController {
	private ArrayList<Model> models = new ArrayList<Model>();
    private Logger		logger	= Logger.getLogger();
    private Gson		gson	= new GsonBuilder().setPrettyPrinting().create();
    private Settings	settings;

	
    /**
     * Instantiates a new model controller.
     */
    public ModelController() {}
    
    public void add(Model model){
    	logger.info("Adding %s", model.getClass());
    	if(model instanceof Settings)
    		settings = (Settings)model;
    	models.add(model);
    }

    /**
     * This method finds a model with the given name and returns it as a Model.
     * @param modelName The name of the model you want to find.
     * @return          The requested model.
     */
    public Optional<Model> get(String name) {
    	return models.stream().filter(model -> model.getFullName().trim().equals(name.trim())).findFirst();
    }

    /**
     * This method finds all models matching the name and returns them as an ArrayList<Model>
     * @param modelName The (fuzzy) name of the model you want to find.
     * @return          The requested model.
     */
    public ArrayList<Model> getAll(String name) {
    	return (ArrayList<Model>) models.stream().filter(model -> model.getFullName().equals(name))
                .collect(Collectors.toList());
    }
    
    public ArrayList<Model> getAll(){
    	return models;
    }
    
    public boolean save(Model model){
    	return saveAs(settings.getProfilePath() + model.getConfigName(), model);
    }

    public boolean saveAsDefault(Model model){
    	Settings settings = (Settings) Models.get("settings").get();
    	return saveAs(settings.getDefaultPath() + model.getConfigName(), model);
    }

    public boolean saveAs(String filePath, Model model){
    	try {
    		logger.info(filePath);
    		File configFile = new File(filePath);
    		configFile.getParentFile().mkdirs();
    		if(!configFile.exists())
    			configFile.createNewFile();
			Files.write(gson.toJson(model),configFile, Charset.defaultCharset());
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    }
    
    public boolean initializeAll(){
    	return models.stream()
    			.map(model -> load(model))
    			.reduce(true, (accumulator, succes) -> succes && accumulator);
    }
    
    public boolean reinitialize(Model model){
    	model.update(generateFieldMap(model));
    	return load(model);
    }
    
    public boolean reinitializeAll(){
    	return models.stream()
    			.map(model -> reinitialize(model))
    			.reduce(true, (accumulator, succes) -> succes && accumulator);
    }
    
    private Map<String, Object> generateFieldMap(Object obj){
    	return Stream.of(obj.getClass().getDeclaredFields())
	    		.filter(field -> !Modifier.isTransient(field.getModifiers())) 
    			.collect(
    					Collectors.toMap(Field::getName, null)
    					);
    }
    
	public boolean load(Model model){
		Optional<Path> configFile = findValidPath(model.getConfigName());

		if(!configFile.isPresent()){
			logger.info("No configfile found for %s", model.getConfigName());
			return false;
		}

		Optional<Map<String, Object>> map = readConfig(configFile.get(), model.getClass());
		logger.info("Reading configfile %s", configFile.get());

		if(!map.isPresent()){
			logger.info("Reading failed for %s", configFile);
			return false;
		}
		
		return model.update(map.get());
	}
	
	private Optional<Map<String, Object>> readConfig(Path configFile, Class<?> clazz){
		try {
			Type type = new TypeToken<Map<String, Object>>(){}.getType();
				
				//Typetoken ensures right type, so suppresWarnings is authorized here
				@SuppressWarnings("unchecked")
				Map<String, Object> map = ((Map<String, Object>) 
						//read everything and cast to a Entry<String, Object> in which String represents the fieldname
						//and Object represents the Gson object, that represents the Object in the field
						gson.fromJson(new String(java.nio.file.Files.readAllBytes(configFile)), type))
						.entrySet().stream()
							//filter everything that hasn't got a field
							.filter(e -> Reflect.getField(e.getKey(), clazz).isPresent())
							.collect(
													//key stays the same
									Collectors.toMap(e -> (String)e.getKey(),
													//Typecast 'gson'-Object to a Java-Object for use in Model
													 e -> (Object) gson.fromJson(e.getValue().toString(), Reflect.getField(e.getKey(), clazz).get().getType())	
											)
									);
			return Optional.of(map);

		} catch (JsonSyntaxException | IOException e) {
	    	return Optional.empty();
		}
	}

	private Optional<Path> findValidPath(String filename) {
		logger.info("Searching for configfile %s", filename);
		//check for a custom file
		File config = new File(settings.getProfilePath() + filename);
		if((config.exists() && !config.isDirectory()))
			return Optional.of(config.toPath());
	
		//check for a default one
		config = new File(settings.getDefaultPath() + filename);
		if((config.exists() && !config.isDirectory()))
			return Optional.of(config.toPath());

		//none found
		return Optional.empty();
	}

}
