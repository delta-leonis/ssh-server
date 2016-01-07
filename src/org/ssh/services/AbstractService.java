package org.ssh.services;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

import org.ssh.managers.AbstractManageable;
import org.ssh.managers.manager.Services;
import org.ssh.pipelines.AbstractPipelinePacket;
import org.ssh.util.Logger;

import com.google.common.reflect.TypeToken;
import com.google.common.reflect.TypeToken.TypeSet;

/**
 * The Class Service.
 *
 * AbstractService<P extends AbstractPipelinePacket> is an abstract class representing a component of the framework
 * that handles Pipeline data. It remembers its parameterized genericType through reflection.
 *
 * @param <P>
 *            A PipelinePacket this Service can work with.
 *           
 * @author Rimon Oz
 */
public abstract class AbstractService<P extends AbstractPipelinePacket<? extends Object>> extends AbstractManageable {
    
    /** Whether the service is enabled. */
    private boolean               enabled     = false;
                                              
    /** The reflected TypeToken (o¬‿¬o ). */
    @SuppressWarnings ("serial")
    public TypeToken<P>           genericType = new TypeToken<P>(this.getClass()) { };
                                              
    // a logger for good measure
    protected static final Logger LOG         = Logger.getLogger();
                                              
    /**
     * Instantiates a new Service.
     *
     * @param name
     *            The name of the Service.
     */
    public AbstractService(final String name) {
        super(name);
        this.enabled = false;
        AbstractService.LOG.info("New Service instantiated named %s",  name);
        Services.add(this);
    }
    
    /**
     * Gets the service as a Service.
     *
     * @param <S>
     *            The generic type of Service requested by the user.
     * @return The Service itself.
     */
    @SuppressWarnings ("unchecked")
    public <S extends AbstractService<?>> S getAsService() {
        return (S) this;
    }
    
    /**
     * Gets the type of {@link AbstractPipelinePacket} on which this Service operates.
     *
     * @return The type of AbstractPipelinePacket on which this Service operates.
     */
    public Type getType() {
        return this.genericType.getType();
    }

    /**
     * Gets all the types of {@link AbstractPipelinePacket} on which this Service operates.
     *
     * @return The types of AbstractPipelinePacket on which this Service operates.
     */
    public List<Type> getTypes() {
        return this.genericType.getTypes().stream().map(TypeToken::getType).collect(Collectors.toList());
    }
    
    /**
     * Start the Service.
     * 
     * @param <S>
     *            The generic type of Service requested by the user.
     * @return The Service itself.
     */
    public <S extends AbstractService<?>> S start() {
        AbstractService.LOG.info("Service %s is starting ...", this.getName());
        this.setEnabled(true);
        return this.<S>getAsService();
    }
    
    /**
     * Stops the Service.
     * 
     * @param <S>
     *            The generic type of Service requested by the user.
     * @return The Service itself.
     */
    public <S extends AbstractService<?>> S stop() {
        AbstractService.LOG.info("Service %s is stopping ...", this.getName());
        this.setEnabled(false);
        return this.<S>getAsService();
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
