package org.ssh.util;

import com.google.common.reflect.ClassPath;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.ssh.ui.lua.console.AvailableInLua;
import org.ssh.ui.lua.console.Console;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class that has all sorts of utility functions for the luaj library
 *
 * @author Thomas Hakkers
 */
public class LuaUtils {
    
    // A logger for errorhandling
    private static final Logger       LOG              = Logger.getLogger();
                                                       
    private static final Globals      GLOBALS          = JsePlatform.standardGlobals();
                                                       
    private static final List<Object> AVAILABLE_IN_LUA = LuaUtils.getAllAvailableInLua();

    /**
     * Private constructor
     */
    private LuaUtils() {
    }
    
    /**
     * Function that collects every class that has the {@link AvailableInLua} annotation and returns
     * it as an ArrayList<Object>
     */
    public static List<Object> getAllAvailableInLua() {
        List<Class<?>> types = LuaUtils.getAllTypesAnnotatedWith(AvailableInLua.class, "org.ssh");
        
        final ArrayList<Object> objectArrayList = new ArrayList<>();
        
        // For each class
        types.forEach(clazz -> {
            // Find out whether it has a singleton
            Object object = getSingleton(clazz);
            if (object != null) objectArrayList.add(object);
        });
        return objectArrayList;
    }
    
    /**
     * Method used to find every class in the given package that is annotated with the given
     * {@link Annotation}
     * 
     * @param annotation
     *            The class that uses this annotation
     * @param packageName
     *            The package we need to look in
     * @return All classes in the package with the given annotation
     */
    private static <A extends Annotation> List<Class<?>> getAllTypesAnnotatedWith(Class<A> annotation,
            String packageName) {
        try {
            // Get all classes in package
            return ClassPath.from(Thread.currentThread().getContextClassLoader())
                    .getTopLevelClassesRecursive(packageName).stream()
                    // Filter based on annotation
                    .filter(classInfo -> classInfo.load().getAnnotation(annotation) != null)
                    // Load the required classes and collect them.
                    .map(ClassPath.ClassInfo::load).collect(Collectors.toList());
        }
        catch (IOException exception) {
            LuaUtils.LOG.exception(exception);
            return new ArrayList<>();
        }
    }
    
    /**
     * Look for a singleton in the given class
     * 
     * @param clazz
     *            The potential singleton class
     * @return A singleton of the object if it has one, else it just returns the Class.
     */
    @SuppressWarnings ("rawtypes")
    private static Object getSingleton(final Class clazz) {
        try {
            // Find any singletons
            Optional<Method> method = Arrays.asList(clazz.getDeclaredMethods()).stream()
                    .filter(singleton -> "getInstance".equals(singleton.getName())
                            && singleton.getReturnType().getSimpleName().equals(clazz.getSimpleName())
                            && (singleton.getParameterCount() == 0))
                    .findFirst();
            // If we find a singleton
            if (method.isPresent())
                // Use singleton for the list
                return method.get().invoke(clazz);
            else
                // Use the class instead.
                return Class.forName(clazz.getName());
        }
        catch (final Exception exception) {
            LOG.exception(exception);
            LOG.finest(
                    "Exception found in LuaUtils.getAllAvailalbeInLua. Probably a method that was invoked the wrong way.");
            return null;
        }
    }
    
    /**
     * Collects all Class names and puts them in an ArrayList<String>
     *
     * @return an ArrayList<String> containing every class in the functionClasses variable
     */
    public static List<String> getLuaClasses() {
        // Turn everything into a stream
        return LuaUtils.AVAILABLE_IN_LUA.stream().map(LuaUtils::getSimpleName).collect(Collectors.toList());
    }
    
    /**
     * Collects all Function names and puts them in an ArrayList<String>
     *
     * @return an ArrayList<String> containing every Function in the functionClasses variable
     */
    public static List<String> getLuaFunctions() {
        // Turn into stream
        return LuaUtils.AVAILABLE_IN_LUA.stream()
                // Get all declared methods as Method[]
                .map(o -> LuaUtils.getClass(o).getDeclaredMethods())
                // Turn Method[] into multiple streams
                .map(me -> Arrays.stream(me)
                        // Retrieve names from methods
                        .map(Method::getName)
                        // Collect into a list of List<List<String>>
                        .collect(Collectors.toList()))
                // Turn List<List<String>> into a stream
                .flatMap(Collection::stream)
                // Collect everything back into a List<String>
                .collect(Collectors.toList());
    }
    
    /**
     * @param o
     *            The object we need the simple name of
     * @return The simple name of the object. If an object has been turned into a {@link Class}, it
     *         won't return Class as simpleName
     */
    public static String getSimpleName(final Object o) {
        return o instanceof Class ? ((Class<?>) o).getSimpleName() : o.getClass().getSimpleName();
    }
    
    /**
     * Function used to get the {@link Class} of a certain object properly. Motivation: The classes
     * retrieved by LuaUtils are a mix of Class instances and normal instances. When you call
     * getClass() on each of these objects, you won't get what you want when when it's called on
     * something that's a Class already. For example: You'd get Chicken.getClass().getClass() which
     * would return `Class` rather than `Chicken`.
     *
     * @param o
     *            The object to (maybe) call `getClass()` on
     * @return The valid {@link Class} of o.
     */
    @SuppressWarnings ("rawtypes")
    public static Class getClass(final Object o) {
        return o instanceof Class ? (Class) o : o.getClass();
    }
    
    /**
     * Finds out whether the given string is an object found in lua globals and returns the object
     * 
     * @param string
     *            The name of the object
     * @return The {@link Object} this string belongs to
     */
    public static Object getObjectBasedOnString(String string) {
        Optional<Object> optionalObject = LuaUtils.AVAILABLE_IN_LUA.stream()
                // Find the object in the objects available in lua
                .filter(object -> getSimpleName(object).equals(string)).findFirst();
        // Return it if it exists
        if (optionalObject.isPresent())
            return optionalObject.get();
        return null;
    }
    
    /**
     * Function that returns all functions in the given object.
     */
    public static List<String> getPrettyFunctions(final Object object) {
        // Turn into stream
        return Arrays.asList(getClass(object).getDeclaredMethods()).stream()
                // Turn into pretty functions
                .map(LuaUtils::getFunctionDescription)
                // Collect into list and return it
                .collect(Collectors.toList());
    }
    
    /**
     * Generates a function description for the given Method. Example: If the method
     * getFunctionDescription we're used, it would return getFunctionDescription(Method)
     * 
     * @param method
     *            The method we want a description of
     * @return A pretty representation of the function
     * @see {@link Console}
     */
    @SuppressWarnings ("rawtypes")
    private static String getFunctionDescription(Method method) {
        // Method + ( = foo(
        String currentString = method.getName() + "(";
        final Class[] parameters = method.getParameterTypes();
        for (int i = 0; i < parameters.length; ++i) {
            // Add parameters
            currentString += parameters[i].getSimpleName();
            if ((parameters.length > 1) && (i < (parameters.length - 1)))
                // Add commas to separate parameters
                currentString += ",";
        }
        // Close function
        currentString += ")";
        return currentString;
    }
    
    /**
     * Makes sure all classes annotated with @AvailableInLua are loaded into the global variables in
     * lua.
     */
    public static void initGlobals() {
        LuaUtils.AVAILABLE_IN_LUA.forEach(o -> LuaUtils.GLOBALS
                .load(o.getClass().getSimpleName() + // Name of global variable
                        " = luajava.bindClass('" + o.getClass().getName() + "')") // Class
                                                                                  // name
                .call());
    }
    
    /**
     * Run a lua function using one argument.
     *
     * @param functionname
     *            The name of the lua function without brackets.
     * @return A LuaValue of what is returned.
     */
    public static LuaValue runFuction(final String functionname) {
        final LuaValue func = LuaUtils.GLOBALS.get(functionname);
        return func.call();
    }
    
    /**
     * Run a lua function using one argument.
     *
     * @param functionname
     *            The name of the lua function without brackets.
     * @param arg1
     *            The argument. This can be anything.
     * @return A LuaValue of what is returned.
     */
    public static LuaValue runFunction(final String functionname, final Object arg1) {
        final LuaValue func = LuaUtils.GLOBALS.get(functionname);
        return func.call(CoerceJavaToLua.coerce(arg1));
    }
    
    /**
     * Run a lua function using one argument.
     *
     * @param functionname
     *            The name of the lua function without brackets.
     * @param arg1
     *            The argument. This can be anything.
     * @param arg2
     *            The argument. This can be anything.
     * @return A LuaValue of what is returned. For example: ret = runFunction("add", 2, 3); int i =
     *         ret.checkInt(); checkInt() returns the int LuaValue represented IF it was an int. If
     *         you're unsure whether something is an int of a double, you can use the isDouble() and
     *         isInt() functions.
     */
    public static LuaValue runFunction(final String functionname, final Object arg1, final Object arg2) {
        final LuaValue func = LuaUtils.GLOBALS.get(functionname);
        return func.call(CoerceJavaToLua.coerce(arg1), CoerceJavaToLua.coerce(arg2));
    }
    
    /**
     * Runs a lua script with the given path
     *
     * @param path
     *            The path that leads to the lua script.
     */
    public static void runScriptPath(final String path) {
        LuaUtils.GLOBALS.loadfile(path).call();
    }

    /**
     * Runs a lua script
     *
     * @param command
     *            The script to run
     */
    public static void runScript(final String command) {
        LuaUtils.GLOBALS.load(command).call();
    }
    
    /**
     * Run a function from a specific script.
     *
     * @param script
     *            The path to the script.
     * @param functionname
     *            The name of the function without brackets.
     * @param callingObject
     *            The Object you're passing.
     */
    public static void runScriptFunction(final String script,
                                         final String functionname,
                                         final Object callingObject) {
        // Load a script into globals
        LuaUtils.GLOBALS.get("dofile").call(LuaValue.valueOf(script));
        // Turn Java object into LuaValue
        final LuaValue luaobj = CoerceJavaToLua.coerce(callingObject);
        // Get functions from globals
        final LuaValue func = LuaUtils.GLOBALS.get(functionname);
        // Call the function using a LuaValue
        func.call(luaobj);
    }

}
