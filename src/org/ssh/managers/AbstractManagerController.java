package org.ssh.managers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.ssh.expressions.languages.Meme;
import org.ssh.models.enums.ManagerEvent;
import org.ssh.util.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The Class AbstractManagerController.
 * <p>
 * A ManagerController is used by a Manager to operate on Manageables. Since a Manager is static and
 * final another class is needed to operate on dynamic data.
 *
 * @param <M> The generic type of {@link AbstractManageable} this ManagerController operates on.
 * @author Rimon Oz
 * @author Jeroen de Jong
 */
public abstract class AbstractManagerController<M extends AbstractManageable> {

    /**
     * The manageables which are being managed by this controller.
     */
    protected Map<String, M> manageables;
    /**
     * The manageable expression parser. This is used to lookup
     * manageables by their name in the manageables map.
     */
    protected static Meme memeEngine;

    // unique logger
    protected static final Logger LOG = Logger.getLogger();

    /**
     * map containing all subscribers for elements
     */
    protected Multimap<ManagerEvent, Map.Entry<Class<?>, Consumer>> subscribers;

    /**
     * Start listening for a specific event
     * @param event       event to listen for
     * @param consumer    consumer to call when event happens
     * @param classes     class(es) that should trigger the event
     * @return true if listener has been added successful
     */
    public boolean addSubscription(ManagerEvent event, Consumer consumer, Class<?>... classes){
        // stream all classes
        return Stream.of(classes).map(clazz ->
                // add it to the map
                subscribers.put(event,
                        new AbstractMap.SimpleEntry<>(clazz, consumer)))
                // check if all went well
                .reduce(true, (accumulator, success) -> accumulator && success);
    }

    /**
     * Stop listening for a specific event for a specific (set of) class(es)
     * @param event     event to stop listening for
     * @param consumer  consumer to unsubscribe
     * @param classes   class to remove listener from
     * @return true if successful
     */
    public boolean removeSubscription(ManagerEvent event, Consumer consumer, Class<?>... classes) {
        // primitive array to list
        List<Class<?>> classList = Arrays.asList(classes);
        // stream all subscribers to this ManagerEvent
        return subscribers.get(event).stream()
                // filter the classes that has been provided in #classes
                .filter(set -> classList.contains(set.getKey()) &&
                        consumer.equals(set.getValue()))
                // remove the subscription
                .map(set ->
                        subscribers.remove(set.getKey(), set.getValue()))
                // make sure everything worked
                .reduce(true, (accumulator, success) -> accumulator && success);
    }

    /**
     * Sets up the controller.
     */
    public AbstractManagerController() {
        // set attributes
        this.manageables = new ConcurrentHashMap<>();

        // build the engine if it doesn't exist yet
        if (memeEngine == null)
            memeEngine = new Meme(name -> name);

        // create new HashMultiMap
        subscribers = HashMultimap.create();
    }

    /**
     * Finds all the Manageables whose true name matches the given pattern.
     *
     * @param pattern The pattern to match on.
     * @param <N>     The type of Manageable requested by the user.
     * @return The list of Manageables matching the given pattern.
     */
    @SuppressWarnings("unchecked")
    public <N extends AbstractManageable> List<N> find(String pattern) {
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
     * @param name the name of the manageable
     * @return An Optional representing the Manageable.
     */
    @SuppressWarnings("unchecked")
    public <N extends AbstractManageable> Optional<N> get(String name) {
        return (Optional<N>) Optional.ofNullable(this.manageables.get(name));
    }

    /**
     * Gets all the Manageables as a List.
     *
     * @return The List of Manageables
     */
    @SuppressWarnings("unchecked")
    public <N extends AbstractManageable> List<N> getAll() {
        return new ArrayList<>((Collection<? extends N>) this.manageables.values());
    }

    /**
     * Adds a {@link AbstractManageable} to the Manager with the default name.
     *
     * @param manageable The Manageable to be added.
     * @return true, if successful
     */
    public boolean add(final M manageable) {
        return this.put(manageable.getName(), manageable);
    }

    /**
     * Adds a {@link AbstractManageable} to the Manager with the specified name.
     *
     * @param manageable the Manageable to be added
     * @return true, if successful
     */
    public boolean put(final String name, final M manageable) {
        if (!this.manageables.containsValue(manageable)) {
            this.manageables.put(name, manageable);
            this.triggerEvent(ManagerEvent.CREATE, manageable);
            return true;
        }
        return false;
    }

    /**
     * Removes a {@link AbstractManageable} with the specified key from the list of Manageables.
     *
     * @param name The key belonging to the Manageable.
     * @param <N>  The type of Manageable requested by the user.
     * @return The removed Manageable.
     */
    @SuppressWarnings("unchecked")
    public <N extends AbstractManageable> N remove(final String name) {
        return (N) this.manageables.remove(name);
    }

    /**
     * Removes the supplied {@link AbstractManageable} from the list of Manageables if it is present in the list.
     *
     * @param manageable The Manageable to be removed.
     * @param <N>        The type of Manageable requested by the user.
     * @return The removed Manageable.
     */
    public <N extends AbstractManageable> N remove(final N manageable) {
        // check to see if the Manageable is in the list, return null otherwise
        if (!this.manageables.containsValue(manageable))
            return null;

        // loop through the list and remove the manageable if it is present anywhere
        this.manageables.forEach((key, value) -> {
            if (manageable.equals(value)) {
                this.manageables.remove(key);
                this.triggerEvent(ManagerEvent.DELETE, manageable);
            }
        });
        // return the removed manageable
        return manageable;
    }

    /**
     * Gets a list of manageables of the given type.
     *
     * @param <N>  The generic type of Manageable
     * @param type The type of the requested manageables
     * @return The list of manageables
     */
    @SuppressWarnings("unchecked")
    public <N extends AbstractManageable> List<N> getOfType(final Class<?> type) {
        // get the list of manageables
        return (List<N>) this.manageables.values().stream()
                // filter out the compatible ones by type
                .filter(manageable -> type.isAssignableFrom(manageable.getClass()))
                // and stick them in a list
                .collect(Collectors.toList());
    }

    /**
     * Manually trigger a specific event (such as {@link ManagerEvent#UPDATE}).
     *
     * @param event     event to trigger
     * @param object    instance that triggered the event
     */
    public void triggerEvent(ManagerEvent event, Object object){
        if(event == null || object == null || !subscribers.containsKey(event))
            return;

        //loop all subscribers that subscribed to this event
        subscribers.get(event).forEach(set -> {
            //check if they subscribed for this class
            if( set.getKey().isInstance(object))
                try{
                    // if so, start the consumer on the object
                    set.getValue().accept(object);
                }catch(Exception e){
                    AbstractManagerController.LOG.exception(e);
                }
        });
    }
}
