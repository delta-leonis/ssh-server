-- Initialisation script
-- This script initialises all useful variables for further use

--[[ Import system ]]-- 
system = luajava.bindClass('java.lang.System')
--[[ function newPoint ]]--
function newPoint(x, y)
    return luajava.newInstance("javafx.geometry.Point2D", x, y)
end

--[[ sleep function ]]-- 
function sleep(n)
	luajava.bindClass("java.lang.Thread"):currentThread():sleep(n)
end

-- Create "aliases" for all robots. Robot B0 is robotB[0]
robotB = {}
for i=0, 12 do
    robot = Models:get("robot B" .. tonumber(i))
    if robot:isPresent() then 
        robot = robot:get()
	else
		robot = nil
    end
	robotB[i] = robot
end

robotY = {}
for i=0, 12 do
    robot = Models:get("robot Y" .. tonumber(i))
    if robot:isPresent() then 
        robot = robot:get()
	else
		robot = nil
    end
	robotY[i] = robot
end

-- Create "alias" for ball
ball = Models:get("ball")
if ball:isPresent() then
    ball = ball:get()
end
