package org.ssh.pipelines.packets;

import org.ssh.pipelines.PipelinePacket;

import com.google.protobuf.MessageOrBuilder;

import protobuf.RefereeOuterClass;
import protobuf.RefereeOuterClass.RefereeOrBuilder;

/**
 * The GeometryPacket class.
 *
 * @author Rimon Oz
 */
public class RefereePacket extends PipelinePacket<RefereeOuterClass> {
    
    /** The data. */
    private RefereeOrBuilder data;

}
