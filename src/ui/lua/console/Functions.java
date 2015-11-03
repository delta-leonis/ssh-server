package ui.lua.console;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Functions to test out in the {@link Console}
 * Example:  functions:hello()
 * 
 * ERRORS:
 * -
 * FIXED:
 * 	You can't use the result of a java function inside another java function.
 * 	For example: functions:printFromJava(functions:add(2,2)) won't work.
 *  This doesn't work because of the way luaj works. If you would return an int in Java, then lua will transform that into a lua-int, which in turn can't be used in java functions.
 *  This test would've worked if printFromJava accepted {@link LuaValue} instead of ints.
 * 
 * You can however use the Lua functions for this
 * 	For example: functions:printFromJava(add(2,2)) and print(functions:add(2,2)) will work.
 * 
 * @author Thomas Hakkers E-mail: ThomasHakkers@hotmail.com
 *
 */
@AvailableInLua
public class Functions{
	/**
	 * Function that returns all functions in the given object
	 * TODO: Streams
	 */
	@SuppressWarnings("rawtypes")
	public static final List<String> getFunctions(Object object){
		List<String> list = new ArrayList<String>();
		String currentString = "";
		for(Method m : object.getClass().getDeclaredMethods()){
			currentString = m.getName() + "(";
			Class[] classes = m.getParameterTypes();
			for(int i = 0; i < classes.length; ++i){
				currentString += classes[i].getSimpleName();
				if(classes.length > 1 && i < classes.length - 1)
					currentString += ",";
			}
			currentString += ")";
			list.add(currentString);
		}
		return list;
	}
}
