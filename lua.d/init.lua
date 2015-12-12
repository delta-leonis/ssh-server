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
function robotA(integer)
   robot = Models:get("robot A" .. tonumber(integer))
   if robot:isPresent() then
       return robot:get()
   else
      print("robot A" .. tonumber(integer) .. " not present")
       return nil
   end
end

function robotO(integer)
   robot = Models:get("robot O" .. tonumber(integer))
   if robot:isPresent() then
       return robot:get()
   else
       print("robot O" .. tonumber(integer) .. " not present")
       return nil
   end
end

-- Create "alias" for ball
function getBall()
  ball = Models:get("ball")
  if ball:isPresent() then
      ball = ball:get()
  end
  return nil
end
