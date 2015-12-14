package org.ssh.managers;

import org.ssh.expressions.languages.Meme;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
    
    /** The manageables which are being managed by this controller. */
    protected Map<String, M> manageables;
    /**
     * The manageable expression parser. This is used to lookup
     * manageables by their name in the manageables map.
     */
    protected static Meme memeEngine;
    
    /**
     * Sets up the controller.
     */
    public ManagerController() {
        // set attributes
        this.manageables = new ConcurrentHashMap<>();
        // build the engine if it doesn't exist yet
        if (memeEngine == null) {
            memeEngine = new Meme();
        }
    }

    /**
     * Finds all the Manageables whose true name matches the given pattern.
     * @param pattern   The pattern to match on.
     * @param <N>       The type of Manageable requested by the user.
     * @return          The list of Manageables matching the given pattern.
     */
    @SuppressWarnings("unchecked")
    public <N extends Manageable> List<N> find(String pattern) {
        // evaluate the pattern
        List<String> possibilities = memeEngine.evaluate(pattern);
        // parallelize a search
        return (List<N>) possibilities.stream().parallel()
            .map(possibility -> this.manageables.entrySet().stream().parallel()
                // filter on a partial match of the string
                .filter(entry -> entry.getKey().contains(possibility))
                // map the match to the target object
                .map(Map.Entry::getValue)
                // collect the unique values in a list
                .collect(Collectors.toList()))
            .flatMap(Collection::stream).distinct().collect(Collectors.toList());
    }

    /**
     * Gets the Manageable with the specified name as an Optional<Manageable>.
     *
     * @param name
     *            the name of the manageable
     * @return An Optional representing the Manageable.
     */
    @SuppressWarnings("unchecked")
    public <N extends Manageable> Optional<N> get(String name) {
        return (Optional<N>) Optional.ofNullable(this.manageables.get(name));
    }
    
    /**
     * Gets all the Manageables as a List.
     *
     * @return The List of Manageables
     */
    @SuppressWarnings("unchecked")
    public <N extends Manageable> List<N> getAll() {
        return new ArrayList<>((Collection<? extends N>) this.manageables.values());
    }

    /**
     * Adds a {@link Manageable} to the Manager with the default name.
     * @param manageable The Manageable to be added.
     * @return           true, if successful
     */
    public boolean add(final M manageable) {
        return this.put(manageable.getName(), manageable);
    }

    /**
     * Adds a {@link Manageable} to the Manager with the specified name.
     *
     * @param manageable
     *            the Manageable to be added
     * @return true, if successful
     */
    public boolean put(final String name, final M manageable) {
        if (!this.manageables.containsValue(manageable)) {
            this.manageables.put(name, manageable);
            return true;
        }
        return false;
    }

    /**
     * Removes a {@link Manageable} with the specified key from the list of Manageables.
     * @param name  The key belonging to the Manageable.
     * @param <N>   The type of Manageable requested by the user.
     * @return      The removed Manageable.
     */
    @SuppressWarnings("unchecked")
    public <N extends Manageable> N remove(final String name) {
        return (N) this.manageables.remove(name);
    }

    /**
     * Removes the supplied {@link Manageable} from the list of Manageables if it is present in the list.
     * @param manageable    The Manageable to be removed.
     * @param <N>           The type of Manageable requested by the user.
     * @return              The removed Manageable.
     */
    public <N extends Manageable> N remove(final N manageable){
        // check to see if the Manageable is in the list, return null otherwise
        if(!this.manageables.containsValue(manageable))
            return null;

        // loop through the list and remove the manageable if it is present anywhere
        this.manageables.forEach((key, value) -> {
            if (manageable.equals(value))
                this.manageables.remove(key);
        });
        // return the removed manageable
        return manageable;
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
    @SuppressWarnings("unchecked")
    public <N extends Manageable> List<N> getOfType(final Class<?> type) {
        // get the list of manageables
        return (List<N>) this.manageables.values().stream()
                // filter out the compatible ones by type
                .filter(manageable -> manageable.getClass().equals(type))
                // and stick them in a list
                .collect(Collectors.toList());
    }
}
