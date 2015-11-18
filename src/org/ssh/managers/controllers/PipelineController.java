package org.ssh.managers.controllers;

import org.ssh.managers.ManagerController;
import org.ssh.managers.manager.Pipelines;
import org.ssh.pipelines.Pipeline;
import org.ssh.pipelines.PipelinePacket;

/**
 * The Class PipelineController.
 *
 * PipelineController is responsible for maintaining {@link Pipelines}. It holds
 * references to the Pipelines.
 *
 * @author Rimon Oz
 */
public class PipelineController extends ManagerController<Pipeline<? extends PipelinePacket<? extends Object>>> {

    

}
