package org.ssh.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    
    private static final Globals globals = JsePlatform.standardGlobals();
    
    /**
     * Function that collects every class that has the {@link AvailableInLua} annotation and returns
     * it as an ArrayList<Object>
     */
    public static List<Object> getAllAvailableInLua() {
        // Create {@link Reflections} object based on our classpath
        final Reflections reflections = new Reflections(
                new ConfigurationBuilder().setUrls(ClasspathHelper.forJavaClassPath()));
        // Find every class annotated with {@link AvailableInLua}
        final Set<Class<?>> types = reflections.getTypesAnnotatedWith(AvailableInLua.class);
        final ArrayList<Object> objectArrayList = new ArrayList<Object>();
        
        // TODO: Turn into stream
        types.forEach(c -> {
            try {
                boolean singleton = false;
                for (final Method m : c.getDeclaredMethods()) {
                    // Check whether the class is a singleton
                    if (m.getName().equals("getInstance") && m.getReturnType().getSimpleName().equals(c.getSimpleName())
                            && (m.getParameterCount() == 0)) {
                        singleton = true;
                        objectArrayList.add(m.invoke(c));
                    }
                }
                // If it's not a singleton, just return the Class
                if (!singleton) objectArrayList.add(Class.forName(c.getName()));
                
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        });
        return objectArrayList;
    }
    
    /**
     * Makes sure all classes annotated with @AvailableInLua are loaded into the global variables in
     * lua.
     */
    public static void initGlobals() {
        LuaUtils.getAllAvailableInLua()
                .forEach(o -> LuaUtils.globals
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
        final LuaValue func = LuaUtils.globals.get(functionname);
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
        final LuaValue func = LuaUtils.globals.get(functionname);
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
        final LuaValue func = LuaUtils.globals.get(functionname);
        return func.call(CoerceJavaToLua.coerce(arg1), CoerceJavaToLua.coerce(arg2));
    }
    
    /**
     * Runs a lua script with the given path
     *
     * @param path
     *            The path that leads to the lua script.
     */
    public static void runScript(final String path) {
        LuaUtils.globals.loadfile(path).call();
    }
    
    /**
     * Run a function from a specific script.
     *
     * @param script
     *            The path to the script.
     * @param functionname
     *            The name of the function without brackets.
     * @param calling_obj
     *            The Object you're passing.
     */
    public static void runScriptFunction(final String script, final String functionname, final Object calling_obj) {
        // Load a script into globals
        LuaUtils.globals.get("dofile").call(LuaValue.valueOf(script));
        // Turn Java object into LuaValue
        final LuaValue luaobj = CoerceJavaToLua.coerce(calling_obj);
        // Get functions from globals
        final LuaValue func = LuaUtils.globals.get(functionname);
        // Call the function using a LuaValue
        func.call(luaobj);
    }
}
