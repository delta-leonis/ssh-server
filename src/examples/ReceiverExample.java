package examples;

import org.ssh.managers.Models;
import org.ssh.managers.Services;
import org.ssh.pipelines.RadioPipeline;

import input.UDPReceiver;
import org.ssh.services.pipeline.packets.RadioPacket;

public class ReceiverExample {

	public static void main(String[] args) {
		// make models available
		Models.start();
		// make services available
		Services.start();
		RadioPipeline pipa = new org.ssh.pipelines.RadioPipeline("communication pipeline");
		Services.addPipeline(pipa);
		
		UDPReceiver<RadioPacket> lolwutfaggotnigger = new UDPReceiver<RadioPacket>("jemoeder", 31337);
		Services.addService(lolwutfaggotnigger);

	}

}
