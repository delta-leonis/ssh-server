/*
Author: Aron Faas

Description:

reads the analog input pin were battery is connected 

*/

#include "batteryCheck.h"

batteryCheck::batteryCheck(PinName batteryPin) : battery(batteryPin)
{

}

batteryCheck::~batteryCheck()
{

}

float batteryCheck::getBatteryVoltage()
{
    return battery.read() * MAX_PWR_VOLTAGE;
}