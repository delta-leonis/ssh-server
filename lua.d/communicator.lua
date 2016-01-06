--[[ Prototype for the communicator init script.
	 Will be finished after Jeroen is done updating the Communicator class ]]--
--[[
-- Communicator "alias" 
Pipelines = luajava.bindClass("org.ssh.managers.manager.Pipelines")
radioConsumer = luajava.newInstance("org.ssh.services.consumers.RadioPacketConsumer")
radioConsumer:register(SendMethod.UDP, luajava.newInstance("org.ssh.network.transmit.senders.UDPSender", "192.168.1.10", 1337))
radioConsumer:addDefault(SendMethod.DEBUG) -- at this moment both UDP and DEBUG are default

Services:add(luajava.newInstance("org.ssh.services.producers.Communicator"))
-- add a consumer voor radiopackets
Pipelines:get("communication pipeline"):get():registerConsumer(radioConsumer);

Pipelines:get("communication pipeline"):get():registerCoupler(luajava.newInstance("org.ssh.network.transmit.radio.couplers.RoundCoupler"))
		
function send(id, velocityR, velocityX, velocityY)
	Radio = luajava.bindClass("protobuf.Radio")
	-- create an example packet that will be processed and send
	packet = Radio.RadioProtocolCommand:newBuilder():setRobotId(id):setVelocityR(velocityR):setVelocityX(velocityX):setVelocityY(velocityY)
	-- retrieve the communicator from services
	comm = Services:get("communicator"):get()
	-- send a packet with the default sendmethods
	comm:send(packet)
	-- send a packet with specifeid sendmethods
	comm:send(packet, SendMethod.BLUETOOTH, SendMethod.DEBUG)
end 
]]--