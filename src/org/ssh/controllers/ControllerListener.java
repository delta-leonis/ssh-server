package org.ssh.controllers;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import org.ssh.managers.manager.Models;
import org.ssh.managers.manager.Network;
import org.ssh.models.Robot;
import org.ssh.services.AbstractService;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A {@link AbstractService} that manages all {@link ControllerHandler controllers} for every
 * robotId
 *
 * @author Jeroen de Jong
 * @author Thomas Hakkers
 */
@SuppressWarnings("rawtypes")
public class ControllerListener extends AbstractService {

    private static final int MAX_ROBOT_ID = 12;
    /**
     * List of handlers<br/>
     * - Integer describes robotId<br/>
     * - ControllerHandler is the associated handler for that id, null if not bound<br/>
     */
    private BiMap<Integer, ControllerHandler> handlers = Maps.synchronizedBiMap(HashBiMap.<Integer, ControllerHandler>create());
    /**
     * List to temporarily store any changes in the handlers list. Is processed every
     * {@link #processControllers()} call. Whenever handlers should be updated, tmpHandlers != null.
     */
    private BiMap<Integer, ControllerHandler> tmpHandlers;

    /**
     * Create a controllerlistener
     */
    public ControllerListener() {
        super("ControllerListener");
    }

    /**
     * Find a controller that is not used in any ControllerHandler
     *
     * @param pattern the pattern to search a controller for
     * @return maybe a controller
     */
    public Optional<Controller> findAvailableController(String pattern) {
        return Stream.of(ControllerListener.createDefaultEnvironment().getControllers())
                // filter controllers that are available
                .filter(this::availableController)
                .filter(contr -> contr.getName().contains(pattern))
                // find the first in the list
                .findFirst();
    }
    /**
     * @param controller controller to check
     * @return whether a controller is available
     */
    public boolean availableController(final Controller controller) {
        return !this.handlers.containsValue(controller);
    }

    /**
     * give the next or previous available robotid
     *
     * @param forward give next available id on true, give previous on false
     */
    public void changeRobotId(final ControllerHandler handler, final boolean forward) {
        int currentIndex = this.handlers.inverse().get(handler);
        this.tmpHandlers = HashBiMap.create(handlers);

        // free the id
        this.tmpHandlers.remove(currentIndex);

        int robotId = currentIndex;
        if (forward){
            for (int newIndex = currentIndex+1; newIndex <= MAX_ROBOT_ID; newIndex++)
                if (!tmpHandlers.containsKey(newIndex)) {
                    robotId = newIndex;
                    break;
                }
        }else {
            for (int newIndex = currentIndex-1; newIndex >= 0; newIndex--)
                if (!tmpHandlers.containsKey(newIndex)) {
                    robotId = newIndex;
                    break;
                }
        }

        if (this.tmpHandlers.get(robotId) != null) {
            ControllerListener.LOG.fine("Could not find a available robotid.");
            return;
        }
        Models.<Robot>get("robot A" + currentIndex).ifPresent(robot -> robot.update("hasController", false));
        Models.<Robot>get("robot A" + robotId).ifPresent(robot -> robot.update("hasController", true));
        this.tmpHandlers.put(robotId, handler);
    }

    /**
     * @param robotId robotId to check
     * @return whether given id has a handler (thus a controller) assigned
     */
    private boolean isAssigned(final int robotId) {
        return this.handlers.containsKey(robotId);
    }

    /**
     * Process every controller and its input
     *
     * @return succes value
     */
    public boolean processControllers() {
        // check if the list should change
        this.handlers = this.tmpHandlers == null ? this.handlers : this.tmpHandlers;
        this.tmpHandlers = null;

        return Network.transmit(
                // get all handlers
                this.handlers.entrySet().stream()
                        // only process handlers with a ControllerHandler assigned
                        .filter(entry -> this.isAssigned(entry.getKey()))
                        // process every ControllerHandler
                        .map(entry -> entry.getValue().process(entry.getKey()))
                        // collect the packets to a list
                        .collect(Collectors.toList()));
    }

    /**
     * Extends the current controller library (jinput) to be able to search multiple times for controllers
     *
     * @return new rescanned ControllerEnvironment
     */
    private static ControllerEnvironment createDefaultEnvironment() {
        try {
            // Find constructor (class is package private, so we can't access it directly)
            Constructor<ControllerEnvironment> constructor = (Constructor<ControllerEnvironment>)
                    Class.forName("net.java.games.input.DefaultControllerEnvironment").getDeclaredConstructors()[0];

            // Constructor is package private, so we have to deactivate access control checks
            constructor.setAccessible(true);

            // Create object with default constructor
            return constructor.newInstance();
        }catch (Exception exception){
            ControllerListener.LOG.warning("Could not create new ControllerEnvironment");
            ControllerListener.LOG.exception(exception);
        }
        return null;
    }

    /**
     * Register a specific {@link ControllerLayout} to a robotId
     *
     * @param robotId    robotid to bound to the given {@link ControllerLayout}
     * @param controller Controllerlayout to bind
     * @return succes value
     */
    public boolean register(final int robotId, final ControllerLayout controller) {
        // check if the robotId is allready bound
        if (this.isAssigned(robotId)) {
            ControllerListener.LOG.warning("Handler for %d is not empty, and will be overwritten.\n", robotId);
            this.unregister(robotId);
        }

        Models.<Robot>get("robot A" + robotId).ifPresent(robot -> robot.update("hasController", true));
        this.handlers.put(robotId, new ControllerHandler(controller));
        return true;
    }

    /**
     * unregister the handler for a specific robotId
     *
     * @param robotId robotId to unregister a handler for
     * @return succes value
     */
    public ControllerHandler unregister(final int robotId) {
        // check if it is assigned at all
        if (!this.isAssigned(robotId)) {
            ControllerListener.LOG.warning("Could not unregister controllerHandler for robot %d.\n", robotId);
            return null;
        }
        this.tmpHandlers = HashBiMap.create(handlers);
        this.tmpHandlers.remove(robotId);
        return null;
    }

    /**
     * @return a robot id that doesn't have a controller yet. Optional.empty if no ID has been found
     */
    public OptionalInt findAvailableRobotid() {
        return IntStream.range(0, MAX_ROBOT_ID).filter(id -> !handlers.containsKey(id)).findAny();
    }
}