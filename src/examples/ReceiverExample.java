package examples;

import org.ssh.managers.manager.Models;
import org.ssh.managers.manager.Pipelines;
import org.ssh.managers.manager.Services;
import org.ssh.pipelines.packets.DetectionPacket;
import org.ssh.pipelines.packets.GeometryPacket;
import org.ssh.pipelines.packets.WrapperPacket;
import org.ssh.pipelines.pipeline.DetectionPipeline;
import org.ssh.pipelines.pipeline.GeometryPipeline;
import org.ssh.pipelines.pipeline.WrapperPipeline;
import org.ssh.services.consumers.ProtoConsumer;
import org.ssh.services.consumers.WrapperConsumer;
import org.ssh.services.producers.UDPReceiver;

public class ReceiverExample {
    
    public ReceiverExample() {
        // make models available
        Models.start();
        // make services available
        Services.start();
        WrapperPipeline pipa = new WrapperPipeline("wrapper pipeline");
        DetectionPipeline detecPipa = new DetectionPipeline("detection pipeline");
        GeometryPipeline geoPipa = new GeometryPipeline("geometry pipeline");
        Pipelines.add(pipa);
        Pipelines.add(geoPipa);
        Pipelines.add(detecPipa);

        WrapperConsumer splitter = new WrapperConsumer();
        ProtoConsumer<GeometryPacket> spuger = new ProtoConsumer<GeometryPacket>("spuger", GeometryPacket.class);
        ProtoConsumer<DetectionPacket> spuger2 = new ProtoConsumer<DetectionPacket>("spuger", DetectionPacket.class);
        spuger.attachToCompatiblePipelines();
        spuger2.attachToCompatiblePipelines();

        UDPReceiver refereeReceiver = new UDPReceiver(WrapperPacket.class);
        // Services.addService(receiver);
        
    }
    
    public static void main(final String[] args) {
        new ReceiverExample();
    }
}
