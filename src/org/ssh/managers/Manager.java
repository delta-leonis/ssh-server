package org.ssh.managers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.ssh.util.Logger;

/**
 * The Class Manager.
 * 
 * A Manager is a DAO that handles a specific type of {@link Manageable}. Examples of Managers are
 * {@link Services}, {@link Models}, and {@link UI}.
 *
 * @param <M>
 *            the generic type of {@link Manageable} the Manager can handle.
 *            
 * @author Rimon Oz
 */
abstract public class Manager<M extends Manageable> {
    
    /** The controller. */
    private ManagerController<M> controller;
                                 
    // a logger for good measure
    private static final Logger  LOG = Logger.getLogger();
                                     
    /**
     * Finds a {@link Manageable} with the given name in the Manager.
     *
     * @param name
     *            The name of the wanted Manageable
     * @return The wanted Manageable.
     */
    public Optional<M> get(final String name) {
        Manager.LOG.fine("Getting a manageable named: %s", name);
        return this.controller.get(name);
    }
    
    /**
     * Gets all the Manageables in the Manager store.
     *
     * @return All the Manageables
     * @see org.ssh.managers.ManagerController#getAll()
     */
    public List<M> getAll() {
        return this.controller.getAll();
    }
    
    /**
     * Finds all Manageables matching the name and returns them as an List<Manageable>.
     *
     * @param name
     *            The name of the service you want to find.
     * @return The requested service.
     * @see ManagerController#getAll(String)
     */
    public List<M> getAll(final String name) {
        return this.controller.getAll(name);
    }
    
    /**
     * Adds a {@link Manageable} to the Manager.
     *
     * @param manageable
     *            The service to be added.
     * @return true, if successful.
     * @see ManagerController#add(Manageable)
     */
    public boolean add(final M manageable) {
        Manager.LOG.info("Adding manageable: " + manageable.getClass().getName());
        return this.controller.add(manageable);
    }
    
    /**
     * Wraps {@link #add}.
     *
     * @param manageables
     *            the manageables to be added
     */
    @SuppressWarnings ("unchecked")
    public void add(final M... manageables) {
        Stream.of(manageables).parallel().forEach(service -> this.controller.add(service));
    }
    
}
