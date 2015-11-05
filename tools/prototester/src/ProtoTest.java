import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

import org.ssh.senders.UDPCommunicator;
import protobuf.Radio.RadioProtocolCommand;
import protobuf.Radio.RadioProtocolWrapper;

public class ProtoTest {
	private static String	DEFAULT_IP				= "192.168.1.10";
	private static int 		DEFAULT_COMMAND_COUNT 	= 3;
	private static int 		DEFAULT_WRAPPER_COUNT 	= 2;
	private static int 		DEFAULT_TIMEOUT	 		= 0;
	private static int 		DEFAULT_PORT	 		= 2002; 
	private static boolean	DEFAULT_VERBOSE	 		= false; 
	
	
	public static void main(String[] args) throws Exception {
		HashMap<String, String> commands = parseArguments(args);
		
		if(commands.containsKey("?") || commands.size() == 0)
		{
			System.out.println("Usage:");
			System.out.printf("-i	defines ip (default: %s)\n", DEFAULT_IP);
			System.out.printf("-p	defines port (default: %d)\n", DEFAULT_PORT);
			System.out.printf("-c	defines number of commands per wrapper(default: %d)\n", DEFAULT_COMMAND_COUNT);
			System.out.printf("-w	defines number of wrappers (default: %d)\n", DEFAULT_WRAPPER_COUNT );
			System.out.printf("-t	defines timout (default: %d)\n", DEFAULT_TIMEOUT );
			System.out.printf("-v	verbose org.ssh.senders (default: %s)\n", DEFAULT_VERBOSE ? "true" : "false" );
			System.exit(0);
		}
		
		try {
			//get settings from arguments, otherwise use DEFAULT_* settings
			InetAddress ip	  = InetAddress.getByName(commands.containsKey("i") ? commands.get("i") : DEFAULT_IP);
			int command_count = commands.containsKey("c") ? Integer.parseInt(commands.get("c")) : DEFAULT_COMMAND_COUNT;
			int wrapper_count = commands.containsKey("w") ? Integer.parseInt(commands.get("w")) : DEFAULT_WRAPPER_COUNT;
			int port		  = commands.containsKey("p") ? Integer.parseInt(commands.get("p")) : DEFAULT_PORT;
			int timeout		  = commands.containsKey("t") ? Integer.parseInt(commands.get("t")) : DEFAULT_TIMEOUT;
			boolean verbose	  = commands.containsKey("v") ? commands.get("v").equalsIgnoreCase("true") : DEFAULT_VERBOSE;
	
			//initialize a communicator with received settings
			UDPCommunicator Communicator = new UDPCommunicator(ip, port);
			
			//fill arraylist with wrappers
			ArrayList<RadioProtocolWrapper.Builder> wrappers = new ArrayList<RadioProtocolWrapper.Builder>();
			for(int wrapperI = 0; wrapperI < wrapper_count; wrapperI++){

				RadioProtocolWrapper.Builder wrapper = RadioProtocolWrapper.newBuilder();
				
				//fill command protobuf with random data
				for(int commandI = 0; commandI < command_count; commandI++){
					//add required data
					RadioProtocolCommand.Builder command = RadioProtocolCommand.newBuilder().setRobotId(commandI)
							 .setVelocityX((float) Math.random())
							 .setVelocityY((float) Math.random())
							 .setVelocityR((float) Math.random());

					if(verbose)
						System.out.printf("Command: #%d\nVelocityX: %f\nVelocityY: %f\nVelocityR: %f\n", command.getRobotId()
								   , command.getVelocityX()
								   , command.getVelocityY()
								   , command.getVelocityR());
						
					//optional data
					if(Math.random() > 0.5){
						 command.setFlatKick((float) (Math.random() * 4.0))
						 .setChipKick((float) (Math.random() * 4.0));
						if(verbose)
							System.out.printf("FlatKick: %f\nChipKick: %f\n", command.getFlatKick(), command.getChipKick());
					}
					//optional data
					if(Math.random() > 0.5){
						 command.setDribblerSpin((float) Math.random());
						if(verbose)
							System.out.printf("dribblerSpin: %f\n", command.getDribblerSpin());
					}
					//optional data
					if(Math.random() > 0.5){
						 command.setDistance((int) (Math.random() * 40));
						if(verbose)
							System.out.printf("distance: %d\n", command.getDistance());
					}
					
					wrapper.addCommand(command);
				}
			wrappers.add(wrapper);
		}
			
		wrappers.stream().forEach(command -> Communicator.send(command.build(), timeout));

		} catch (SocketException uhe) {
			System.err.printf("Socket already in use.\n");
		} catch (UnknownHostException uhe) {
			System.err.printf("Unknown host exception.\n");
		}
		System.exit(0);
	}

	/**
	 * Parse given string[] to HashMap with accumulators and thier arguments,<br>
	 * accumulators are defined with the starting char '-'.<br>
	 * parses "value1 value2 -key value3 value4" to<br> 
	 * value1 => null <br> 
	 * value2 => null <br> 
	 * key => value3 <br> 
	 * value4 => null <br>
	 * 
	 * @param args	the string[] with
	 * @return	parsed commands
	 */
	private static HashMap<String, String> parseArguments(String[] args) {
		//parse command line options
		HashMap<String, String> opts = new HashMap<String, String>();
		Stream.of(args).reduce(null, (accumulator, value) ->
			//Check if the value should be a accumulator (e.a. it has arguments)
			value.startsWith("-") 
				? value.substring(1) //return the value to the accumulator var
				: (accumulator == null  //value doesn't have arguments
					? opts.put(value, null)
					//put accumulator and value in opts
					: opts.put(accumulator, value)
				  )
		);
		return opts;
	}

}
