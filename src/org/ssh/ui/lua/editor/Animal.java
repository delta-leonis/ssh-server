package org.ssh.ui.lua.editor;

import org.ssh.util.LuaUtils;

/**
 * Example class that shows how to load a script
 *
 * @author Thomas Hakkers E-mail: ThomasHakkers@hotmail.com
 *         
 */
public class Animal implements IReloadable {
    
    private String       name;
    private int          numberOfFeet;
    private final String path;
                         
    /**
     * Constructor for an animal, using a lua script. Also opens a {@link ScriptEditor}
     */
    public Animal(final String path) {
        this.path = path;
        this.reload();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getPath() {
        return this.path;
    }
    
    /**
     * {@inheritDoc} <br/>
     * In this case the "create" function is called in the lua script. <br/>
     * Also prints the name of the {@link Animal} and the number of feet it has, before and after
     * reloading, for demonstration purposes.
     */
    @Override
    public void reload() {
        System.out.println("Path: " + this.path);
        System.out.println("*BEFORE*\nName: " + this.name + " Number of Feet:" + this.numberOfFeet);
        LuaUtils.runScriptFunction(this.path, "create", this);
        System.out.println("*AFTER*\nName: " + this.name + " Number of Feet:" + this.numberOfFeet + "\n");
    }
    
    /**
     * Sets the amount of feet this animal has
     * 
     * @param feet
     *            The amount of fee this animal has
     */
    public void setFeet(final int feet) {
        this.numberOfFeet = feet;
    }
    
    /**
     * Sets the name of the animal
     * 
     * @param name
     *            The name of the animal
     */
    public void setName(final String name) {
        this.name = name;
    }
}
