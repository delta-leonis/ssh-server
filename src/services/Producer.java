package services;

import java.util.concurrent.Callable;

/**
 * The Class Producer.
 * 
 * @author Rimon Oz
 */
abstract public class Producer extends Service {

    /** The callable. */
    private Callable<?> callable;

    /**
     * Instantiates a new producer.
     *
     * @param name the name
     */
    public Producer(String name) {
        super(name);
    }

    /**
     * Gets the callable.
     *
     * @return the callable
     */
    public Callable<?> getCallable() {
        return this.callable;
    }

    /**
     * Sets the callable.
     *
     * @param callable the new callable
     */
    public void setCallable(Callable<?> callable) {
        this.callable = callable;
    }
}
