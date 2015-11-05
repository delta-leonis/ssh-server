package org.ssh.models;

import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.lambda.Unchecked;
import org.ssh.managers.Models;
import org.ssh.util.Logger;
import org.ssh.util.Reflect;

/**
 * The Class Model.<br />
 * <br />
 * 
 * Note: A lot of refactoring is being done for this class, to keep this class
 * dynamic enough. Remember to use the transient {@link Modifier} for
 * {@link Field Fields} that don't need to be saved in a json file by the
 * {@link #save()} method.
 * 
 * @see {@link #update(Map)}
 * @see {@link ModelFactory}
 * @see {@link ModelController}
 * @author Jeroen
 */
abstract public class Model {

	/** The name. */
	private transient String name;
	/** unique suffix for this org.ssh.models **/
	private transient String suffix;
	// respective logger
	private transient Logger logger = Logger.getLogger();

	/**
	 * Instantiates a new org.ssh.models.
	 *
	 * @param name
	 *            the name
	 */
	public Model(String name, String suffix) {
		this.name = name;
		this.suffix = suffix;
	}

	/**
	 * Instantiates a new org.ssh.models.
	 *
	 * @param name
	 *            the name
	 */
	public Model(String name) {
		this(name, "");
	}

	/**
	 * Gets the name including the suffix
	 * 
	 * @return name and suffix
	 */
	public String getFullName() {
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

	/**
	 * @return name used for config
	 */
	public String getConfigName() {
		return this.getClass().getName() + ".json";
	}

	/**
	 * Sets suffix. <br>
	 * 
	 * Mostly used for defining specific models (robot A1, goal EAST etc.)
	 * 
	 * @param suffix
	 *            the suffix
	 */
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	/**
	 * Gets suffix.<br>
	 * 
	 * Mostly used for defining specific models (robot A1, goal EAST etc.)
	 * 
	 * @return the suffix
	 */
	public String getSuffix() {
		return suffix;
	}

	/**
	 * Sets the name.
	 *
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * update this Model with a primitive array
	 * 
	 * @param changes
	 *            should exist of a even number of arguments, with each odd
	 *            argument being a String representing a field and every even
	 *            argument representing it's new contents
	 * @see {@link Model#update(Map)}
	 * @return
	 */
	public boolean update(Object... changes) {
		// this method will only work if there are a even number of changes
		if (changes.length % 2 != 0) {
			logger.warning("Uneven number of changes.");
			return false;
		}

		// new map for update(map) method
		Map<String, Object> changeMap = new HashMap<String, Object>();

		// map every odd Object as String, and every even Object as Object
		for (int i = 0; i < changes.length - 1; i++)
			changeMap.put((String) changes[i], changes[++i]);

		return update(changeMap);
	}

	/**
	 * Update a number of fields as described in given Map. Key describes a
	 * {@link Field field name} in a {@link Model} and the value contains the
	 * respective {@link Object} that corresponds to that field.
	 * 
	 * @param changes
	 *            changes to make to this org.ssh.models
	 * @return success value
	 */
	public boolean update(Map<String, ?> changes) {
		// loop all changes
		return changes.entrySet().stream()
				// check wheter this field exists
				.filter(entry -> Reflect.containsField(entry.getKey(), this.getClass()))
				// set this value
				.map(entry -> set(entry.getKey(), entry.getValue()))
				// reduce succes value
				.reduce(true, (accumulator, succes) -> accumulator && succes);
	}

	/**
	 * Generates a human-readable string of all {@link Field Fields} in this
	 * class by refactoring
	 */
	@Override
	public String toString() {
		// create a stringwriter
		StringWriter tostring = new StringWriter();

		// loop to the highest superclass before java.Object
		Class<?> clazz = this.getClass();
		while (clazz.getSuperclass() != null) {
			// write classname
			tostring.write(clazz.getName() + "\n");
			// build a stream for all declared methods
			Stream.of(clazz.getDeclaredFields()).forEach(Unchecked.consumer(field -> {
				// prevent infinite loop
				if (field.getType() != this.getClass()) {
					// make the field accessible if necessary
					if (!field.isAccessible())
						field.setAccessible(true);
					// write the field and its value
					tostring.write("   " + field.getName() + ": " + field.get(this) + "\n");
				}
			}));
			// get superclass of this class
			clazz = clazz.getSuperclass();
		}
		// return build string
		return tostring.toString();
	}

	/**
	 * Set a specific {@link Field} to a specific value
	 * 
	 * @param fieldName
	 *            field to set
	 * @param value
	 *            value to set
	 * @return success value
	 */
	public <T extends Object> boolean set(String fieldName, T value) {
		try {
			// try to get the field
			Optional<Field> oField = Reflect.getField(fieldName, this.getClass());
			// found the field?
			if (oField.isPresent()) {
				// get it and set it accessible
				Field field = oField.get();
				if (!field.isAccessible())
					field.setAccessible(true);
				// try to cast this value, and set the field
				field.set(this, (field.getType().cast(value)));
				return true;
			} else {
				// field doesn't exist
				logger.info("%s does not exist.\n", fieldName);
				return false;
			}
		} catch (IllegalAccessException | ClassCastException e) {
			// field is either not accessible, or the fieldType didn't match
			logger.info("%s is not a (accessible) field.\n", fieldName);
			return false;
		}
	}

	/**
	 * Save this org.ssh.models in current Profile
	 * 
	 * @return success value
	 */
	public boolean save() {
		return Models.save(this);
	}

	/**
	 * Save this org.ssh.models as org.ssh.managers-wide default
	 * 
	 * @return success value
	 */
	public boolean saveAsDefault() {
		return Models.saveAsDefault(this);
	}

}
