package org.ssh.models;

import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.jooq.lambda.Unchecked;
import org.ssh.managers.Models;
import org.ssh.util.Logger;
import org.ssh.util.Reflect;

import com.google.common.reflect.Reflection;

/**
 * The Class Model.<br />
 *
 * Note: A lot of refactoring is being done for this class, to keep this class dynamic. Remember to
 * use the transient {@link Modifier} for {@link Field Fields} that don't need to be saved in a json
 * file by the {@link #save()} method.
 *
 * @see {@link #update(Map)}
 * @see {@link ModelFactory}
 * @see {@link ModelController}
 * @see {@link Reflection}
 * @author Jeroen de Jong
 */

// Serializable is only implemented to use transient modifiers,
// but wont be serialized.
@SuppressWarnings ("serial")
public abstract class Model implements Serializable {
    
    /** The name. */
    private transient String              name;
    /** unique suffix for this models **/
    private transient String              suffix;
    // respective logger
    private transient static final Logger LOG = Logger.getLogger();
                                              
    /**
     * Instantiates a new models.
     *
     * @param name
     *            the name
     */
    public Model(final String name) {
        this(name, "");
    }
    
    /**
     * Instantiates a new models.
     *
     * @param name
     *            the name
     */
    public Model(final String name, final String suffix) {
        this.name = name;
        this.suffix = suffix;
    }
    
    /**
     * @return name used for config
     */
    public String getConfigName() {
        return this.getClass().getName() + ".json";
    }
    
    /**
     * Gets the name including the suffix
     * 
     * @return name and suffix
     */
    public String getFullName() {
        return String.format("%s %s", this.name, this.suffix);
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
     * Gets suffix.<br>
     * 
     * Mostly used for defining specific models (robot A1, goal EAST etc.)
     * 
     * @return the suffix
     */
    public String getSuffix() {
        return this.suffix;
    }
    
    /**
     * Save this models in current Profile
     * 
     * @return success value
     */
    public boolean save() {
        return Models.save(this);
    }
    
    /**
     * Save this models as sytem-wide default
     * 
     * @return success value
     */
    public boolean saveAsDefault() {
        return Models.saveAsDefault(this);
    }
    
    /**
     * Set a specific {@link Field} to a specific value
     * 
     * @param fieldName
     *            string name of a field to set
     * @param value
     *            value to set
     * @return success value
     */
    public <T extends Object> boolean set(final String fieldName, final T value) {
        try {
            // try to get the field
            final Optional<Field> oField = Reflect.getField(fieldName, this.getClass());
            // found the field?
            if (oField.isPresent()) {
                // get it and set it accessible
                final Field field = oField.get();
                if (!field.isAccessible()) field.setAccessible(true);
                // try to cast this value, and set the field
                field.set(this, (field.getType().cast(value)));
                return true;
            }
            else {
                // field doesn't exist
                Model.LOG.info("%s does not exist.\n", fieldName);
                return false;
            }
        }
        catch (IllegalAccessException | ClassCastException exception) {
            // field is either not accessible, or the fieldType didn't match
            Model.LOG.info("%s is not a (accessible) field.\n", fieldName);
            Model.LOG.exception(exception);
            return false;
        }
    }
    
    /**
     * Sets the name.
     *
     * @param name
     *            the new name
     */
    public void setName(final String name) {
        this.name = name;
    }
    
    /**
     * Sets suffix. <br>
     * 
     * Mostly used for defining specific models (robot A1, goal EAST etc.).
     * 
     * @param suffix
     *            the suffix
     */
    public void setSuffix(final String suffix) {
        this.suffix = suffix;
    }
    
    /**
     * Generates a human-readable string of all {@link Field Fields} in this class by refactoring.
     */
    @Override
    public String toString() {
        // create a stringwriter
        final StringWriter tostring = new StringWriter();
        
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
                    if (!field.isAccessible()) field.setAccessible(true);
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
     * Update a number of fields as described in given Map. Key describes a {@link Field field name}
     * in a {@link Model} and the value contains the respective {@link Object} that corresponds to
     * that field.
     * 
     * @param changes
     *            changes to make to this models
     * @return success value
     */
    public boolean update(final Map<String, ?> changes) {
        // loop all changes
        return changes.entrySet().stream()
                // check whether this field exists
                .filter(entry -> Reflect.containsField(entry.getKey(), this.getClass()))
                // set this value
                .map(entry -> this.set(entry.getKey(), entry.getValue()))
                // reduce succes value
                .reduce(true, (accumulator, succes) -> accumulator && succes);
    }
    
    /**
     * update this Model with a primitive array.
     * 
     * @param changes
     *            should consist of a even number of arguments, with each odd argument being a
     *            String representing a field and every even argument representing it's new
     *            contents.
     * @example
     *          
     *          <pre>
     *          Model.update("position", new Point2D(123, 123), "robotId", 12);
     *          </pre>
     * 
     * @see {@link Model#update(Map)}
     * @return success value
     */
    public boolean update(final Object... changes) {
        // this method will only work if there are an even number of changes
        if ((changes.length % 2) != 0) {
            Model.LOG.warning("Uneven number of changes.");
            return false;
        }
        
        // new map for update(map) method
        final Map<String, Object> changeMap = new HashMap<String, Object>();
        
        // map every odd Object as String, and every even Object as Object
        for (int i = 0; i < (changes.length - 1); i++)
            changeMap.put((String) changes[i], changes[++i]);
            
        return this.update(changeMap);
    }
    
}