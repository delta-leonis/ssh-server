package org.ssh.util;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.common.reflect.Reflection;

/**
 * TODO remove, and use {@link Reflection} library instead.
 * 
 * @author Jeroen de Jong
 *         
 */
public class Reflect {
    
    private static Logger LOG = Logger.getLogger();
    
    public static boolean containsField(final String fieldName, final Class<?> clazz) {
        return Reflect.getField(fieldName, clazz).isPresent();
    }
    
    public static Optional<java.lang.reflect.Field> getField(final String fieldName, Class<?> clazz) {
        Class<?> clazzI = clazz;
        while (clazzI.getSuperclass() != null) {
            try {
                return Optional.of(clazzI.getDeclaredField(fieldName));
            }
            catch (final NoSuchFieldException exception) {
                Optional<Field> oField = Stream.of(clazzI.getDeclaredFields()).filter(field -> {
                    Alias annotation = field.getAnnotation(Alias.class);
                    if (annotation != null) return annotation.value().equals(fieldName);
                    return false;
                }).findFirst();
                if (oField.isPresent()) {
                    Reflect.LOG.fine("Found %s as annotation in %s", fieldName, clazzI);
                    return oField;
                }
                
                clazzI = clazzI.getSuperclass();
            }
        }
        Reflect.LOG.info("Could not find %s in %s (or a parent).", fieldName, clazz.getName());
        return Optional.empty();
    }
}
