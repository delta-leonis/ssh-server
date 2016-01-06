package org.ssh.managers.controllers;

import org.ssh.managers.ManagerController;
import org.ssh.managers.manager.Pipelines;
import org.ssh.pipelines.AbstractPipeline;
import org.ssh.pipelines.AbstractPipelinePacket;

/**
 * The Class PipelineController.
 *
 * PipelineController is responsible for maintaining {@link Pipelines}. It holds
 * references to the Pipelines.
 *
 * @author Rimon Oz
 */
public class PipelineController extends ManagerController<AbstractPipeline<? extends AbstractPipelinePacket<? extends Object>>> {

    

}
