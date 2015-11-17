package org.ssh.managers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

/**
 * The Class ManagerController.
 * 
 * A ManagerController is used by a Manager to operate on Manageables. Since a Manager is static and
 * final another class is needed to operate on dynamic data.
 *
 * @param <M>
 *            the generic type of {@link Manageable} this ManagerController operates on.
 *            
 * @author Rimon Oz
 */
public abstract class ManagerController<M extends Manageable> {
    
    /** The manageables. */
    protected ImmutableList<M> manageables;
    
    public ManagerController() {
        this.manageables = ImmutableList.of();
    }
    
    /**
     * Gets the Manageable with the specified name as an Optional<Manageable>.
     *
     * @param name
     *            the name of the manageable
     * @return An Optional representing the Manageable.
     */
    public Optional<M> get(String name) {
        return this.manageables.stream().filter(manageable -> manageable.getName().equals(name)).findFirst();
    }
    
    /**
     * Gets all the Manageables as a List.
     *
     * @return The List of Manageables
     */
    public List<M> getAll() {
        return this.manageables.stream().collect(Collectors.toList());
    }
    
    /**
     * Gets the all the Manageables with the specified name.
     *
     * @param name
     *            The name of the Manageables
     * @return All the Manageables with the specified name
     */
    public List<M> getAll(String name) {
        return this.manageables.stream().filter(manageable -> manageable.getName().equals(name))
                .collect(Collectors.toList());
    }
    
    /**
     * Adds a {@link Manageable} to the Manager.
     *
     * @param manageable
     *            the Manageable to be a dded
     * @return true, if successful
     */
    @SuppressWarnings ("unchecked")
    public <N extends Manageable> boolean add(final N manageable) {
        this.manageables = ImmutableList.<M> builder().addAll(this.manageables).add((M) manageable).build();
        return true;
    }
        /**
     * Gets a list of manageables of the given type.
     *
     * @param <N>
     *            The generic type of Manageable
     * @param type
     *            The type of the requested manageables
     * @return The list of manageables
     */
    public <N extends Manageable> List<N> getOfType(final Class<?> type) {
//        Manager.LOG.info("Getting compatible manageables for type: %s", type.getTypeName());
        
        // get the list of manageables
        @SuppressWarnings ("unchecked")
        final List<N> collect = (List<N>) this.manageables.stream()
                // filter out the compatible ones by type
                .filter(manageable -> manageable.getClass().equals(type))
                // and stick them in a list
                .collect(Collectors.toList());
                
//        Manager.LOG.info("%d manageables found to be compatible with type %s.", collect.size(), type.toString());
        return collect;
    }
}
