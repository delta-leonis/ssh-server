package examples;

import org.ssh.managers.manager.Models;
import org.ssh.managers.manager.Network;
import org.ssh.managers.manager.Services;
import org.ssh.pipelines.packets.DetectionPacket;
import org.ssh.pipelines.packets.GeometryPacket;
import org.ssh.pipelines.packets.WrapperPacket;
import org.ssh.pipelines.pipeline.DetectionPipeline;
import org.ssh.pipelines.pipeline.GeometryPipeline;
import org.ssh.pipelines.pipeline.WrapperPipeline;
import org.ssh.services.consumers.GeometryConsumer;
import org.ssh.services.consumers.ProtoConsumer;
import org.ssh.services.consumers.WrapperConsumer;

public class ReceiverExample {
    
    public ReceiverExample() {
        // make models available
        Models.start();
        // make services available
        Services.start();
        //make network available
        Network.start();
        
        //Create all pipa's
        new WrapperPipeline("wrapper pipeline");
        new DetectionPipeline("detection pipeline");
        new GeometryPipeline("geometry pipeline");
        
        // create splitter (from wrapper to (detection|geometry) )
        new WrapperConsumer();
        // create consumer to update Field model
        new GeometryConsumer("consumer geometry").attachToCompatiblePipelines();

        // verbose print both packets
        new ProtoConsumer("spuger", GeometryPacket.class).attachToCompatiblePipelines();
        new ProtoConsumer("spuger", DetectionPacket.class).attachToCompatiblePipelines();
        
        //listen for wrapperpackets
        Network.listenFor(WrapperPacket.class);
        
    }
    
    public static void main(final String[] args) {
        new ReceiverExample();
    }
}
