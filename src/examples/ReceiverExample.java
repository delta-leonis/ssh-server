package examples;

import java.lang.reflect.Type;

import org.ssh.managers.manager.Models;
import org.ssh.managers.manager.Network;
import org.ssh.managers.manager.Pipelines;
import org.ssh.managers.manager.Services;
import org.ssh.pipelines.packets.DetectionPacket;
import org.ssh.pipelines.packets.GeometryPacket;
import org.ssh.pipelines.packets.RefereePacket;
import org.ssh.pipelines.packets.WrapperPacket;
import org.ssh.pipelines.pipeline.DetectionPipeline;
import org.ssh.pipelines.pipeline.GeometryPipeline;
import org.ssh.pipelines.pipeline.WrapperPipeline;
import org.ssh.services.consumers.GeometryConsumer;
import org.ssh.services.consumers.ProtoConsumer;
import org.ssh.services.consumers.WrapperConsumer;
import org.ssh.services.producers.UDPReceiver;

import com.google.common.reflect.TypeToken;

public class ReceiverExample {
    
    public ReceiverExample() {
        // make models available
        Models.start();
        // make services available
        Services.start();
        Network.start();
        
        new WrapperPipeline("wrapper pipeline");
        new DetectionPipeline("detection pipeline");
        new GeometryPipeline("geometry pipeline");


        new WrapperConsumer();
        new GeometryConsumer("consumer geometry").attachToCompatiblePipelines();
        new ProtoConsumer("spuger", GeometryPacket.class).attachToCompatiblePipelines();
        new ProtoConsumer("spuger", DetectionPacket.class).attachToCompatiblePipelines();

        Network.listenFor(WrapperPacket.class);
        // Services.addService(receiver);
        
    }
    
    public static void main(final String[] args) {
        new ReceiverExample();
    }
}
