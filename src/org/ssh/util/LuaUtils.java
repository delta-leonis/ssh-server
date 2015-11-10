package org.ssh.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.ssh.ui.lua.console.AvailableInLua;

/**
 * Class that has all sorts of utility functions for the luaj library
 *
 * @author Thomas Hakkers E-mail: ThomasHakkers@hotmail.com
 */
public class LuaUtils {

    // A logger for errorhandling
    private static final Logger              LOG                 = Logger.getLogger();

    private static final Globals GLOBALS = JsePlatform.standardGlobals();

    /**
     * Private constructor
     */
    private LuaUtils(){
    }

    /**
     * Function that collects every class that has the {@link AvailableInLua} annotation and returns
     * it as an ArrayList<Object>
     */
    public static List<Object> getAllAvailableInLua() {
        // Create {@link Reflections} object based on our classpath
        final Reflections reflections = new Reflections(
                new ConfigurationBuilder().setUrls(ClasspathHelper.forJavaClassPath()));
        // Find every class annotated with AvailableInLua
        final Set<Class<?>> types = reflections.getTypesAnnotatedWith(AvailableInLua.class);
        final ArrayList<Object> objectArrayList = new ArrayList<Object>();
        
        types.forEach(c -> {
            Object object = getSingleton(c);
            if(object != null)
                objectArrayList.add(object);
        });
        return objectArrayList;
    }

    /**
     * Look for a singleton in the given class
     * @param clazz The potential singleton class
     * @return A singleton of the object if it has one, else it just returns the Class.
     */
    @SuppressWarnings("rawtypes")
    private static final Object getSingleton(final Class clazz){
        try {
            // Find any singletons
            Optional<Method> method = Arrays.asList(clazz.getDeclaredMethods()).stream()
                    .filter(singleton -> "getInstance".equals(singleton.getName())
                        && singleton.getReturnType().getSimpleName().equals(clazz.getSimpleName())
                        && (singleton.getParameterCount() == 0))
                    .findFirst();
            // If we find a singleton
            if(method.isPresent())
                // Use singleton for the list
                return method.get().invoke(clazz);
            else
                // Use the class instead.
                return Class.forName(clazz.getName());
        }
        catch (final Exception exception) {
            LOG.exception(exception);
            LOG.finest("Exception found in LuaUtils.getAllAvailalbeInLua. Probably a method that was invoked the wrong way.");
            return null;
        }
    }

    /**
     * Collects all Class names and puts them in an ArrayList<String>
     *
     * @return an ArrayList<String> containing every class in the functionClasses variable
     */
    public static List<String> getLuaClasses() {
        List<Object> functionClasses = getAllAvailableInLua();
        if (functionClasses == null) 
            return new ArrayList<String>();
        // Turn everything into a stream
        return functionClasses.stream().map(o ->
            // and get the simple name of each class
            LuaUtils.getSimpleName(o)).collect(Collectors.toList());
    }

    /**
     * Collects all Function names and puts them in an ArrayList<String>
     *
     * @return an ArrayList<String> containing every Function in the functionClasses variable
     */
    public static List<String> getLuaFunctions() {
        List<Object> functionClasses = getAllAvailableInLua();
        if (functionClasses == null) 
            return new ArrayList<String>();
        // Turn into stream
        return functionClasses.stream()
                // Get all declared methods as Method[]
                .map(o -> LuaUtils.getClass(o).getDeclaredMethods())
                // Turn Method[] into multiple streams
                .map(me -> Arrays.stream(me)
                        // Retrieve names from methods
                        .map(m -> m.getName())
                        // Collect into a list of List<List<String>>
                        .collect(Collectors.toList()))
                // Turn List<List<String>> into a stream
                .flatMap(l -> l.stream())
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
     * Makes sure all classes annotated with @AvailableInLua are loaded into the global variables in
     * lua.
     */
    public static void initGlobals() {
        LuaUtils.getAllAvailableInLua()
                .forEach(o -> LuaUtils.GLOBALS
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
    public static void runScript(final String path) {
        LuaUtils.GLOBALS.loadfile(path).call();
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
    public static final void runScriptFunction(final String script, final String functionname, final Object callingObject) {
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
