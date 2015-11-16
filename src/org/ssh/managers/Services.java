package org.ssh.managers;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ssh.managers.controllers.ServicesController;
import org.ssh.services.Consumer;
import org.ssh.services.Coupler;
import org.ssh.services.Pipeline;
import org.ssh.services.PipelinePacket;
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
     * Adds a pipeline that can be used by a {@link Producer}, {@link Coupler}, or {@link Consumer}.
     * 
     * @param
     *            <P>
     *            The generic type of {@link PipelinePacket}
     * @param pipeline
     *            The pipeline to be added.
     * @return true, if successful.
     * @see ServicesController#addPipeline(Pipeline)
     */
    public static <P extends PipelinePacket> boolean addPipeline(final Pipeline<P> pipeline) {
        Services.LOG.info("Adding pipeline<%s>: %s", pipeline.getType(), pipeline.getClass().getName());
        return Services.controller.addPipeline(pipeline);
    }
    
    /**
     * Wraps {@link #addPipeline}.
     *
     * @param
     *            <P>
     *            The generic type of {@link PipelinePacket}.
     * @param pipelines
     *            The pipelines to be added.
     */
    @SafeVarargs
    public static <P extends PipelinePacket> void addPipelines(final Pipeline<P>... pipelines) {
        Stream.of(pipelines).parallel().forEach(Services::addPipeline);
    }
    
    /**
     * Adds a {@link Service} to the Services store.
     *
     * @param <S>
     *            A generic type of Service
     * @param service
     *            The service to be added.
     * @return true, if successful.
     * @see ServicesController#addService(Service)
     */
    public static <S extends Service<? extends PipelinePacket>> boolean addService(final S service) {
        Services.LOG.info("Adding Service: " + service.getClass().getName());
        return Services.controller.add(service);
    }
    
    /**
     * Wraps {@link #addService}.
     *
     * @param services
     *            The services to be added.
     */
    @SafeVarargs
    public static void addServices(final Service<? extends PipelinePacket>... services) {
        Stream.of(services).parallel().forEach(service -> Services.controller.add(service));
    }
    
    /**
     * Gets a List of available {@link Consumer} compatible with the given {@link Pipeline}.
     *
     * @param
     *            <P>
     *            The generic type of {@link PipelinePacket} handled by the Consumer.
     * @param <C>
     *            The generic type Consumer compatible with the Pipeline.
     * @param pipeline
     *            The given pipeline.
     * @return The compatible Consumers.
     */
    public static <P extends PipelinePacket, C extends Consumer<P>> List<C> getCompatibleConsumers(
            final Pipeline<P> pipeline) {
        Services.LOG.info("Getting compatible Consumer(s) for type: %s", pipeline.getType().toString());
        
        @SuppressWarnings ("unchecked")
        // get the list of org.ssh.services
        final List<C> collect = (List<C>) Services.controller.getAll().stream()
                // filter out the services compatible with this pipeline
                .filter(service -> service.getType().equals(pipeline.getType()))
                // map them to the correct parameterized type and collect them in a list
                .map(service -> service.getType().getClass().cast(service)).collect(Collectors.toList());
                
        Services.LOG.info("%d Consumer(s) found to be compatible with type %s",
                collect.size(),
                pipeline.getType().toString());
                
        return collect;
    }
    
    /**
     * Gets a List of available {@link Coupler} compatible with the given {@link Pipeline}.
     *
     * @param
     *            <P>
     *            The generic type of {@link PipelinePacket} handled by the Consumer.
     * @param <C>
     *            The generic type Coupler compatible with the Pipeline.
     * @param pipeline
     *            The given pipeline.
     * @return The compatible Couplers.
     */
    public static <P extends PipelinePacket, C extends Coupler<P>> List<C> getCompatibleCouplers(
            final Pipeline<P> pipeline) {
        Services.LOG.info("Getting compatible Coupler(s) for type: %s", pipeline.getType().toString());
        
        @SuppressWarnings ("unchecked")
        // get the list of services
        final List<C> collect = (List<C>) Services.controller.getAll().stream()
                // filter out the services compatible with this pipeline
                .filter(service -> service.getType().equals(pipeline.getType()))
                // map them to the correct parameterized genericType and collect them in a list
                .map(service -> service.getType().getClass().cast(service)).collect(Collectors.toList());
                
        Services.LOG.info("%d Coupler(s) found to be compatible with type %s",
                collect.size(),
                pipeline.getType().toString());
                
        return collect;
    }
    
    /**
     * Gets a List of available {@link Producer} compatible with the given {@link Pipeline}.
     *
     * @param
     *            <P>
     *            The generic type of {@link PipelinePacket} handled by the Producer.
     * @param <C>
     *            The generic type Producer compatible with the Pipeline.
     * @param pipeline
     *            The given pipeline.
     * @return The compatible Producers.
     */
    public static <P extends PipelinePacket, C extends Producer<P>> List<C> getCompatibleProducers(
            final Pipeline<P> pipeline) {
        Services.LOG.info("Getting compatible Producers for genericType: %s", pipeline.getType().toString());
        
        @SuppressWarnings ("unchecked")
        // get the list of org.ssh.services
        final List<C> collect = (List<C>) Services.controller.getAll().stream()
                // filter out the org.ssh.services compatible with this org.ssh.services.pipeline
                .filter(service -> service.getType().equals(pipeline.getType()))
                // map them to the correct parametrized genericType and collect them in a list
                .map(service -> service.getType().getClass().cast(service)).collect(Collectors.toList());
                
        Services.LOG.info("%d Producers found to be compatible with genericType %s",
                collect.size(),
                pipeline.getType().toString());
                
        return collect;
    }
    
    /**
     * Gets the Singleton instance of Services.
     *
     * @return The single instance.
     */
    public static Object getInstance() {
        return Services.instance;
    }
    
    /**
     * Gets the {@link Pipeline} with the supplied name.
     *
     * @param name
     *            The name of the pipeline.
     * @return The requested pipeline.
     */
    public static Optional<? extends Pipeline<? extends PipelinePacket>> getPipeline(final String name) {
        Services.LOG.info("Getting Pipeline named %s", name);
        return Services.controller.getPipeline(name);
    }
    
    /**
     * Gets a list of {@link Pipeline} that operates on the supplied Type of
     * {@link org.ssh.services.PipelinePacket}.
     *
     * @param
     *            <P>
     *            The generic type Consumer compatible with the Pipeline.
     * @param <C>
     *            the generic type
     * @param packetType
     *            The Type with which the Pipelines need to be compatible.
     * @return The list of compatible Pipelines.
     */
    public static <P extends PipelinePacket, C extends Pipeline<P>> List<C> getPipelines(final Type packetType) {
        Services.LOG.info("Getting compatible Pipelines for genericType: %s", packetType.getTypeName());
        
        // get the list of org.ssh.pipelines
        @SuppressWarnings ("unchecked")
        final List<C> collect = (List<C>) Services.controller.getPipelines().stream()
                // filter out the compatible ones by genericType
                .filter(pipeline -> pipeline.getType().equals(packetType)).collect(Collectors.toList());
                
        Services.LOG.info("%d Pipelines found to be compatible with genericType %s",
                collect.size(),
                packetType.toString());
        return collect;
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