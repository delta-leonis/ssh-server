package model;

/**
 * The Class Model.
 */
abstract public class Model {

    /** The name. */
    private String          name;
    private String			suffix;

    /**
     * Instantiates a new model.
     *
     * @param name the name
     */
    public Model(String name, String suffix) {
        this.name = name;
        this.suffix = suffix;
    }

    /**
     * Instantiates a new model.
     *
     * @param name the name
     */
    public Model(String name){
    	this(name, "");
    }

    /**
     * Gets the name including the suffix
     * 
     * @return name and suffix
     */
    public String getFullName(){
    	return String.format("%s %s", name, suffix);
    }
    
    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Sets suffix. <br>
     * 
     * Mostly used for defining specific models (robot A1, goal EAST etc.)
     * 
     * @param suffix the suffix
     */
    public void setSuffix(String suffix){
    	this.suffix = suffix;
    }

    /**
     * Gets suffix.<br>
     * 
     * Mostly used for defining specific models (robot A1, goal EAST etc.)
     * 
     * @return the suffix
     */
    public String getSuffix(){
    	return suffix;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Human readable string that describes this model
     */
    public String toString(){
    	return String.format(getFullName());
    }
}
