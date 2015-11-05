package services;

import java.lang.reflect.Type;

import com.google.common.reflect.TypeToken;

import application.Services;
import pipeline.PipelinePacket;
import util.Logger;

/**
 * The Class Service.
 * 
 * Service<T extends PipelinePacket> is an abstract class representing a component of
 * the framework that handles Pipeline data. It remembers its parametrized type through
 * reflection.
 *
 * @author Rimon Oz
 * @param <T> A PipelinePacket this Service can work with.
 */
abstract public class Service<T extends PipelinePacket> {

    /** Whether the service is enabled. */
    public boolean enabled = false;

    /** The name. */
    public String name;

    /**  The reflected TypeToken (o¬‿¬o ). */
    /*   This is how we defeat Generics    */
    @SuppressWarnings("serial")
    public TypeToken<T> type = new TypeToken<T>(this.getClass()) {};

    // a logger for good measure
    protected static final Logger logger = Logger.getLogger();

    /**
     * Instantiates a new Service.
     *
     * @param name The name of the Service.
     */
    public Service(String name) {
        this.name = name;
        Service.logger.info("New Service instantiated of type %s named %s", this.type.toString(), name);
        Services.addService(this);
    }

    /**
     * Gets the service as a Service.
     *
     * @return The Service itself.
     */
    public Service<?> getAsService() {
        return this;
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
     * Gets the type of {@link pipeline.PipelinePacket} on which this Service operates.
     *
     * @return The type of PipelinePacket on which this Service operates.
     */
    public Type getDataType() {
        return this.type.getType();
    }

    /**
     * Sets the name of the Service.
     *
     * @param name The new name of the Service.
     */
    public void setName(String name) {
        Service.logger.info("Service named %s has changed its name to %s", this.getName(), name);
        this.name = name;
    }

    /**
     * Start the Service.
     */
    public void start() {
        Service.logger.info("Service %s is starting ...", this.getName());
        this.enabled = true;
    }

    /**
     * Stops the Service.
     */
    public void stop() {
        Service.logger.info("Service %s is stopping ...", this.getName());
        this.enabled = false;
    }
}
