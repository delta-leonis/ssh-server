package model;

/**
 * The Class Model.
 */
abstract public class Model {

    /** The name. */
    private String            name;

    /** The data. */
    private Object            data;

    /**
     * Instantiates a new model.
     *
     * @param name the name
     */
    public Model(String name) {
        this.name = name;
    }

    /**
     * Adds the data.
     *
     * @param data the data
     */
    abstract public void   addData(Object data);

    /**
     * Gets the data.
     *
     * @return the data
     */
    public Object getData() {
    	return this.data;
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
     * Sets the data.
     *
     * @param data the new data
     */
    abstract public void   setData(Object data);

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }
}
