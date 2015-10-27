package util;

import java.util.Optional;

public class Reflect {

    public static boolean containsField(String fieldName, Class<?> clazz) {
		return Reflect.getField(fieldName, clazz).isPresent();
    }
    
    public static Optional<java.lang.reflect.Field> getField(String fieldName, Class<?> clazz) {
    	while(clazz.getSuperclass() != null){
    		try { 
    			return Optional.of(clazz.getDeclaredField(fieldName));
    		}catch ( NoSuchFieldException e ) {
    			clazz = clazz.getSuperclass();
            }	
    	}
    	return Optional.empty();
    }
}
