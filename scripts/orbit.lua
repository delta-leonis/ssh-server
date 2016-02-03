--[[ This is an example script ]]--
-- Initialize variables to minimize CPU cost
robotA0 = robotA(0)
robotA1 = robotA(1)
robotA2 = robotA(2)
robotA3 = robotA(3)
-- While loop that makes the robots spin around in orbit
while true do
	for i = 0,2*math.pi, 0.01 do
		robotA0:update({"xPosition", math.sin(i)*500, "yPosition", math.cos(i)*1000})
		robotA1:update({"xPosition", math.sin(-i-1)*1500, "yPosition", math.cos(-i-1)*1000})
		robotA2:update({"xPosition", math.sin(i-2)*2000, "yPosition", math.cos(i-2)*1500})
		robotA3:update({"xPosition", math.sin(-i-3)*2000, "yPosition", math.cos(-i-3)*2500})
		sleep(10)
	end
end
