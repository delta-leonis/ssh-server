package org.ssh.services;

import org.ssh.managers.Services;
import org.ssh.pipelines.Pipeline;
import org.ssh.pipelines.PipelinePacket;
import org.ssh.util.Logger;

import com.google.common.util.concurrent.FutureCallback;

/**
 * The Class TaskFutureCallback.
 * 
 * This class gets attached to a task once its submitted to the Services store. This class can be
 * used to cancel currently running tasks and holds completion handlers that automatically add
 * generated PipelinePackets to the appropriate Pipeline.
 *
 * @param
 *            <P>
 *            the generic type of PipelinePacket handled by the FutureCallback
 *            
 * @author Rimon Oz
 */
public class TaskFutureCallback<P extends PipelinePacket> implements FutureCallback<P> {
    
    /** The name of the task. */
    private String              name;
                                
    // a logger for good measure
    private static final Logger LOG = Logger.getLogger();
                                    
    /**
     * Instantiates a new callback handler for a task.
     *
     * @param name
     *            the name
     */
    public TaskFutureCallback(String name) {
        this.setName(name);
    }
    
    /**
     * Task failed to execute or threw an error!.
     *
     * @param failPacket
     *            the fail packet
     */
    @Override
    public void onFailure(final Throwable failPacket) {
        // log error
        TaskFutureCallback.LOG.exception((Exception) failPacket);
    }
    
    /**
     * Task successfully completed!.
     *
     * @param successPacket
     *            the success packet
     */
    @SuppressWarnings ("unchecked")
    @Override
    public void onSuccess(final P successPacket) {
        TaskFutureCallback.LOG.fine("Task completed by %s", this.getName());
        Services.getPipelines(successPacket.getClass()).stream()
                // start them up
                .map(pipeline -> (Pipeline<P>) pipeline)
                // process the packet
                .forEach(pipeline -> pipeline.addPacket(successPacket).processPacket());
    }
    
    /**
     * Gets the name of the task.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of the task.
     *
     * @param name
     *            the new name
     */
    public void setName(String name) {
        this.name = name;
    }
};
