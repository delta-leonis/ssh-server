package org.ssh.managers;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ssh.pipelines.Pipeline;
import org.ssh.pipelines.PipelinePacket;
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
    private static ManagerController<?> controller;
                                 
    // a logger for good measure
    private static final Logger  LOG = Logger.getLogger();
                                     
    /**
     * Finds a {@link Manageable} with the given name in the Manager.
     *
     * @param name
     *            The name of the wanted Manageable
     * @return The wanted Manageable.
     */
    public static Optional<? extends Manageable> get(final String name) {
        Manager.LOG.fine("Getting a manageable named: %s", name);
        return Manager.controller.get(name);
    }
    
    /**
     * Gets all the Manageables in the Manager store.
     *
     * @return All the Manageables
     * @see org.ssh.managers.ManagerController#getAll()
     */
    public static List<? extends Manageable> getAll() {
        return Manager.controller.getAll();
    }
    
    /**
     * Finds all Manageables matching the name and returns them as an List<Manageable>.
     *
     * @param name
     *            The name of the service you want to find.
     * @return The requested service.
     * @see ManagerController#getAll(String)
     */
    public static List<? extends Manageable> getAll(final String name) {
        return Manager.controller.getAll(name);
    }
    
    /**
     * Adds a {@link Manageable} to the Manager.
     *
     * @param manageable
     *            The service to be added.
     * @return true, if successful.
     * @see ManagerController#add(Manageable)
     */
    @SuppressWarnings ("unchecked")
    public static <N extends Manageable> boolean add(final N manageable) {
        Manager.LOG.info("Adding manageable: " + manageable.getClass().getName());
        return Manager.controller.add(manageable);
    }
    
    /**
     * Wraps {@link #add}.
     *
     * @param manageables
     *            the manageables to be added
     * @return 
     */
    @SuppressWarnings ("unchecked")
    public static <N extends Manageable> boolean add(final N... manageables) {
        return Manager.controller.add(manageables);
    }
    
    
    /**
     * Gets a list of {@link Pipeline} that operates on the supplied Type of
     * {@link org.ssh.pipelines.PipelinePacket}.
     *
     * @param
     *            <P>
     *            The generic type Consumer compatible with the Pipeline.
     * @param <C>
     *            the generic type
     * @param type
     *            The Type with which the Pipelines need to be compatible.
     * @return The list of compatible Pipelines.
     */
    public static <N extends Manageable> List<N> getOfType(final Class<?> type) {
        Manager.LOG.info("Getting compatible manageables for type: %s", type.getTypeName());
        
        // get the list of org.ssh.pipelines
        @SuppressWarnings ("unchecked")
        final List<N> collect = (List<N>) Manager.controller.getAll().stream()
                // filter out the compatible ones by genericType
                .filter(manageable -> manageable.getClass().equals(type)).collect(Collectors.toList());
                
        Manager.LOG.info("%d manageables found to be compatible with type %s",
                collect.size(),
                type.toString());
        return collect;
    }
    
}
