--[[ Communicator init script. ]]--
		
radio = luajava.bindClass("protobuf.Radio")
rotation_speed = 1000

function send(id, velocityR, velocityX, velocityY)
	-- create an example packet that will be processed and send
	packet = radio.RadioProtocolCommand:newBuilder():setRobotId(id)
	packet:setVelocityR(velocityR)
	packet:setVelocityX(velocityX)
	packet:setVelocityY(velocityY)
	-- send a packet with the default sendmethods
	Network:transmit(packet, {})
end


function gotoPosition(robot, x, y, direction)
	while 1 do
		xVel = x - robot:getXPosition()
		yVel = y - robot:getYPosition()
		rVel = (robot:getOrientation() - direction)/math.pi * rotation_speed
		send(robot:getRobotId(), rVel,xVel,yVel)
		sleep(200)
	end
end

function stop()
	for i=0, 12 do
		send(i, 0, 0, 0)
	end
end