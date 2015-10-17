package services;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Function;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableScheduledFuture;

import application.Services;
import pipeline.PipelinePacket;

/**
 * The Class Producer.
 * 
 * @author Rimon Oz
 */
abstract public class Producer<T extends PipelinePacket> extends Service<T> {

    /** The callable. */
    private Callable<T> callable;

    /**
     * Instantiates a new producer.
     *
     * @param name the name
     */
    public Producer(String name) {
        super(name);
    }

    /**
     * Gets the callable.
     *
     * @return the callable
     */
    public Callable<T> getCallable() {
        return this.callable;
    }
    
    public Runnable getRunnable() {
    	
    	return new Runnable() {
				@Override
				public void run() {
	    			try {
						getCallable().call();
					} catch (Exception e) {
						// TODO handle error
						e.printStackTrace();
					}
				}
		};
	}

    /**
     * Sets the callable.
     *
     * @param runnable the new callable
     */
    public void setCallable(Callable<T> runnable) {
        this.callable = runnable;
    }
    
    
    public ListenableFuture<T> produceOnce() {
    	ListenableFuture<T> producerFuture = Services.submitTask(this.getCallable());
    	Futures.addCallback(producerFuture, new FutureCallback<T>() {

			@Override
			public void onFailure(Throwable failPacket) {
				// log error
			}

			@Override
			public void onSuccess(T successPacket) {
				// find pipelines that can process this packet
				Services.getPipelines(successPacket.getClass()).stream()
				// TODO: make sure producer is registered with pipeline
				// start them up
						.forEach(pipeline -> pipeline.processPacket());
			}
    	});
    	return producerFuture;
    }
    
    public ListenableFuture<T> produceNTimes(int n) {
    	ListenableScheduledFuture<T> producerFuture = Services.scheduleTask("produceSchedule", this.getRunnable(), 1000000);
    	Futures.addCallback(producerFuture, new FutureCallback<T>() {

			@Override
			public void onFailure(Throwable failPacket) {
				// log error
			}

			@Override
			public void onSuccess(T successPacket) {
				// find pipelines that can process this packet
				Services.getPipelines(successPacket.getClass()).stream()
				// TODO: make sure producer is registered with pipeline
				// start them up
						.forEach(pipeline -> pipeline.processPacket());
			}
    	});
    	return producerFuture;
    }
    
    
}
