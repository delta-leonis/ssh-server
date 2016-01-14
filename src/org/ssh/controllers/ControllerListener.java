package org.ssh.controllers;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import org.ssh.managers.manager.Models;
import org.ssh.managers.manager.Network;
import org.ssh.managers.manager.Services;
import org.ssh.models.Robot;
import org.ssh.services.AbstractService;
import protobuf.Radio.RadioProtocolCommand;

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
    private BiMap<Integer, ControllerHandler> handlers = HashBiMap.create();
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
        return Stream.of(ControllerEnvironment.getDefaultEnvironment().getControllers())
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
    private boolean availableController(final Controller controller) {
        return !this.containsController(controller);
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
     * @param controller controller to check
     * @return whether given controller is in use
     */
    public boolean containsController(final Controller controller) {
        return this.handlers.entrySet().stream()
                // Only check handlers that actually have a handler assigned to a id
                .filter(entry -> entry.getValue() != null)
                // filter controllers that equal to a assigned controller
                .filter(entry -> entry.getValue().getLayout().getController().equals(controller))
                // found any? Then it is in use
                .count() > 0;
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
     * Register a specific {@link ControllerLayout} to a robotId
     *
     * @param robotId    robotid to bound to the given {@link ControllerLayout}
     * @param controller Controllerlayout to bind
     * @return succes value
     */
    public boolean register(final int robotId, final ControllerLayout controller) {
        // check if the robotId is allready bound
        if (this.isAssigned(robotId)) {
            ControllerListener.LOG.warning("Handler for %d is not empty, and will be overwriten.\n", robotId);
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
    public boolean unregister(final int robotId) {
        // check if it is assigned at all
        if (!this.isAssigned(robotId)) {
            ControllerListener.LOG.warning("Could not unregister controllerHandler for robot %d.\n", robotId);
            return false;
        }

        this.handlers.remove(robotId);
        return true;
    }

    /**
     * @return a robot id that doesn't have a controller yet. Optional.empty if no ID has been found
     */
    public OptionalInt findAvailableRobotid() {
        return IntStream.range(0, MAX_ROBOT_ID).filter(id -> !handlers.containsKey(id)).findAny();
    }
}