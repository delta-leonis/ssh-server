package org.ssh.managers.manager;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import org.ssh.managers.Manageable;
import org.ssh.managers.Manager;
import org.ssh.managers.ManagerController;
import org.ssh.managers.controllers.ServicesController;
import org.ssh.pipelines.PipelinePacket;
import org.ssh.services.Service;
import org.ssh.util.Logger;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableScheduledFuture;

/**
 * The Class Services.
 *
 * This class is one of the main components of the framework. Services.java gets instantiated as
 * lazy-loaded Singleton {@link https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom}
 * . For Services functionality see {@link ServicesController}
 *
 * @author Rimon Oz
 */
public final class Services implements Manager<Service<? extends PipelinePacket>> {
    
    /**
     * The Services manager has a controller that runs the place.
     */
    private static ServicesController controller = new ServicesController();
                                                 
    /** The instance. */
    private static final Object       instance   = new Object();
                                                 
    // a logger for good measure
    private static final Logger       LOG        = Logger.getLogger();
                                                 
    /**
     * Gets the Singleton instance of Services.
     *
     * @return The single instance.
     */
    public static Object getInstance() {
        return Services.instance;
    }
    
    /**
     * Periodically schedules a Runnable to be called with the given delay between termination of
     * the previous execution and start of the next execution.
     *
     * @param taskName
     *            The name of the task
     * @param task
     *            The Runnable to be executed periodically.
     * @param delay
     *            Time between termination of previous execution and start of next execution in ms.
     * @return A ScheduledFuture that can be used to cancel the periodic execution.
     * @see org.ssh.managers.controllers.ServicesController#scheduleTask(String, Runnable, long)
     */
    public static ListenableScheduledFuture<?> scheduleTask(final String taskName,
            final Runnable task,
            final long delay) {
        Services.LOG.info("Scheduling a task named %s with an interval of %d us", taskName, delay);
        return Services.controller.scheduleTask(taskName, task, delay);
    }
    
    /**
     * Starts the Services manager.
     */
    public static void start() {
        Services.LOG.info("Starting Services...");
        Services.controller = new ServicesController();
    }
    
    /**
     * Submits a task to the threadpool.
     *
     * @param
     *            <P>
     *            The generic type of PipelinePacket produced by this task.
     * @param taskName
     *            The name of the task.
     * @param task
     *            The task as a Callable.
     * @return A ListenableFuture representing the result of the task.
     * @see org.ssh.managers.controllers.ServicesController#submitTask(Callable)
     */
    public static <P extends PipelinePacket> ListenableFuture<P> submitTask(final String taskName,
            final Callable<P> task) {
        Services.LOG.info("Submitting task named %s ...", taskName);
        return Services.controller.submitTask(task);
    }
    
    /**
     * Submits a task to the threadpool.
     *
     * @param taskName
     *            The name of the task.
     * @param task
     *            The task as a Runnable.
     * @return A ListenableFuture representing the result of the task.
     * @see org.ssh.managers.controllers.ServicesController#submitTask(Callable)
     */
    public static ListenableFuture<?> submitTask(final String taskName, final Runnable task) {
        Services.LOG.info("Submitting task named %s ...", taskName);
        return Services.controller.submitTask(task);
    }
    
    /**
     * Finds a {@link Service} with the given name in the Services manager.
     *
     * @param name
     *            The name of the wanted Services
     * @return The requested Service
     */
    public static Optional<? extends Service<? extends PipelinePacket>> get(final String name) {
        Services.LOG.fine("Getting a Service named: %s", name);
        return Services.controller.get(name);
    }
    
    /**
     * Gets all the Services in the Services manager.
     *
     * @return All the Services
     * @see org.ssh.managers.ManagerController#getAll()
     */
    public static List<? extends Service<? extends PipelinePacket>> getAll() {
        return Services.controller.getAll();
    }
    
    /**
     * Finds all Services matching the name and returns them as a List.
     *
     * @param name
     *            The name of the Service you want to find.
     * @return The requested Service.
     * @see ManagerController#getAll(String)
     */
    public static List<? extends Service<? extends PipelinePacket>> getAll(final String name) {
        return Services.controller.getAll(name);
    }
    
    /**
     * Adds a {@link Service} to the Services manager.
     *
     * @param service
     *            The service to be added.
     * @return true, if successful.
     * @see ManagerController#add(Manageable)
     */
    public static <S extends Service<? extends PipelinePacket>> boolean add(final S service) {
        Services.LOG.info("Adding Service: " + service.getClass().getName());
        return Services.controller.add(service);
    }
    
    /**
     * Wraps {@link #add}.
     *
     * @param services
     *            The Services to be added
     * @return true if all succeeded, false otherwise
     */
    @SafeVarargs
    public static <S extends Service<? extends PipelinePacket>> boolean addAll(final S... services) {
        return Stream.of(services).map(manageable -> Services.controller.add(manageable))
                // collect all success values and reduce to true if all senders
                // succeeded; false otherwise
                .reduce(true, (accumulator, success) -> accumulator && success);
    }
    
    /**
     * Gets a list of Services of the given type.
     *
     * @param <S>
     *            The generic type of Service
     * @param type
     *            The type of the requested Service
     * @return The list of Services matching the supplied type
     */
    public static <S extends Service<? extends PipelinePacket>> List<S> getOfType(final Class<?> type) {
        return Services.controller.getOfType(type);
    }
}