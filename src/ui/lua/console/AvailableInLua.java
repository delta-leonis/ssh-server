package ui.lua.console;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to specify whether the class can be used in the lua console.
 * Warning: Make sure the class you annotate has a getInstance() function.
 * 
 * @author Thomas Hakkers
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface AvailableInLua {
}
