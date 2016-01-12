package org.ssh.managers.controllers;

import org.ssh.managers.AbstractManagerController;
import org.ssh.managers.manager.Pipelines;
import org.ssh.pipelines.AbstractPipeline;
import org.ssh.pipelines.AbstractPipelinePacket;

/**
 * The Class PipelineController.
 * <p>
 * PipelineController is responsible for maintaining {@link Pipelines}. It holds
 * references to the Pipelines.
 *
 * @author Rimon Oz
 */
public class PipelineController extends AbstractManagerController<AbstractPipeline<? extends AbstractPipelinePacket<? extends Object>>> {


}
