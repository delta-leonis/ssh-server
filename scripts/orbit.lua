--[[ This is an example script ]]--
while true do
	for i = 0,2*math.pi, 0.01 do 
		robotB[0]:update({"position", newPoint(math.sin(i)*500, math.cos(i)*500)})
		robotB[1]:update({"position", newPoint(math.sin(-i-1)*1000, math.cos(-i-1)*1000)})
		robotB[2]:update({"position", newPoint(math.sin(i-2)*1500, math.cos(i-2)*1500)})
		robotB[3]:update({"position", newPoint(math.sin(-i-3)*2000, math.cos(-i-3)*2000)})
		sleep(3)
	end
end