package org.ssh.services.pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.ssh.managers.Services;
import org.ssh.models.enums.ProducerType;
import org.ssh.services.Pipeline;
import org.ssh.services.Service;

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
 * @author Rimon Oz
 * @param <T>
 *            A PipelinePacket this Producer can work with.
 */
abstract public class Producer<T extends PipelinePacket> extends Service<T> {
    
    /** The work function which generates PipelinePackets. */
    private Callable<T>             workerLambda;
                                    
    /** The type of the Producer. */
    private ProducerType            producerType;
                                    
    /** The org.ssh.pipelines subscribed to this Producer . */
    private final List<Pipeline<T>> registeredPipelines;
                                    
    /**
     * Instantiates a new Producer.
     *
     * @param name
     *            The name of the new Producer.
     * @param producerType
     *            The type of the new Producer
     */
    public Producer(final String name, final ProducerType producerType) {
        super(name);
        // set attributes
        this.producerType = producerType;
        this.registeredPipelines = new ArrayList<Pipeline<T>>();
    }
    
    /**
     * Attach to compatible org.ssh.pipelines.
     */
    @SuppressWarnings ("unchecked")
    public void attachToCompatiblePipelines() {
        // find compatible org.ssh.pipelines
        Services.getPipelines(this.type.getType()).stream()
                // .parallel()
                .filter(pipeline -> pipeline.type.equals(this.type)).map(pipeline -> pipeline.getClass().cast(pipeline))
                .forEach(pipeline -> this.registerPipeline(pipeline));
    }
    
    /**
     * Gets the work function as a Callable<T>.
     *
     * @return The work function as a Callable<T>
     */
    public Callable<T> getCallable() {
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
            catch (final Exception e) {
                // TODO handle error
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
    public ListenableFuture<T> produceOnce(final String taskName) {
        // submit the task to the worker pool in the org.ssh.services store.
        final ListenableFuture<T> producerFuture = Services.submitTask(taskName, this.getCallable());
        // add callbacks to the future that get triggered once the thread is done executing
        Futures.addCallback(producerFuture, new FutureCallback<T>() {
            
            /**
             * Task failed to execute or threw an error!
             */
            @Override
            public void onFailure(final Throwable failPacket) {
                // log error
                System.out.println(failPacket.toString());
            }
            
            /**
             * Task successfully completed!
             */
            @SuppressWarnings ("unchecked")
            @Override
            public void onSuccess(final T successPacket) {
                Service.logger.info("Task named %s completed by %s", taskName, Producer.this.getName());

                // TODO: make sure producer is registered with org.ssh.services.pipeline
                // find org.ssh.pipelines that can process this packet
                Services.getPipelines(successPacket.getClass()).stream()
                        // start them up
                        .map(pipeline -> (Pipeline<T>) pipeline)
                        // process the packet!
                        .forEach(pipeline -> pipeline.addPacket(successPacket).processPacket());
            }
        });
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
    public ListenableFuture<T> produceSchedule(final String taskName, final long taskInterval) {
        // add the task to the scheduled worker pool
        final ListenableScheduledFuture<T> producerFuture = Services.scheduleTask(taskName,
                this.getRunnable(),
                taskInterval);
        Futures.addCallback(producerFuture, new FutureCallback<T>() {
            
            /**
             * Task failed to execute or threw an error!
             */
            @Override
            public void onFailure(final Throwable failPacket) {
                // log error
                System.out.println(failPacket.toString());
            }

            /**
             * Task successfully completed!
             */
            @SuppressWarnings ("unchecked")
            @Override
            public void onSuccess(final T successPacket) {
                Service.logger.info("Task completed by %s", Producer.this.getName());

                // TODO: make sure producer is registered with org.ssh.services.pipeline
                // find org.ssh.pipelines that can process this packet
                Services.getPipelines(successPacket.getClass()).stream()
                        // start them up
                        .map(pipeline -> (Pipeline<T>) pipeline)
                        // process the packet
                        .forEach(pipeline -> pipeline.addPacket(successPacket).processPacket());
            }
        });
        // return the future so the user can use it
        return producerFuture;
    }
    
    /**
     * Registers a {@link org.ssh.services.Pipeline} with the Producer.
     *
     * @param org.ssh.services.pipeline
     *            The org.ssh.services.pipeline to be registered.
     * @return true, if successful.
     */
    public boolean registerPipeline(final Pipeline<T> pipeline) {
        Service.logger.info("Producer %s registered to Pipeline %s", this.getName(), pipeline.getName());
        return this.registeredPipelines.add(pipeline);
    }
    
    /**
     * Sets the work function.
     *
     * @param workFunction
     *            The new work function.
     */
    public void setCallable(final Callable<T> workFunction) {
        this.workerLambda = workFunction;
    }
    
    /**
     * Sets the type of the Producer.
     *
     * @param producerType
     *            The new type of the producer.
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
            Service.logger.info("Producer %s is starting a single production ...", this.getName());
            // start a single production
            this.produceOnce(this.getName());
        }
        else if (this.producerType.equals(ProducerType.SCHEDULED)) {
            Service.logger.info("Producer %s is starting scheduled production ...", this.getName());
            // start a scheduled production with a default interval
            this.produceSchedule(this.getName(), 1000000);
        }
    }
}
