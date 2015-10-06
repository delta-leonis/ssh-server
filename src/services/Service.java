package services;

/**
 * The Class Service.
 */
abstract public class Service {

    /** Whether the service is enabled. */
    public boolean enabled = false;

    /** The name. */
    public String  name;

    /**
     * Instantiates a new service.
     *
     * @param name the name
     */
    public Service (String name) {
        this.name = name;
    }

    /**
     * Gets the service as a Service.
     *
     * @return the as service
     */
    public Service getAsService() {
        return this;
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
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Start.
     */
    public void start() {
        this.enabled = true;
    }

    /**
     * Stop.
     */
    public void stop() {
        this.enabled = false;
    }

}