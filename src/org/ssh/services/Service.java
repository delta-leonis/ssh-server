package org.ssh.services;

import java.lang.reflect.Type;

import org.ssh.managers.Services;
import org.ssh.util.Logger;

import com.google.common.reflect.TypeToken;

/**
 * The Class Service.
 *
 * Service<T extends PipelinePacket> is an abstract class representing a component of the framework
 * that handles Pipeline data. It remembers its parameterized genericType through reflection.
 *
 * @author Rimon Oz
 * @param <T>
 *            A PipelinePacket this Service can work with.
 */
public abstract class Service<T extends PipelinePacket> {
                                              
    /** Whether the service is enabled. */
    private boolean                enabled = false;
                                          
    /** The name. */
    private String                 name;
                                  
    /** The reflected TypeToken (o¬‿¬o ). */
    /* This is how we defeat Generics */
    @SuppressWarnings ("serial")
    public TypeToken<T>           genericType    = new TypeToken<T>(this.getClass()) {};
    
    // a LOG for good measure
    protected static final Logger LOG     = Logger.getLogger();
                                      
    /**
     * Instantiates a new Service.
     *
     * @param name
     *            The name of the Service.
     */
    public Service(final String name) {
        this.name = name;
        this.enabled = false;
        Service.LOG.info("New Service instantiated of genericType %s named %s", this.genericType.toString(), name);
        Services.addService(this);
    }
    
    /**
     * Gets the service as a Service.
     *
     * @return The Service itself.
     */
    public Service<T> getAsService() {
        return this;
    }
    
    /**
     * Gets the genericType of {@link org.ssh.services.PipelinePacket} on which this Service
     * operates.
     *
     * @return The genericType of PipelinePacket on which this Service operates.
     */
    public Type getType() {
        return this.genericType.getType();
    }
    
    /**
     * Gets the name of the Service.
     *
     * @return The name of the Service.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Sets the name of the Service.
     *
     * @param name
     *            The new name of the Service.
     */
    public void setName(final String name) {
        Service.LOG.info("Service named %s has changed its name to %s", this.getName(), name);
        this.name = name;
    }
    
    /**
     * Start the Service.
     */
    public void start() {
        Service.LOG.info("Service %s is starting ...", this.getName());
        this.setEnabled(true);
    }
    
    /**
     * Stops the Service.
     */
    public void stop() {
        Service.LOG.info("Service %s is stopping ...", this.getName());
        this.setEnabled(false);
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
