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
	private Console console;
	private static Functions functions;
	
	public Functions(Console console){
		this.console = console;
	}
	/**
	 * Test function for Lua. Prints "Hello From Java"
	 */
	public void hello(){
		System.out.println("Hello from Java");
	}
	
	public static Functions getInstance(){
		if(functions == null)
			functions = new Functions(Console.getInstance());
		return functions;
	}
	/**
	 * Test function for Lua. Adds two numbers.
	 */
	public int add(int a, int b){
		return a+b;
	}
	/**
	 * Prints the given string using the System.out
	 */
	public void printFromJava(String string){
		System.out.println(string);
	}
	/**
	 * Functions to test whether functions:getNewFunctions():printFromJava("Hello") works
	 */
	public Functions getNewFunctions(){
		return new Functions(console);
	}
	/**
	 * A function used to test whether functions:testObjectPassing(functions:getNewFunctions()) doesn't give an error
	 */
	public void testObjectPassing(Functions functions){
		System.out.println("Test passed");
	}
	/**
	 * Prints the given int using the System.out
	 */
	public void printInt(int i){
		System.out.println(i);
	}
	
	public void testFunction(String s, int i, Functions function, boolean b){
		
	}
	
	public void printFunctions(){
		getFunctions().forEach(f -> console.println(f));
	}
	/**
	 * Note to self: Can be applied to any function
	 */
	@SuppressWarnings("rawtypes")
	public List<String> getFunctions(){
		List<String> list = new ArrayList<String>();
		String currentString = "";
		for(Method m : this.getClass().getDeclaredMethods()){
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
