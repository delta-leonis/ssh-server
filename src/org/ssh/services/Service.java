package org.ssh.services;

import java.lang.reflect.Type;

import org.ssh.managers.Manageable;
import org.ssh.managers.Services;
import org.ssh.pipelines.PipelinePacket;
import org.ssh.util.Logger;

import com.google.common.reflect.TypeToken;

/**
 * The Class Service.
 *
 * Service<P extends PipelinePacket> is an abstract class representing a component of the framework
 * that handles Pipeline data. It remembers its parameterized genericType through reflection.
 *
 * @param
 *            <P>
 *            A PipelinePacket this Service can work with.
 *           
 * @author Rimon Oz
 */
public abstract class Service<P extends PipelinePacket> extends Manageable {
    
    /** Whether the service is enabled. */
    private boolean               enabled     = false;
                                              
    /** The reflected TypeToken (o¬‿¬o ). */
    @SuppressWarnings ("serial")
    public TypeToken<P>           genericType = new TypeToken<P>(this.getClass()) {
                                              };
                                              
    // a logger for good measure
    protected static final Logger LOG         = Logger.getLogger();
                                              
    /**
     * Instantiates a new Service.
     *
     * @param name
     *            The name of the Service.
     */
    public Service(final String name) {
        super(name);
        this.enabled = false;
        Service.LOG.info("New Service instantiated of type %s named %s", this.genericType.toString(), name);
        Services.addService(this);
    }
    
    /**
     * Gets the service as a Service.
     *
     * @return The Service itself.
     */
    public Service<P> getAsService() {
        return this;
    }
    
    /**
     * Gets the type of {@link PipelinePacket} on which this Service operates.
     *
     * @return The type of PipelinePacket on which this Service operates.
     */
    public Type getType() {
        return this.genericType.getType();
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
     * Checks if the Service is enabled.
     *
     * @return Whether or not the service is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Enables/disables the service.
     *
     * @param enabled
     *            true to enable, false to disable
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
