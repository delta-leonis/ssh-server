package org.ssh.managers.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.ssh.managers.AbstractManagerController;
import org.ssh.managers.manager.Services;
import org.ssh.pipelines.AbstractPipelinePacket;
import org.ssh.services.AbstractService;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableScheduledFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * The Class ServicesController.
 *
 * ServicesController is responsible for maintaining {@link Services}. It holds references to the
 * Services and has thread management functionality.
 *
 * @author Rimon Oz
 */
public class ServicesController extends AbstractManagerController<AbstractService<? extends AbstractPipelinePacket<? extends Object>>> {
    
    /** The scheduler service. */
    private final ListeningScheduledExecutorService                      scheduler;
                                                                         
    /** The scheduled tasks. */
    private final Map<String, ScheduledFuture<? extends AbstractPipelinePacket<? extends Object>>> scheduledTasks;
                                                                         
    /** The completion service. */
    private final ListeningExecutorService                               taskService;
                                                                         
    /**
     * Instantiates a new Services controller.
     */
    public ServicesController() {
        this.scheduler = MoreExecutors.listeningDecorator(Executors.newScheduledThreadPool(1));
        this.taskService = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
        this.scheduledTasks = new HashMap<String, ScheduledFuture<? extends AbstractPipelinePacket<? extends Object>>>();
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
     */
    @SuppressWarnings ("unchecked")
    public <L> ListenableScheduledFuture<L> scheduleTask(final String taskName, final Runnable task, final long delay) {
        // schedule the task
        final ListenableScheduledFuture<?> scheduledFuture = this.scheduler.scheduleWithFixedDelay(task,
                0,
                delay,
                TimeUnit.MICROSECONDS);
        // save the ListenableFuture for future use
        this.scheduledTasks.put(taskName, (ScheduledFuture<? extends AbstractPipelinePacket<? extends Object>>) scheduledFuture);
        
        return (ListenableScheduledFuture<L>) scheduledFuture;
    }
    
    /**
     * Submits a task to the threadpool which returns a PipelinePacket.
     *
     * @param
     *            <P>
     *            The generic type of data generated by the task.
     * @param task
     *            The task as a Runnable.
     * @return A ListenableFuture representing the result of the task.
     */
    public <P extends AbstractPipelinePacket<? extends Object>> ListenableFuture<P> submitTask(final Callable<P> task) {
        return this.taskService.submit(task);
    }
    
    /**
     * Submits a task to the threadpool.
     *
     * @param task
     *            The task as a Runnable.
     * @return A ListenableFuture representing the result of the task.
     */
    public <L> ListenableFuture<L> submitTask(final Runnable task) {
        return (ListenableFuture<L>) this.taskService.submit(task);
    }
    
}