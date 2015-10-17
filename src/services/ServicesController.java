package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableScheduledFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import pipeline.Pipeline;
import pipeline.PipelinePacket;

/**
 * The Class ServicesController.
 * 
 * ServicesController is responsible for maintaining {@link application.Services}. It holds references
 * to the Pipelines, Producers, Couplers, and Consumers.
 *
 * @author Rimon Oz
 */
public class ServicesController {

    /** The service pipeline list. */
    private ImmutableList<Pipeline<?>> pipelineList;

    /** The services list. */
    private ImmutableList<Service<?>> servicesList;

    /** The scheduler service. */
    private final ListeningScheduledExecutorService scheduler;

    /** The scheduled tasks. */
    private final Map<String, ScheduledFuture<? extends PipelinePacket>> scheduledTasks;

    /** The completion service. */
    private final ListeningExecutorService taskService;

    /**
     * Instantiates a new services controller.
     */
    public ServicesController() {
        // set all the attributes
        this.pipelineList   = ImmutableList.of();
        this.servicesList   = ImmutableList.of();
        this.scheduler      = MoreExecutors.listeningDecorator(Executors.newScheduledThreadPool(1));
        this.taskService    = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
        this.scheduledTasks = new HashMap<String, ScheduledFuture<? extends PipelinePacket>>();
    }

    /**
     * Adds a {@link pipeline.Pipeline} to the list of Pipelines.
     *
     * @param pipeline The pipeline to be added.
     * @return         true, if successful
     */
    public boolean addPipeline(Pipeline<?> pipeline) {
        this.pipelineList = ImmutableList.<Pipeline<?>> builder().addAll(this.pipelineList).add(pipeline).build();
        return true;
    }

    /**
     * Adds a list of Pipelines to the Services store.
     *
     * @param pipelines The Pipelines to be added.
     * @return          true, if successful
     */
    public boolean addPipelines(Pipeline<?>... pipelines) {
        return Stream.of(pipelines).map(pipeline -> this.addPipeline(pipeline))
            // collect all success values and reduce to true if all senders
            // succeeded; false otherwise
            .reduce(true, (accumulator, success) -> accumulator && success);
    }

    /**
     * Adds a {@link service.Service} to the Services store.
     *
     * @param service The Service to be added.
     * @return        true, if successful
     */
    public boolean addService(Service<?> service) {
        this.servicesList = ImmutableList.<Service<?>> builder().addAll(this.servicesList).add(service).build();
        return true;
    }

    /**
     * Adds a list of Services to the Services store.
     *
     * @param services The Services to be added.
     * @return         true, if successful
     */
    public boolean addServices(Service<?>... services) {
        return Stream.of(services).map(service -> this.addService(service))
            // collect all success values and reduce to true if all senders
            // succeeded; false otherwise
            .reduce(true, (accumulator, success) -> accumulator && success);
    }

    /**
     * Finds a service with the given name and returns it as a
     * Service.
     *
     * @param name The name of the requested Service.
     * @return     The requested service.
     */
    public Service<?> get(String name) {
        return this.servicesList.stream().filter(service -> service.getName().equals(name)).findFirst().get();
    }

    /**
     * Gets all the Services in the Services store.
     *
     * @return All the Services.
     */
    public List<Service<?>> getAll() {
        return this.servicesList;
    }

    /**
     * Finds all services matching the name and returns them as an
     * ArrayList<Service>.
     *
     * @param name The (fuzzy) name of the service you want to find.
     * @return     The requested service.
     */
    public ArrayList<Service<?>> getAll(String name) {
        return (ArrayList<Service<?>>) this.servicesList.stream().filter(service -> service.getName().equals(name))
            .collect(Collectors.toList());
    }

    /**
     * Gets a {@link pipeline.Pipeline} from the Services store with the given name.
     *
     * @param name The name of the requested Pipeline.
     * @return     The requested Pipeline.
     */
    public Pipeline<?> getPipeline(String name) {
        return this.pipelineList.stream().filter(pipeline -> pipeline.getName().equals(name)).findFirst().get();
    }

    /**
     * Gets all the Pipelines in the Services store.
     *
     * @return All the Pipelines in the Services store.
     */
    public List<Pipeline<?>> getPipelines() {
        return this.pipelineList.stream().collect(Collectors.toList());
    }

    /**
     * Periodically schedules a Runnable to be called with the given
     * delay between termination of the previous execution and start of the next
     * execution.
     *
     * @param <T>      The generic type of {@link pipeline.PipelinePacket} which the Runnable produces.
     * @param taskName The name of the task
     * @param task     The Runnable to be executed periodically.
     * @param delay    Time between termination of previous execution and start of
     *                 next execution in ms.
     * @return         A ScheduledFuture that can be used to cancel the periodic
     *                 execution.
     */
    public <T extends PipelinePacket> ListenableScheduledFuture<T> scheduleTask(String taskName, Runnable task,    long delay) {
        @SuppressWarnings("unchecked")
        // schedule the task
        final ListenableScheduledFuture<T> scheduledFuture = (ListenableScheduledFuture<T>) this.scheduler
            .scheduleWithFixedDelay(task, 0, delay, TimeUnit.MICROSECONDS);
        // save the ListenableFuture for future use
        this.scheduledTasks.put(taskName, scheduledFuture);
        
        return scheduledFuture;
    }

    /**
     * Submits a task to the threadpool.
     *
     * @param <T>      The generic type of data generated by the task.
     * @param taskName The name of the task.
     * @param task     The task as a Runnable.
     * @return         A ListenableFuture representing the result of the task.
     */
    public <T extends PipelinePacket> ListenableFuture<T> submitTask(Callable<T> task) {
        return this.taskService.submit(task);
    }
}