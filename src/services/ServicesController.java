package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import application.Services;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pipeline.Pipeline;
import pipeline.PipelinePacket;

/**
 * The Class ServicesController.
 * 
 * @author Rimon Oz
 */
public class ServicesController {

    /** The service pipeline list. */
    private final ObservableList<Pipeline<?>> servicePipelineList;

    /** The services list. */
    private final ObservableList<Service<?>>  servicesList;
    
    /** The scheduler service. */
    private final ScheduledExecutorService scheduler;
    
    private Map<String, ScheduledFuture<? extends PipelinePacket>> scheduledTasks;
    
    /** The completion service. */
    private final ListeningExecutorService taskService;

    /**
     * Instantiates a new services controller.
     */
    public ServicesController() {
        this.servicePipelineList = FXCollections.observableArrayList();
        this.servicesList        = FXCollections.observableArrayList();
        this.scheduler			 = Executors.newScheduledThreadPool(1);
        this.taskService   		 = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
        this.scheduledTasks		 = new HashMap<String, ScheduledFuture<? extends PipelinePacket>>();
    }

    /**
     * Adds the pipeline.
     *
     * @param pipeline the pipeline
     * @return true, if successful
     */
    public boolean addPipeline(Pipeline<?> pipeline) {
        return this.servicePipelineList.add(pipeline);
    }

    /**
     * Adds the pipelines.
     *
     * @param pipelines the pipelines
     * @return true, if successful
     */
    public boolean addPipelines(Pipeline<?>... pipelines) {
        return Stream.of(pipelines).map(pipeline -> this.addPipeline(pipeline))
            // collect all success values and reduce to true if all senders succeeded; false otherwise
            .reduce(true, (accumulator, success) -> accumulator && success);
    }

    /**
     * Adds the service.
     *
     * @param service the service
     * @return true, if successful
     */
    public boolean addService(Service<?> service) {
        return this.servicesList.add(service.getAsService());
    }

    /**
     * Adds the services.
     *
     * @param services the services
     * @return true, if successful
     */
    public boolean addServices(Service<?>... services) {
        return Stream.of(services).map(service -> this.addService(service))
            // collect all success values and reduce to true if all senders succeeded; false otherwise
            .reduce(true, (accumulator, success) -> accumulator && success);
    }

    /**
     * This method finds a service with the given name and returns it as a Service.
     * @param name      The name of the service you want to find.
     * @return          The requested service.
     */
    public Service<?> get(String name) {
        return (Service<?>) this.servicesList.stream().filter(service -> service.getName().equals(name)).findFirst().get();
    }

    /**
     * Gets all the services.
     *
     * @return all services
     */
    public ObservableList<Service<?>> getAll() {
        return this.servicesList;
    }

    /**
     * This method finds all services matching the name and returns them as an ArrayList<Service>
     * @param name      The (fuzzy) name of the service you want to find.
     * @return          The requested service.
     */
    public ArrayList<Service<?>> getAll(String name) {
        return (ArrayList<Service<?>>) this.servicesList.stream()
            .filter(service -> service.getName().equals(name))
            .collect(Collectors.toList());
    }
    
    /**
     * This method periodically schedules a Runnable to be called with the given delay between
     * termination of the previous execution and start of the next execution. 
     * @param task   The Runnable to be executed periodically
     * @param delay  Time between termination of previous execution and start of next execution in ms
     * @return       A ScheduledFuture that can be used to cancel the periodic execution
     */
    public <T extends PipelinePacket> ScheduledFuture<T> scheduleTask(String taskName, Runnable task, long delay) {
    	@SuppressWarnings("unchecked")
    	ScheduledFuture<T> scheduledFuture = (ScheduledFuture<T>) this.scheduler.scheduleWithFixedDelay(task, 0, delay, TimeUnit.MICROSECONDS);
    	this.scheduledTasks.put(taskName, scheduledFuture);
    	return scheduledFuture;
    }
    
    public <T extends PipelinePacket> ListenableFuture<T> runTask(Callable<T> task) {
    	return this.taskService.submit(task);
    }

    public <T extends PipelinePacket> ListenableFuture<T> runTaskAsProducer(Callable<T> task) {
    	ListenableFuture<T> producerFuture = this.runTask(task);
    	Futures.addCallback(producerFuture, new FutureCallback<T>() {

			@Override
			public void onFailure(Throwable failPacket) {
				// log error
			}

			@Override
			public void onSuccess(T successPacket) {
				// find generic type
				
				// find pipeline
				
				// add the packet
			}
    	});
    	return producerFuture;
    }
}