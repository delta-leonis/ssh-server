package org.ssh.models;

import javafx.beans.value.WritableValue;
import javafx.util.Pair;
import org.jooq.lambda.Unchecked;
import org.ssh.managers.AbstractManageable;
import org.ssh.managers.controllers.ModelController;
import org.ssh.managers.manager.Models;
import org.ssh.models.enums.ManagerEvent;
import org.ssh.util.Reflect;

import java.io.StringWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * The Class AbstractModel.<br />
 * <p>
 * <strong>Note:</strong> A lot of refactoring is being done for this class, to keep this class dynamic. Remember to
 * use the transient {@link Modifier} for {@link Field Fields} that don't need to be saved in a json
 * file by the {@link #save()} method.
 *
 * @author Jeroen de Jong
 * @see {@link #update(Map)}
 * @see {@link Models#create}
 * @see {@link ModelController}
 */

public abstract class AbstractModel extends AbstractManageable {

    /**
     * unique identifier for this model
     */
    private transient String identifier;

    /**
     * Instantiates a new models.
     *
     * @param name the name
     */
    public AbstractModel(final String name, final String identifier) {
        super(name);
        this.identifier = identifier;
    }

    /**
     * Should contains all initial values as declared normally in the constructor
     */
    public abstract void initialize();

    /**
     * @return name used for config
     */
    public String getConfigName() {
        return this.getIdentifier().replace(" ", "_") + ".json";
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
     * Save this models as system-wide default
     *
     * @return success value
     */
    public boolean saveAsDefault() {
        return Models.saveAsDefault(this);
    }

    /**
     * Set a specific {@link Field} to a specific value
     *
     * @param fieldName string name of a field to set
     * @param value     value to set
     * @return success value
     */
    public <T> boolean set(final String fieldName, final T value) {
        try {
            // try to get the field
            final Optional<Field> oField = Reflect.getField(fieldName, this.getClass());
            // found the field?
            if (oField.isPresent()) {
                // get it and set it accessible
                final Field field = oField.get();
                if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                    AbstractModel.LOG.fine("%s in is not a modifiable field", field.getName(), this.getClass().getSimpleName());
                    return false;
                }
                if (!field.isAccessible())
                    field.setAccessible(true);
                // if the field is a collection and the value is not, the value is added to the collection
                if (field.get(this) instanceof Collection && !(value instanceof Collection))
                    ((Collection) field.get(this)).add(value);
                    // check if the field is a writable value surrounding it
                else if (field.get(this) instanceof WritableValue)
                    // if so, use the setValue method of field so its bounded properties get updated
                    if (value instanceof WritableValue)
                        ((WritableValue) field.get(this)).setValue(((WritableValue) value).getValue());
                    else
                        ((WritableValue) field.get(this)).setValue(value);
                    // try to cast this value, and set the field
                    // Slightly hacky way to deal with primitive numbers.
                else if (value instanceof Number) {
                    if (field.getType().equals(Float.class))
                        field.set(this, ((Number) value).floatValue());
                    else if (field.getType().equals(Double.class))
                        field.set(this, ((Number) value).doubleValue());
                    else if (field.getType().equals(Integer.class))
                        field.set(this, ((Number) value).intValue());
                    else if (field.getType().equals(Long.class))
                        field.set(this, ((Number) value).longValue());
                    else if (field.getType().equals(Short.class))
                        field.set(this, ((Number) value).shortValue());
                } else
                    field.set(this, field.getType().cast(value));
                return true;
            } else
                // field doesn't exist
                AbstractModel.LOG.info("%s does not exist.\n", fieldName);
        } catch (IllegalAccessException exception) {
            // field is either not accessible, or the fieldType didn't match
            AbstractModel.LOG.info("%s is not a (accessible) field.\n", fieldName);
            AbstractModel.LOG.exception(exception);
        } catch (ClassCastException exception) {
            AbstractModel.LOG.info("%s is not assignable from %s.\n", fieldName, value.getClass().getTypeName());
            AbstractModel.LOG.exception(exception);
        } catch (UnsupportedOperationException exception) {
            AbstractModel.LOG.info("Collection %s cannot be modified.", fieldName);
            AbstractModel.LOG.exception(exception);
        }
        return false;
    }


    /**
     * Generates a human-readable string of all {@link Field Fields} in this class by refactoring.
     */
    @Override
    public String toString() {
        // create a string writer
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
     * in a {@link AbstractModel} and the value contains the respective {@link Object} that corresponds to
     * that field.
     *
     * @param changes changes to make to this models
     * @return success value
     */
    public boolean update(final Map<String, ?> changes) {
        AbstractModel.LOG.fine("Updating model %s", getClass());

        // loop all changes
        boolean returnvalue = changes.entrySet().stream()
                // check whether this field exists
                .filter(entry -> Reflect.hasField(entry.getKey(), this.getClass()))
                // set this value
                .map(entry -> this.set(entry.getKey(), entry.getValue()))
                // reduce success value
                .reduce(true, (accumulator, success) -> accumulator && success);
        Models.triggerEvent(ManagerEvent.UPDATE, this);
        return returnvalue;
    }

    /**
     * Serialize this Model to a Map, with each fieldname as a key, and each field value as value.
     *
     * @return map containing fieldnames and their values
     */
    public Map<String, Object> toMap() {
        // get Class<?> object
        Class<?> clazz = this.getClass();
        // create map
        Map<String, Object> fieldMap = new HashMap<>();

        // loop all inherited classes, starting with the highest one
        while (clazz.getSuperclass() != null) {
            // get all fields
            Stream.of(clazz.getDeclaredFields())
                    // if it isn't transient
                    .filter(field -> !Modifier.isTransient(field.getModifiers())
                            && !Modifier.isFinal(field.getModifiers())
                            && !fieldMap.containsKey(field.getName()))
                    // loop all left-over fields
                    .forEach(Unchecked.consumer(field -> {
                        // get the right access rights
                        if (!field.isAccessible()) field.setAccessible(true);
                        // put k-v in the map
                        fieldMap.put(field.getName(), field.get(this));
                    }));
            // get next superclass
            clazz = clazz.getSuperclass();
        }
        // return the map
        return fieldMap;
    }

    /**
     * update this Model with a primitive array.
     *
     * @param changes should consist of a even number of arguments, with each odd argument being a
     *                String representing a field and every even argument representing it's new
     *                contents.
     * @return success value
     * @example          <pre>
     *          Model.update("x", 123, "robotId", 12);
     *          </pre>
     * @see {@link AbstractModel#update(Map)}
     */
    public boolean update(final Object... changes) {
        // this method will only work if there are an even number of changes
        if ((changes.length % 2) != 0) {
            AbstractModel.LOG.warning("Uneven number of changes.");
            return false;
        }

        return this.update(IntStream.range(0, changes.length)
                .filter(i -> i %2 == 0) // only even numbers
                .mapToObj(index -> new Pair<>((String) changes[index], changes[index+1]))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue)));
    }

    /**
     * @return unique suffix describing the manageable.
     */
    public String getIdentifier() {
        return String.format("%s %s", getName(), identifier).trim();
    }

    /**
     * Reset a number of fields
     *
     * @param fields field to reset to null
     */
    public void reset(List<String> fields) {
        fields.forEach(field -> {
            try {
                if (Reflect.getField(field, this.getClass()).get().get(this) instanceof WritableValue) {
                    AbstractModel.LOG.info("Did not reset");
                    return;
                }
            } catch(Exception ignored){}
            set(field, null);
        });
    }

    @Target(value = ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Alias {
        String value();
    }

}
