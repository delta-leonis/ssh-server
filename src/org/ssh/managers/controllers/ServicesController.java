package org.ssh.managers.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ssh.managers.ManagerController;
import org.ssh.pipelines.Pipeline;
import org.ssh.pipelines.PipelinePacket;
import org.ssh.services.Service;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableScheduledFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * The Class ServicesController.
 *
 * ServicesController is responsible for maintaining {@link org.ssh.managers.Services}. It holds
 * references to the Pipelines, Producers, Couplers, and Consumers.
 *
 * @author Rimon Oz
 */
public class ServicesController extends ManagerController<Service<? extends PipelinePacket>> {
    
    /** The service org.ssh.services.pipeline list. */
    private ImmutableList<Pipeline<? extends PipelinePacket>>            pipelineList;
                                                                         
    /** The scheduler service. */
    private final ListeningScheduledExecutorService                      scheduler;
                                                                         
    /** The scheduled tasks. */
    private final Map<String, ScheduledFuture<? extends PipelinePacket>> scheduledTasks;
                                                                         
    /** The completion service. */
    private final ListeningExecutorService                               taskService;
                                                                         
    /**
     * Instantiates a new org.ssh.services controller.
     */
    public ServicesController() {
        // set all the attributes
        this.pipelineList = ImmutableList.of();
        this.manageables = ImmutableList.of();
        this.scheduler = MoreExecutors.listeningDecorator(Executors.newScheduledThreadPool(1));
        this.taskService = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
        this.scheduledTasks = new HashMap<String, ScheduledFuture<? extends PipelinePacket>>();
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
    public ListenableScheduledFuture<?> scheduleTask(final String taskName, final Runnable task, final long delay) {
        // schedule the task
        final ListenableScheduledFuture<?> scheduledFuture = this.scheduler.scheduleWithFixedDelay(task,
                0,
                delay,
                TimeUnit.MICROSECONDS);
        // save the ListenableFuture for future use
        this.scheduledTasks.put(taskName, (ScheduledFuture<? extends PipelinePacket>) scheduledFuture);
        
        return scheduledFuture;
    }
    
    /**
     * Submits a task to the threadpool which returns a PipelinePacket.
     *
     * @param <T>
     *            The generic type of data generated by the task.
     * @param taskName
     *            The name of the task.
     * @param task
     *            The task as a Runnable.
     * @return A ListenableFuture representing the result of the task.
     */
    public <P extends PipelinePacket> ListenableFuture<P> submitTask(final Callable<P> task) {
        return this.taskService.submit(task);
    }
    
    /**
     * Submits a task to the threadpool.
     *
     * @param <T>
     *            The generic type of data generated by the task.
     * @param taskName
     *            The name of the task.
     * @param task
     *            The task as a Runnable.
     * @return A ListenableFuture representing the result of the task.
     */
    public ListenableFuture<?> submitTask(final Runnable task) {
        return this.taskService.submit(task);
    }
    
}