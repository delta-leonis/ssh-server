package application;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableScheduledFuture;

import pipeline.Pipeline;
import pipeline.PipelinePacket;
import services.Consumer;
import services.Coupler;
import services.Producer;
import services.Service;
import services.ServicesController;
import util.Logger;

/**
 * The Class Services.
 * 
 * This class is one of the main components of the framework. Services.java gets
 * instantiated as lazy-loaded Singleton {@link https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom}.
 * For Services functionality see {@link services.ServicesController}
 *
 * @author Rimon Oz
 */
public final class Services {
    /**
     * The services store has a controller that runs the store.
     */
    private static ServicesController servicesController = new ServicesController();

    /** The instance. */
    private static final Object instance = new Object();

    // a logger for good measure
    private static final Logger logger = Logger.getLogger();

    /**
     * Adds a pipeline that can be used by a {@link services.Producer}, {@link services.Coupler}, or {@link services.Consumer}. 
     *
     * @param pipeline The pipeline to be added.
     * @return         true, if successful.
     * @see            services.ServicesController#addPipeline(Pipeline)
     */
    public static boolean addPipeline(Pipeline<?> pipeline) {
        Services.logger.info("Adding pipeline<%s>: %s", pipeline.getType(), pipeline.getClass().getName());
        return Services.servicesController.addPipeline(pipeline);
    }

    /**
     * Wraps {@link #addPipeline}.
     *
     * @param pipelines The pipelines to be added.
     */
    @SafeVarargs
    public static void addPipelines(Pipeline<?>... pipelines) {
        Stream.of(pipelines)
            // .parallel()
            .forEach(Services::addPipeline);
    }

    /**
     * Adds a {@link services.Service} to the Services store.
     *
     * @param <T>     A generic type extending Service.
     * @param service The service to be added.
     * @return        true, if successful.
     * @see           services.ServicesController#addService(Service)
     */
    public static <T extends Service<?>> boolean addService(T service) {
        Services.logger.info("Adding Service: " + service.getClass().getName());
        return Services.servicesController.addService(service);
    }

    /**
     * Wraps {@link #addService}.
     *
     * @param services The services to be added.
     */
    @SafeVarargs
    public static void addServices(Service<?>... services) {
        Stream.of(services)
            // .parallel()
            .forEach(Services::addService);
    }

    /**
     * Finds a {@link services.Service} with the given name in the Services store.
     *
     * @param name The name of the wanted Service
     * @return     The wanted Service.
     */
    public static Service<?> get(String name) {
        Services.logger.info("Getting a Service named: %s", name);
        return Services.servicesController.get(name);
    }

    /**
     * Gets all the Services in the Services store.
     *
     * @return All the services.
     * @see    services.ServicesController#getAll()
     */
    public static List<Service<?>> getAll() {
        return Services.servicesController.getAll();
    }

    /**
     * Gets a List of available {@link services.Consumer} compatible with the given {@link pipeline.Pipeline}.
     *
     * @param <T>      The generic type of the {@link pipeline.PipelinePacket} handled by the Consumer and Service.
     * @param pipeline The given pipeline.
     * @return         The compatible Consumers.
     */
    public static <T extends PipelinePacket> List<Consumer<T>> getCompatibleConsumers(Pipeline<T> pipeline) {
        Services.logger.info("Getting compatible Consumers for type: %s", pipeline.type.toString());
        
        @SuppressWarnings("unchecked")
        // get the list of services
        final List<Consumer<T>> collect = (List<Consumer<T>>) Services.servicesController.getAll().stream()
            // filter out the services compatible with this pipeline
            .filter(service -> service.type.equals(pipeline.type))
            // map them to the correct parametrized type and collect them in a list
            .map(service -> service.getDataType().getClass().cast(service)).collect(Collectors.toList());
        
        Services.logger.info("%d Consumer found to be compatible with type %s", collect.size(),
            pipeline.type.toString());
        
        return collect;
    }

    /**
     * Gets a List of available {@link services.Coupler} compatible with the given {@link pipeline.Pipeline}.
     *
     * @param <T>      The generic type of the {@link pipeline.PipelinePacket} handled by the Coupler and Service.
     * @param pipeline The given pipeline.
     * @return         The compatible Couplers.
     */
    public static <T extends PipelinePacket> List<Coupler<T>> getCompatibleCouplers(Pipeline<T> pipeline) {
        Services.logger.info("Getting compatible Couplers for type: %s", pipeline.type.toString());
        
        @SuppressWarnings("unchecked")
        // get the list of services
        final List<Coupler<T>> collect = (List<Coupler<T>>) Services.servicesController.getAll().stream()
            // filter out the services compatible with this pipeline
            .filter(service -> service.type.equals(pipeline.type))
            // map them to the correct parametrized type and collect them in a list
            .map(service -> service.getDataType().getClass().cast(service)).collect(Collectors.toList());
        
        Services.logger.info("%d Couplers found to be compatible with type %s", collect.size(),
                pipeline.type.toString());
        
        return collect;
    }

    /**
     * Gets a List of available {@link services.Producer} compatible with the given {@link pipeline.Pipeline}.
     *
     * @param <T>      The generic type of the {@link pipeline.PipelinePacket} handled by the Producer and Service.
     * @param pipeline The given pipeline.
     * @return         The compatible Producers.
     */
    public static <T extends PipelinePacket> List<Producer<T>> getCompatibleProducers(Pipeline<T> pipeline) {
        Services.logger.info("Getting compatible Producers for type: %s", pipeline.type.toString());
        
        @SuppressWarnings("unchecked")
        // get the list of services
        final List<Producer<T>> collect = (List<Producer<T>>) Services.servicesController.getAll().stream()
            // filter out the services compatible with this pipeline
            .filter(service -> service.type.equals(pipeline.type))
            // map them to the correct parametrized type and collect them in a list
            .map(service -> service.getDataType().getClass().cast(service)).collect(Collectors.toList());
        
        Services.logger.info("%d Producers found to be compatible with type %s", collect.size(),
                pipeline.type.toString());
        
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
     * Gets the {@link pipeline.Pipeline} with the supplied name.
     *
     * @param name The name of the Pipeline.
     * @return     The requested Pipeline.
     */
    public static <T extends PipelinePacket> Pipeline<?> getPipeline(String name) {
        Services.logger.info("Getting Pipeline named %s", name);
        Pipeline<T> pipeline = Services.servicesController.getPipeline(name);
        logger.info(pipeline.getType().toString());
        return pipeline;
    }

    /**
     * Gets a list of {@link pipeline.Pipeline} that operates on the supplied Type of {@link pipeline.PipelinePacket}.
     *
     * @param packetType The Type with which the Pipelines need to be compatible.
     * @return           The list of compatible Pipelines.
     */
    public static List<Pipeline<?>> getPipelines(Type packetType) {
        Services.logger.info("Getting compatible Pipelines for type: %s", packetType.getTypeName());
        
        // get the list of pipelines
        final List<Pipeline<?>> collect = Services.servicesController.getPipelines().stream()
            // filter out the compatible ones by type
            .filter(pipeline -> pipeline.getType().equals(packetType)).collect(Collectors.toList());
        
        Services.logger.info("%d Pipelines found to be compatible with type %s", collect.size(), packetType.toString());
        return collect;
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
     * @see            services.ServicesController#scheduleTask(String, Runnable, long)
     */
    public static <T extends PipelinePacket> ListenableScheduledFuture<T> scheduleTask(String taskName, Runnable task, long delay) {
        Services.logger.info("Scheduling a task named %s with an interval of %d us", taskName, delay);
        return Services.servicesController.scheduleTask(taskName, task, delay);
    }

    /**
     * Starts the Services store.
     */
    public static void start() {
        Services.logger.info("Starting Services...");
        Services.servicesController = new ServicesController();
    }

    /**
     * Submits a task to the threadpool.
     *
     * @param <T>      The generic type of data generated by the task.
     * @param taskName The name of the task.
     * @param task     The task as a Runnable.
     * @return         A ListenableFuture representing the result of the task.
     * @see            services.ServicesController#submitTask(Callable)
     */
    public static <T extends PipelinePacket> ListenableFuture<T> submitTask(String taskName, Callable<T> task) {
        Services.logger.info("Submitting task named %s ...", taskName);
        return Services.servicesController.submitTask(task);
    }

    /**
     * Finds all services matching the name and returns them as an
     * ArrayList<Service>.
     *
     * @param name The (fuzzy) name of the service you want to find.
     * @return     The requested service.
     * @see        services.ServicesController#getAll(String)
     */
    public ArrayList<Service<?>> getAll(String name) {
        return Services.servicesController.getAll(name);
    }
}