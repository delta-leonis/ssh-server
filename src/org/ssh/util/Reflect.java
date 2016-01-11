package org.ssh.util;

import org.ssh.models.AbstractModel;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.ssh.models.AbstractModel.*;

/**
 * Class used for tools concerning {@link java.lang.reflect}
 *
 * @author Jeroen de Jong
 */
public class Reflect {

    //respective logger
    private static final Logger LOG = Logger.getLogger();

    /**
     * This class should merely act as a toolkit, so it shouldn't be be instantiated.
     */
    private Reflect(){
    }

    /**
     * Checks whether a Class or it's parents contain a field with given name or {@link Alias}.
     *
     * @param fieldName name or {@link Alias} of a field to check for
     * @param clazz class object to check for the field
     * @return true if class or parent contains a field by this name
     */
    public static boolean hasField(final String fieldName, final Class<?> clazz) {
        return Reflect.getField(fieldName, clazz).isPresent();
    }

    /**
     * Gets a {@link Field} of a Class or it's parents with given name or {@link Alias}.
     *
     * @param fieldName name or {@link Alias} of a field to check for
     * @param clazz class object to check for the field
     * @return {@link Field} if found, or else an empty optional
     */
    public static Optional<java.lang.reflect.Field> getField(final String fieldName, Class<?> clazz) {
        // local class Iterator
        Class<?> clazzI = clazz;
        // loop until all superclasses has been tried
        while (clazzI.getSuperclass() != null) {
            try {
                //return the optional of a field if found
                return Optional.of(clazzI.getDeclaredField(fieldName));
            }
            // gets thrown by #getDeclaredField(String) when a field is not found
            catch (final NoSuchFieldException exception) {
                Reflect.LOG.exception(exception);
                //try to find the field based on an Model.Alias
                Optional<Field> oField = Stream.of(clazzI.getDeclaredFields())
                        //stream all fields, and filter the fields based on their equality in name
                        .filter(field -> {
                            // retrieve the annotation
                            Alias annotation = field.getAnnotation(Alias.class);
                            // return the equality state
                            return annotation != null && annotation.value().equals(fieldName);
                        }).findAny();
                // It has been found under it's Model.Alias
                if (oField.isPresent()) {
                    Reflect.LOG.fine("Found %s as annotation in %s", fieldName, clazzI);
                    return oField;
                }

                // search in the superclass
                clazzI = clazzI.getSuperclass();
            }
        }

        // could not find the field
        Reflect.LOG.info("Could not find %s in %s (or a parent).", fieldName, clazz.getName());
        return Optional.empty();
    }

    /**
     * Create a list of all fieldnames that aren't Transient
     *
     * @param clazz  Class to create list for
     * @return list of all fieldnames that aren't Transient
     */
    public static List<String> fieldList(final Class<?> clazz){
        List<String> fieldNames = new ArrayList<String>();

        // local class Iterator
        Class<?> clazzI = clazz;
        // loop until all superclasses has been tried
        while (clazzI.getSuperclass() != null) {
            //get all fieldnames for this class
            fieldNames.addAll(Stream.of(clazzI.getDeclaredFields())
                    // it should not be transient, final or static
                    .filter(field ->  !Modifier.isTransient(field.getModifiers())
                            && !Modifier.isFinal(field.getModifiers())
                            && !Modifier.isStatic(field.getModifiers()))
                    //field -> fieldname
                    .map(Field::getName)
                    .collect(Collectors.toList()));
            clazzI = clazzI.getSuperclass();
        }
        return fieldNames;
    }

}
