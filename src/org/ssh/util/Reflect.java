package org.ssh.util;

import java.util.Optional;

import com.google.common.reflect.Reflection;

/**
 * TODO remove, and use {@link Reflection} library instead.
 * 
 * @author Jeroen de Jong
 *        
 */
public class Reflect {
    
    public static boolean containsField(final String fieldName, final Class<?> clazz) {
        return Reflect.getField(fieldName, clazz).isPresent();
    }
    
    public static Optional<java.lang.reflect.Field> getField(final String fieldName, Class<?> clazz) {
        while (clazz.getSuperclass() != null) {
            try {
                return Optional.of(clazz.getDeclaredField(fieldName));
            }
            catch (final NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return Optional.empty();
    }
}
