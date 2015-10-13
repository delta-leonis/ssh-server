package ui.lua.utils;


import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;


/**
 * Class that can load lua scripts
 */
public class LuaUtils {
	private static Globals globals = JsePlatform.standardGlobals();
	
	/**
	 * Run a lua function using one argument.
	 * @param functionname The name of the lua function without brackets.
	 * @param arg1 The argument. This can be anything.
	 * @param arg2 The argument. This can be anything.
	 * @return A LuaValue of what is returned.
	 * For example: 
	 * ret = runFunction("add", 2, 3);
	 * int i = ret.checkInt();
	 * checkInt() returns the int LuaValue represented IF it was an int.
	 * If you're unsure whether something is an int of a double, you can use the isDouble() and isInt() functions.
	 */
	public static LuaValue runFunction( String functionname, Object arg1, Object arg2){
		LuaValue func = globals.get(functionname);
        return func.call( CoerceJavaToLua.coerce(arg1), CoerceJavaToLua.coerce(arg2) );
	}
	
	/**
	 * Run a lua function using one argument.
	 * @param functionname The name of the lua function without brackets.
	 * @param arg1 The argument. This can be anything.
	 * @return A LuaValue of what is returned.
	 */
	public static LuaValue runFunction( String functionname, Object arg1 ){
		LuaValue func = globals.get(functionname);
        return func.call( CoerceJavaToLua.coerce(arg1) );
	}
	
	/**
	 * Run a lua function using one argument.
	 * @param functionname The name of the lua function without brackets.
	 * @return A LuaValue of what is returned.
	 */
	public static LuaValue runFuction( String functionname ){
		LuaValue func = globals.get(functionname);
        return func.call();
	}
	
	/**
	 * Run a function from a specific script.
	 * @param script The path to the script.
	 * @param functionname The name of the function without brackets.
	 * @param calling_obj The Object you're passing.
	 */
	public static void runScriptFunction ( String script, String functionname, Object calling_obj )
    {
		// Load a script into globals
        globals.get("dofile").call(LuaValue.valueOf(script));
        // Turn Java object into LuaValue
        LuaValue luaobj = CoerceJavaToLua.coerce(calling_obj);
        // Get functions from globals
        LuaValue func = globals.get(functionname);
        // Call the function using a LuaValue
        func.call( luaobj );
    }
}
