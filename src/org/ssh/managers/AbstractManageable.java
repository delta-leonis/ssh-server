package org.ssh.managers;

import org.ssh.models.AbstractModel;
import org.ssh.services.AbstractService;
import org.ssh.util.Logger;

/**
 * The Class AbstractManageable.
 * 
 * A Manageable represents an Object that is managed by a {@link ManagerInterface}. Examples of Manageable
 * are {@link AbstractModel} and {@link AbstractService}.
 * 
 * @author Rimon Oz
 */
public abstract class AbstractManageable {
    
    /** The name of the Manageable. */
    private transient String                name;
                                  
    // a logger for good measure
    protected static final transient Logger LOG = Logger.getLogger();
                                      
    /**
     * Instantiates a new manageable.
     *
     * @param name
     *            The name of the manageable.
     */
    public AbstractManageable(String name) {
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
     * Sets the name of the Manageable.
     *
     * @param <M>
     *            The return type of the Manageable.
     * @param name
     *            The new name of the Manageable.
     * @return The current Manageable.
     */
    @SuppressWarnings ("unchecked")
    public <M extends AbstractManageable> M setName(final String name) {
        AbstractManageable.LOG.fine("Manageable named %s has changed its name to %s", this.getName(), name);
        this.name = name;
        return (M) this;
    }

}
