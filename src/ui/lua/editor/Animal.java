package ui.lua.editor;

import ui.lua.utils.LuaUtils;

/**
 * Example class that shows how to load a script
 * 
 * @author Thomas Hakkers E-mail: ThomasHakkers@hotmail.com
 * 
 */
public class Animal implements IReloadable{
	private String name;
	private int numberOfFeet;
	private String path;
	
	/**
	 * Constructor for an animal, using a lua script.
	 * Also opens a {@link ScriptEditor}
	 */
	public Animal(String path){
		this.path = path;
		reload();
	}

	/**
	 * {@inheritDoc}
	 * <br/>In this case the "create" function is called in the lua script.
	 * <br/>Also prints the name of the {@link Animal} and the number of feet it has,
	 * before and after reloading, for demonstration purposes.
	 */
	public void reload(){
		System.out.println("Path: " + path);
		System.out.println("*BEFORE*\nName: " + name + " Number of Feet:" + numberOfFeet);
		LuaUtils.runScriptFunction(path, "create", this);
		System.out.println("*AFTER*\nName: " + name + " Number of Feet:" + numberOfFeet + "\n");
	}
	
	/**
	 * Sets the name of the animal
	 * @param name The name of the animal
	 */
	public void setName(String name){
		this.name = name;
	}
	
	/**
	 * Sets the amount of feet this animal has
	 * @param feet The amount of fee this animal has
	 */
	public void setFeet(int feet){
		this.numberOfFeet = feet;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPath() {
		return path;
	}
}
