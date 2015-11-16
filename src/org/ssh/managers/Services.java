package org.ssh.managers;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ssh.managers.controllers.ServicesController;
import org.ssh.pipelines.Pipeline;
import org.ssh.pipelines.PipelinePacket;
import org.ssh.services.Consumer;
import org.ssh.services.Coupler;
import org.ssh.services.Producer;
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
public final class Services extends Manager<Service<? extends PipelinePacket>> {
    
    /**
     * The Services store has a controller that runs the store.
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
     * Starts the Services store.
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
     *            the generic type
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
    
}