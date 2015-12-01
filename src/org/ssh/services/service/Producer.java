package org.ssh.services.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.ssh.managers.manager.Pipelines;
import org.ssh.managers.manager.Services;
import org.ssh.models.enums.ProducerType;
import org.ssh.pipelines.Pipeline;
import org.ssh.pipelines.PipelinePacket;
import org.ssh.services.Service;
import org.ssh.util.Logger;
import org.ssh.util.TaskFutureCallback;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableScheduledFuture;

/**
 * The Class Producer.
 *
 * A Producer generates packets of the type (or subtype of) {@link PipelinePacket}. It does this by
 * running single or scheduled tasks.
 *
 * @param
 *            <P>
 *            A PipelinePacket this Producer can work with.
 *
 * @author Rimon Oz
 */
public abstract class Producer<P extends PipelinePacket<? extends Object>> extends Service<P> {
    
    // a logger for good measure
    private static final Logger     LOG = Logger.getLogger();
                                        
    /** The work function which generates PipelinePackets. */
    private Callable<P>             workerLambda;
                                    
    /** The type of packets made by the Producer. */
    private ProducerType            producerType;
                                    
    /** The pipelines subscribed to this Producer . */
    private final List<Pipeline<P>> registeredPipelines;
                                        
    /**
     * Instantiates a new Producer.
     *
     * @param name
     *            The name of the new Producer.
     * @param producerType
     *            The type of packets made by the Producer
     */
    public Producer(final String name, final ProducerType producerType) {
        super(name);
        // set attributes
        this.producerType = producerType;
        this.registeredPipelines = new ArrayList<Pipeline<P>>();
    }
    
    /**
     * Attach to compatible pipelines.
     * 
     * @param <S>
     *            The generic type of Producer requested by the user.
     * @return The Producer itself.
     */
    public <S extends Producer<P>> S attachToCompatiblePipelines() {
        // find compatible pipelines and attach
        Pipelines.getOfDataType(this.getType()).stream()
            .forEach(pipeline -> this.registerPipeline(pipeline));
        
        return this.<S>getAsService();
    }
    
    /**
     * Gets the work function as a Callable<T>.
     *
     * @return The work function as a Callable<T>
     */
    public Callable<P> getCallable() {
        return this.workerLambda;
    }
    
    /**
     * Gets the work function as a Runnable.
     *
     * @return The work function as a Runnable.
     */
    public Runnable getRunnable() {
        return () -> {
            try {
                Producer.this.getCallable().call();
            }
            catch (final Exception exception) {
                Producer.LOG.exception(exception);
            }
        };
    }
    
    /**
     * Produce a single PipelinePacket with the work function.
     *
     * @param taskName
     *            The name of the task.
     * @return The ListenableFuture representing the result of the work function.
     */
    public ListenableFuture<P> produceOnce(final String taskName) {
        // submit the task to the worker pool in the services store.
        final ListenableFuture<P> producerFuture = Services.submitTask(taskName, this.getCallable());
        // add callbacks to the future that get triggered once the thread is done executing
        FutureCallback<P> taskCallback = new TaskFutureCallback<P>(this.getName());
        
        Futures.addCallback(producerFuture, taskCallback);
        // return the future so the user can use it
        return producerFuture;
    }
    
    /**
     * Produces PipelinePackets on an interval with the supplied length using the work function.
     *
     * @param taskName
     *            The name of the task.
     * @param taskInterval
     *            The length of the interval between task completion and execution.
     * @return A ListenableFuture representing the result of the work function.
     */
    @SuppressWarnings ("unchecked")
    public ListenableFuture<P> produceSchedule(final String taskName, final long taskInterval) {
        // add the task to the scheduled worker pool
        
        Runnable workerLambda = () -> {
            this.produceOnce(this.getName() + "-schedule");
        };
        
        final ListenableScheduledFuture<P> scheduleFuture = (ListenableScheduledFuture<P>) Services
                .scheduleTask(taskName, workerLambda, taskInterval);
                
        FutureCallback<P> scheduleCallback = new TaskFutureCallback<P>(this.getName());
        Futures.addCallback(scheduleFuture, scheduleCallback);
        // return the future so the user can use it
        return scheduleFuture;
    }
    
    /**
     * Registers a {@link Pipeline} with the Producer.
     *
     * @param <C>
     *            The generic type of Producer supplied by the user.
     * @param <S>
     *            The generic type of Pipeline requested by the user.
     * @param pipeline
     *            the pipeline
     * @return The Producer itself.
     */
    @SuppressWarnings ("unchecked")
    public <C extends Producer<P>, S extends Pipeline<?>> C registerPipeline(final S pipeline) {
        Producer.LOG.info("Producer %s registered to Pipeline %s", this.getName(), pipeline.getName());
        this.registeredPipelines.add((Pipeline<P>) pipeline);
        return this.<C>getAsService();
    }
    
    /**
     * Sets the work function.
     *
     * @param <S>
     *            The generic type of Producer requested by the user.
     * @param workFunction
     *            The new work function.
     * @return The Producer itself.
     */
    public <S extends Producer<P>> S setCallable(final Callable<P> workFunction) {
        this.workerLambda = workFunction;
        return this.<S>getAsService();
    }
    
    /**
     * Sets the The type of packets made by the Producer.
     *
     * @param <S>
     *            The generic type of Producer requested by the user.
     * @param producerType
     *            The new type of packets made by the Producer
     * @return The Producer itself.
     */
    public <S extends Producer<P>> S setType(final ProducerType producerType) {
        this.producerType = producerType;
        return this.<S>getAsService();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.ssh.services.Service#start()
     */
    @Override
    public <S extends Service<P>> S start() {
        super.start();
        // check the type of the Producer and start the correct production
        if (this.producerType.equals(ProducerType.SINGLE)) {
            Producer.LOG.fine("Producer %s is starting a single production ...", this.getName());
            // start a single production
            this.produceOnce(this.getName());
        }
        else if (this.producerType.equals(ProducerType.SCHEDULED)) {
            Producer.LOG.fine("Producer %s is starting scheduled production ...", this.getName());
            // start a scheduled production with a default interval (of 1s)
            this.produceSchedule(this.getName(), 1000000);
        }
        return this.<S>getAsService();
    }
}
