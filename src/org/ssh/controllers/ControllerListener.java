package org.ssh.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import org.ssh.managers.manager.Services;
import org.ssh.services.Service;
import org.ssh.services.producers.Communicator;
import org.ssh.util.Logger;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.java.games.input.Controller;
import protobuf.Radio.RadioProtocolCommand;

/**
 * A {@link Service} that manages all {@link ControllerHandler controllers} for every
 * robotId
 *
 * @TODO Rimon fixt deze shit nog naar pipeline
 *      
 * @author Jeroen de Jong
 *         
 */
@SuppressWarnings ("rawtypes")
public class ControllerListener extends Service {
    
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
     * A reference to the {@link Communicator} service
     */
    private Communicator                      communicator;
    // respective logger
    private final static Logger               LOG      = Logger.getLogger();
                                                       
    /**
     * Create a controllerlistener
     * 
     * @param noRobots
     *            total number of robots
     */
    public ControllerListener(final int noRobots) {
        super("ControllerListener");
        // TODO remove noRobots, read it from Models or something
//        IntStream.range(0, noRobots).forEach(index -> this.handlers.put(index, null));
        this.communicator = (Communicator) Services.get("communicator").get();
    }
    
    /**
     * give the next or previous available robotid
     * 
     * @param currentIndex
     * @param forward
     *            give next available id on true, give previous on false
     */
    public void changeRobotId(final ControllerHandler handler, final boolean forward) {
        final int currentIndex = this.handlers.inverse().get(handler);
        this.tmpHandlers = HashBiMap.create(handlers);
        
        // free the id
        this.tmpHandlers.put(currentIndex, null);
        
        final List<Integer> list = this.tmpHandlers.entrySet().stream().filter(entry -> entry.getValue() == null)
                .map(entry -> entry.getKey()).collect(Collectors.toList());
                
        final ListIterator<Integer> it = list.listIterator();
        while (it.next() != currentIndex)
            ;
            
        int robotId = 0;
        if (forward)
            robotId = it.hasNext() ? it.next() : list.get(0);
        else {
            // since it currently between the current, and the next possible id,
            // it should go to the previous value: between the previous possible id, and the current
            // id
            it.previous();
            robotId = it.hasPrevious() ? it.previous() : list.get(list.size() - 1);
        }
        
        if (this.tmpHandlers.get(robotId) != null) {
            ControllerListener.LOG.fine("Could not find a available robotid.");
            return;
        }
        this.tmpHandlers.put(robotId, handler);
    }
    
    /**
     * @param controller
     *            controller to check
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
     * @param robotId
     *            robotId to check
     * @return whether given id has a handler (thus a controller) assigned
     */
    private boolean isAssigned(final int robotId) {
        return this.handlers.get(robotId) != null;
    }
    
    /**
     * Process every controller and its input
     * 
     * TODO don't give it to the Communicator straight away
     * 
     * @return succes value
     */
    public boolean processControllers() {
        // check if the list should change
        this.handlers = this.tmpHandlers == null ? this.handlers : this.tmpHandlers;
        this.tmpHandlers = null;
                
        return this.communicator.send((ArrayList<RadioProtocolCommand.Builder>)
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
     * @param robotId
     *            robotid to bound to the given {@link ControllerLayout}
     * @param controller
     *            Controllerlayout to bind
     * @return succes value
     */
    public boolean register(final int robotId, final ControllerLayout controller) {
        // check if the robotId is allready bound
        if (this.isAssigned(robotId)) {
            ControllerListener.LOG.warning("Handler for %d is not empty, and will be overwriten.\n", robotId);
            this.unregister(robotId);
        }
        
        this.handlers.put(robotId, new ControllerHandler(controller));
        return true;
    }
    
    /**
     * unregister the handler for a specific robotId
     * 
     * @param robotId
     *            robotId to unregister a handler for
     * @return succes value
     */
    public boolean unregister(final int robotId) {
        // check if it is assigned at all
        if (!this.isAssigned(robotId)) {
            ControllerListener.LOG.warning("Could not unregister controllerHandler for robot %d.\n", robotId);
            return false;
        }
        
        // replace the handler with null, let the garbage collector pick up te pieces
        this.handlers.put(robotId, null);
        return true;
    }
    
}