package model;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.lambda.Unchecked;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import application.Models;
import util.Logger;
import util.Reflect;

/**
 * The Class Model.
 */
abstract public class Model {

    /** The name. */
    private transient String	name;
    private transient String	suffix;
    private transient Gson		gson	= new GsonBuilder().setPrettyPrinting().create();
    private transient Logger	logger	= Logger.getLogger();

    private transient Settings	settings;
    
    /**
     * Instantiates a new model.
     *
     * @param name the name
     */
    public Model(String name, String suffix) {
    	Models.add(this);
        this.name = name;
        this.suffix = suffix;

    	settings = (Settings) Models.get("settings").get();
    }

    /**
     * Instantiates a new model.
     *
     * @param name the name
     */
    public Model(String name){
    	this(name, "");
    }


    /**
     * Gets the name including the suffix
     * 
     * @return name and suffix
     */
    public String getFullName(){
    	return String.format("%s %s", name, suffix);
    }
    
    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }
    
    public String getConfigName(){
    	return this.getClass().getName() + ".json";
    }
    
    /**
     * Sets suffix. <br>
     * 
     * Mostly used for defining specific models (robot A1, goal EAST etc.)
     * 
     * @param suffix the suffix
     */
    public void setSuffix(String suffix){
    	this.suffix = suffix;
    }

    /**
     * Gets suffix.<br>
     * 
     * Mostly used for defining specific models (robot A1, goal EAST etc.)
     * 
     * @return the suffix
     */
    public String getSuffix(){
    	return suffix;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    public boolean update(Map<String, ?> changes){
		Map<Boolean, List<Entry<String, ?>>> filteredMethods = changes.entrySet().stream().collect(Collectors.partitioningBy(entry -> Reflect.containsField(entry.getKey(), this.getClass())));
		
		filteredMethods.get(false).forEach(entry -> logger.info("Could not find Field '%s' with type '%s'.\n", entry.getKey(), entry.getValue().getClass()));
    	
    	return filteredMethods.get(true).stream()
    			.map(entry -> set(entry.getKey(), entry.getValue()))
    			.reduce(true, (accumulator, succes) -> accumulator && succes);
    }
    
    @Override
    public String toString(){
    	StringWriter tostring = new StringWriter();
    	Class<?> clazz = this.getClass();
    	while(clazz.getSuperclass() != null){
    		//write classname
    		tostring.write(clazz.getName() + "\n");
    		//build a stream for all declared methods
    		Stream.of(clazz.getDeclaredFields())
    			.forEach(Unchecked.consumer(field -> {
    						//prevent infinite loop 
    						if(field.getType() != this.getClass()){
	    						//make the field accessible if necessary
	    						if(!field.isAccessible()) field.setAccessible(true);
	    						//write the field and its value
	    						tostring.write("   " + field.getName() + ": " + field.get(this) + "\n");
    						}
    					}));
    		//get superclass of this class
    		clazz = clazz.getSuperclass();
    	}
		return tostring.toString();
    }
    
    public <T extends Object> boolean set(String fieldName, T value){
		try {
			Optional<Field> schrödingersField = Reflect.getField( fieldName , this.getClass());
			if(schrödingersField.isPresent()){
				Field field = schrödingersField.get();
				if(!field.isAccessible()) field.setAccessible(true);
				field.set(this, (field.getType().cast(value)));
				return true;
			} else {
				logger.info("%s does not exist.\n", fieldName);
				return false;
			}
		} catch ( IllegalAccessException e) {
			logger.info("%s is not a (accessible) field.\n", fieldName);
			return false;
		}
    }
    
    /**
     * Sets all found non-transient field to values in given map
     * 
     * @param values  Key = fieldName, value is value for the field 
     */
    public void initialize(Map<String, ?> values) {
    	Class<?> clazz = this.getClass();
    	while(clazz != null){
	    	Stream.of(clazz.getDeclaredFields())
	    		//get all non-transient fields in this class
	    		.filter(field -> !Modifier.isTransient(field.getModifiers())) 
	    		.forEach(field -> {
	    			//Retrieve the supposed value for this field
	    			Object value = values.get(field.getName().toString());
	    			//does it even exist?
					if(value != null){
						//make the field accessible if necessary
						if(!field.isAccessible()) field.setAccessible(true);

						//cast the json to the right type
						value = gson.fromJson(value.toString(), field.getType());
						try{
							//try to set the field
							field.set(this, value);
						}catch (Exception e){
							//you're a loser
							logger.warning("Field type didn't match (%s != %s)", field.getType().toString(), value.getClass().getTypeName());
						}
					}
	
				});
	    	//try next superclass
    		clazz = clazz.getSuperclass();
    	}
    }
    
    /**
     * resets all non-transient field to null or the value in given map
     * 
     * @param values  Key = fieldName, value is value for the field 
     */
    public void reinitialize(Map<String, ?> values){
    	Class<?> clazz = this.getClass();
    	while(clazz != null){
	    	Stream.of(clazz.getDeclaredFields())
	    		//get all non-transient fields in this class
	    		.filter(field -> !Modifier.isTransient(field.getModifiers())) 
	    		.forEach(field -> {
	    			//Retrieve the supposed value for this field
	    			Object value = values.get(field.getName().toString());
					//make the field accessible if necessary
					if(!field.isAccessible()) field.setAccessible(true);

					//cast the json to the right type
					value = gson.fromJson(value.toString(), field.getType());
					try{
						//try to set the field
						field.set(this, value);
					}catch (Exception e){
						//you're a loser
						logger.warning("Field type didn't match (%s != %s)", field.getType().toString(), value.getClass().getTypeName());
					}
	
				});
	    	//try next superclass
    		clazz = clazz.getSuperclass();
    	}
    }
        
    
    public String toJson(){
    	return gson.toJson(this);
    }
    
    public boolean initialize(){
    	Optional<Path> possiblePath = getValidPath(getConfigName());
    	
    	if(possiblePath.isPresent()){
    		logger.info("Found a configfile (%s)", possiblePath.get());
    		
	    	Optional<Map<String, Object>> values;
	        	values = load(possiblePath.get());
	        
	    	if(values.isPresent()){
	    		initialize(values.get());
	    		return true;
	    	}
    	}
		return false;
    }

    public Optional<Map<String, Object>> load(Path configFile){
    	try {
    		logger.info("Reading configfile %s", configFile);
        	Type type = new TypeToken<Map<String, Object>>(){}.getType();
			return Optional.of(gson.fromJson(new String(java.nio.file.Files.readAllBytes(configFile)), type));

		} catch (JsonSyntaxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	    	return Optional.empty();
		}
    }

    
    public boolean save(){
    	return saveAs(settings.getProfilePath() + getConfigName());
    }

    public boolean saveAsDefault(){
    	Settings settings = (Settings) Models.get("settings").get();
    	return saveAs(settings.getDefaultPath() + getConfigName());
    }

    public boolean saveAs(String filePath){
    	try {
    		logger.info(filePath);
    		File configFile =  new File(filePath);
    		configFile.getParentFile().mkdirs();
    		if(!configFile.exists())
    			configFile.createNewFile();
			Files.write(toJson(),configFile, Charset.defaultCharset());
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    }

	private Optional<Path> getValidPath(String filename) {
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
