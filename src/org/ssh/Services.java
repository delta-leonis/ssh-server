package org.ssh;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ssh.services.Consumer;
import org.ssh.services.Coupler;
import org.ssh.services.Pipeline;
import org.ssh.services.PipelinePacket;
import org.ssh.services.Producer;
import org.ssh.services.Service;
import org.ssh.services.ServicesController;
import org.ssh.util.Logger;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableScheduledFuture;

/**
 * The Class Services.
 *
 * This class is one of the main components of the framework. Services.java gets instantiated as
 * lazy-loaded Singleton {@link https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom}
 * . For Services functionality see {@link org.ssh.services.ServicesController}
 *
 * @author Rimon Oz
 */
public final class Services {
    
    /**
     * The org.ssh.services store has a controller that runs the store.
     */
    private static ServicesController servicesController = new ServicesController();
                                                         
    /** The instance. */
    private static final Object       instance           = new Object();
                                                         
    // a logger for good measure
    private static final Logger       LOG                = Logger.getLogger();
                                                         
    /**
     * Adds a org.ssh.services.pipeline that can be used by a
     * {@link org.ssh.services.Producer}, {@link org.ssh.services.Coupler}, or
     * {@link org.ssh.services.Consumer}.
     *
     * @param org.ssh.services.pipeline
     *            The org.ssh.services.pipeline to be added.
     * @return true, if successful.
     * @see org.ssh.services.ServicesController#addPipeline(Pipeline)
     */
    public static boolean addPipeline(final Pipeline<?> pipeline) {
        Services.LOG.info("Adding org.ssh.services.pipeline<%s>: %s",
                pipeline.getType(),
                pipeline.getClass().getName());
        return Services.servicesController.addPipeline(pipeline);
    }
    
    /**
     * Wraps {@link #addPipeline}.
     *
     * @param org.ssh.pipelines
     *            The org.ssh.pipelines to be added.
     */
    @SafeVarargs
    public static void addPipelines(final Pipeline<?>... pipelines) {
        Stream.of(pipelines)
                // .parallel()
                .forEach(Services::addPipeline);
    }
    
    /**
     * Adds a {@link org.ssh.services.Service} to the Services store.
     *
     * @param <T>
     *            A generic genericType extending Service.
     * @param service
     *            The service to be added.
     * @return true, if successful.
     * @see org.ssh.services.ServicesController#addService(Service)
     */
    public static <T extends Service<?>> boolean addService(final T service) {
        Services.LOG.info("Adding Service: " + service.getClass().getName());
        return Services.servicesController.addService(service);
    }
    
    /**
     * Wraps {@link #addService}.
     *
     * @param org.ssh.services
     *            The org.ssh.services to be added.
     */
    @SafeVarargs
    public static void addServices(final Service<?>... services) {
        Stream.of(services)
                // .parallel()
                .forEach(Services::addService);
    }
    
    /**
     * Finds a {@link org.ssh.services.Service} with the given name in the Services store.
     *
     * @param name
     *            The name of the wanted Service
     * @return The wanted Service.
     */
    public static Optional<? extends Service<? extends PipelinePacket>> get(final String name) {
        Services.LOG.info("Getting a Service named: %s", name);
        return Services.servicesController.get(name);
    }
    
    /**
     * Gets all the Services in the Services store.
     *
     * @return All the org.ssh.services.
     * @see org.ssh.services.ServicesController#getAll()
     */
    public static List<Service<PipelinePacket>> getAll() {
        return Services.servicesController.getAll();
    }
    
    /**
     * Gets a List of available {@link org.ssh.services.Consumer} compatible with the given
     * {@link org.ssh.services.Pipeline}.
     *
     * @param <T>
     *            The generic genericType of the {@link org.ssh.services.PipelinePacket} handled
     *            by the Consumer and Service.
     * @param org.ssh.services.pipeline
     *            The given org.ssh.services.pipeline.
     * @return The compatible Consumers.
     */
    public static <T extends PipelinePacket> List<Consumer<PipelinePacket>> getCompatibleConsumers(final Pipeline<T> pipeline) {
        Services.LOG.info("Getting compatible Consumers for genericType: %s", pipeline.getType().toString());

        @SuppressWarnings ("unchecked")
        // get the list of org.ssh.services
        final List<Consumer<PipelinePacket>> collect = (List<Consumer<PipelinePacket>>) Services.servicesController.getAll().stream()
                // filter out the org.ssh.services compatible with this org.ssh.services.pipeline
                .filter(service -> service.getType().equals(pipeline.getType()))
                // map them to the correct parameterized genericType and collect them in a list
                .map(service -> service.getType().getClass().cast(service)).collect(Collectors.toList());
                
        Services.LOG.info("%d Consumer found to be compatible with genericType %s",
                collect.size(),
                pipeline.getType().toString());
                
        return collect;
    }
    
    /**
     * Gets a List of available {@link org.ssh.services.Coupler} compatible with the given
     * {@link org.ssh.services.Pipeline}.
     *
     * @param <T>
     *            The generic genericType of the {@link org.ssh.services.PipelinePacket} handled
     *            by the Coupler and Service.
     * @param org.ssh.services.pipeline
     *            The given org.ssh.services.pipeline.
     * @return The compatible Couplers.
     */
    public static <T extends PipelinePacket> List<Coupler<PipelinePacket>> getCompatibleCouplers(final Pipeline<T> pipeline) {
        Services.LOG.info("Getting compatible Couplers for genericType: %s", pipeline.getType().toString());

        @SuppressWarnings ("unchecked")
        // get the list of org.ssh.services
        final List<Coupler<PipelinePacket>> collect = (List<Coupler<PipelinePacket>>) Services.servicesController.getAll().stream()
                // filter out the org.ssh.services compatible with this org.ssh.services.pipeline
                .filter(service -> service.getType().equals(pipeline.getType()))
                // map them to the correct parameterized genericType and collect them in a list
                .map(service -> service.getType().getClass().cast(service)).collect(Collectors.toList());
                
        Services.LOG.info("%d Couplers found to be compatible with genericType %s",
                collect.size(),
                pipeline.getType().toString());
                
        return collect;
    }
    
    /**
     * Gets a List of available {@link org.ssh.services.Producer} compatible with the given
     * {@link org.ssh.services.Pipeline}.
     *
     * @param <T>
     *            The generic genericType of the {@link org.ssh.services.PipelinePacket} handled
     *            by the Producer and Service.
     * @param org.ssh.services.pipeline
     *            The given org.ssh.services.pipeline.
     * @return The compatible Producers.
     */
    public static <T extends PipelinePacket> List<Producer<PipelinePacket>> getCompatibleProducers(final Pipeline<T> pipeline) {
        Services.LOG.info("Getting compatible Producers for genericType: %s", pipeline.getType().toString());

        @SuppressWarnings ("unchecked")
        // get the list of org.ssh.services
        final List<Producer<PipelinePacket>> collect = (List<Producer<PipelinePacket>>) Services.servicesController.getAll().stream()
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
     * Gets the {@link org.ssh.services.Pipeline} with the supplied name.
     *
     * @param name
     *            The name of the Pipeline.
     * @return The requested Pipeline.
     */
    public static Optional<? extends Pipeline<? extends PipelinePacket>> getPipeline(final String name) {
        Services.LOG.info("Getting Pipeline named %s", name);
        return Services.servicesController.getPipeline(name);
    }
    
    /**
     * Gets a list of {@link org.ssh.services.Pipeline} that operates on the supplied Type of
     * {@link org.ssh.services.PipelinePacket}.
     *
     * @param packetType
     *            The Type with which the Pipelines need to be compatible.
     * @return The list of compatible Pipelines.
     */
    public static List<Pipeline<PipelinePacket>> getPipelines(final Type packetType) {
        Services.LOG.info("Getting compatible Pipelines for genericType: %s", packetType.getTypeName());

        // get the list of org.ssh.pipelines
        final List<Pipeline<PipelinePacket>> collect = Services.servicesController.getPipelines().stream()
                // filter out the compatible ones by genericType
                .filter(pipeline -> pipeline.getType().equals(packetType))
                .collect(Collectors.toList());
                
        Services.LOG.info("%d Pipelines found to be compatible with genericType %s", collect.size(), packetType.toString());
        return collect;
    }
    
    /**
     * Periodically schedules a Runnable to be called with the given delay between termination of
     * the previous execution and start of the next execution.
     *
     * @param <T>
     *            The generic genericType of {@link org.ssh.services.PipelinePacket} which the
     *            Runnable produces.
     * @param taskName
     *            The name of the task
     * @param task
     *            The Runnable to be executed periodically.
     * @param delay
     *            Time between termination of previous execution and start of next execution in ms.
     * @return A ScheduledFuture that can be used to cancel the periodic execution.
     * @see org.ssh.services.ServicesController#scheduleTask(String, Runnable, long)
     */
    public static <T extends PipelinePacket> ListenableScheduledFuture<T> scheduleTask(final String taskName,
            final Runnable task,
            final long delay) {
        Services.LOG.info("Scheduling a task named %s with an interval of %d us", taskName, delay);
        return Services.servicesController.scheduleTask(taskName, task, delay);
    }
    
    /**
     * Starts the Services store.
     */
    public static void start() {
        Services.LOG.info("Starting Services...");
        Services.servicesController = new ServicesController();
    }
    
    /**
     * Submits a task to the threadpool.
     *
     * @param <T>
     *            The generic genericType of data generated by the task.
     * @param taskName
     *            The name of the task.
     * @param task
     *            The task as a Runnable.
     * @return A ListenableFuture representing the result of the task.
     * @see org.ssh.services.ServicesController#submitTask(Callable)
     */
    public static <T extends PipelinePacket> ListenableFuture<T> submitTask(final String taskName,
            final Callable<T> task) {
        Services.LOG.info("Submitting task named %s ...", taskName);
        return Services.servicesController.submitTask(task);
    }
    
    /**
     * Finds all org.ssh.services matching the name and returns them as an ArrayList<Service>.
     *
     * @param name
     *            The (fuzzy) name of the service you want to find.
     * @return The requested service.
     * @see org.ssh.services.ServicesController#getAll(String)
     */
    public List<Service<PipelinePacket>> getAll(final String name) {
        return Services.servicesController.getAll(name);
    }
}