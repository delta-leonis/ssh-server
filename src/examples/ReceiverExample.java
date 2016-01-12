package examples;

import org.ssh.managers.manager.Models;
import org.ssh.managers.manager.Network;
import org.ssh.managers.manager.Services;
import org.ssh.network.receive.detection.DetectionPipeline;
import org.ssh.network.receive.geometry.GeometryPipeline;
import org.ssh.network.receive.geometry.consumers.GeometryModelConsumer;
import org.ssh.network.receive.wrapper.WrapperPipeline;
import org.ssh.network.receive.wrapper.consumers.WrapperConsumer;
import org.ssh.pipelines.consumers.ProtoConsumer;
import org.ssh.pipelines.packets.DetectionPacket;
import org.ssh.pipelines.packets.GeometryPacket;
import org.ssh.pipelines.packets.WrapperPacket;

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
        new GeometryModelConsumer("consumer geometry").attachToCompatiblePipelines();

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
