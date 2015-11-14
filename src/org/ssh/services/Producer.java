package org.ssh.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.ssh.managers.Services;
import org.ssh.models.enums.ProducerType;
import org.ssh.util.Logger;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableScheduledFuture;

/**
 * The Class Producer.
 *
 * A Producer generates packets of the type (or subtype of) {@link PipelinePacket}. It does
 * this by running single or scheduled tasks.
 *
 * @author Rimon Oz
 * @param <P>
 *            A PipelinePacket this Producer can work with.
 */
public abstract class Producer<P extends PipelinePacket> extends Service<P> {
    
    /** The work function which generates PipelinePackets. */
    private Callable<P>             workerLambda;
                                    
    /** The genericType of the Producer. */
    private ProducerType            producerType;
                                    
    /** The pipeline subscribed to this Producer . */
    private final List<Pipeline<P>> registeredPipelines;
                                    
    // a logger for good measure
    private static final Logger     LOG = Logger.getLogger();
                                        
    /**
     * Instantiates a new Producer.
     *
     * @param name
     *            The name of the new Producer.
     * @param producerType
     *            The genericType of the new Producer
     */
    public Producer(final String name, final ProducerType producerType) {
        super(name);
        // set attributes
        this.producerType = producerType;
        this.registeredPipelines = new ArrayList<Pipeline<P>>();
    }
    
    /**
     * Attach to compatible pipelines.
     */
    @SuppressWarnings ("unchecked")
    public void attachToCompatiblePipelines() {
        // find compatible org.ssh.pipelines
        Services.getPipelines(this.getType()).stream()
            // .parallel()
            .filter(pipeline -> pipeline.getType().equals(this.getType()))
            .map(pipeline -> pipeline.getClass().cast(pipeline))
            .forEach(pipeline -> this.registerPipeline(pipeline));
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
     * @param pipeline the pipeline
     * @return true, if successful.
     */
    public boolean registerPipeline(final Pipeline<P> pipeline) {
        Producer.LOG.info("Producer %s registered to Pipeline %s", this.getName(), pipeline.getName());
        return this.registeredPipelines.add(pipeline);
    }
    
    /**
     * Sets the work function.
     *
     * @param workFunction
     *            The new work function.
     */
    public void setCallable(final Callable<P> workFunction) {
        this.workerLambda = workFunction;
    }
    
    /**
     * Sets the genericType of the Producer.
     *
     * @param producerType
     *            The new genericType of the producer.
     */
    public void setType(final ProducerType producerType) {
        this.producerType = producerType;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.ssh.services.Service#start()
     */
    @Override
    public void start() {
        super.start();
        // check the type of the Producer and start the correct production
        if (this.producerType.equals(ProducerType.SINGLE)) {
            Producer.LOG.fine("Producer %s is starting a single production ...", this.getName());
            // start a single production
            this.produceOnce(this.getName());
        }
        else if (this.producerType.equals(ProducerType.SCHEDULED)) {
            Producer.LOG.fine("Producer %s is starting scheduled production ...", this.getName());
            // start a scheduled production with a default interval
            this.produceSchedule(this.getName(), 1000000);
        }
    }
}
