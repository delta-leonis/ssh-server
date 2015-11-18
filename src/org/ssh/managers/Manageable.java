package org.ssh.managers;

import org.ssh.util.Logger;

/**
 * The Class Manageable.
 * 
 * A Manageable represents an Object that is managed by a {@link Manager}. Examples of Manageable
 * are {@link Model} and {@link Service}.
 * 
 * @author Rimon Oz
 */
public abstract class Manageable {
    
    /** The name of the Manageable. */
    private String                name;
                                  
    // a logger for good measure
    protected static final Logger LOG = Logger.getLogger();
                                      
    /**
     * Instantiates a new manageable.
     *
     * @param name
     *            the name
     */
    public Manageable(String name) {
        this.name = name;
    }
    
    /**
     * Gets the name of the Manageable.
     *
     * @return The name of the Manageable.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Gets the name including the suffix
     * 
     * @return name and suffix
     */
    public String getFullName() {
        return String.format("%s %s", getName(), getSuffix());
    }

    /**
     * Sets the name of the Manageable.
     *
     * @param name
     *            The new name of the Manageable.
     */
    public void setName(final String name) {
        Manageable.LOG.fine("Manageable named %s has changed its name to %s", this.getName(), name);
        this.name = name;
    }
    
    /** 
     * @return unique suffix describing the manageable.
     */
    public abstract String getSuffix();
}
