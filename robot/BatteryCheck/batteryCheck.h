/*
Author: Aron Faas

Description:

reads the analog input pin were battery is connected 

*/

#ifndef _BATTERY_CHECKER_H_
#define _BATTERY_CHECKER_H_

// Voltage divider resistors:
#define RESISTOR_1          41 //kilo ohm
#define RESISTOR_2          10 //kilo ohm

#define MAX_MBED_VOLTAGE    3.30 //Volt
#define MAX_PWR_VOLTAGE     ((RESISTOR_1+RESISTOR_2)/RESISTOR_2)*MAX_MBED_VOLTAGE //Volt

#include "mbed.h"


/**
    Battery Checker class.

    Used to check the voltage of the battery
*/
class batteryCheck
{
private:
    AnalogIn battery;

public:

    /**
        Constructor.

        @param batteryPin An analogIn pin, to read the battery voltage.
        @note WARNING the analogIn pin has a maxed rated input voltage of 3.3 volts,
            if the input voltage has to be higher, use a voltage divider
    */
    batteryCheck(PinName batteryPin);

    /**
        Gets the battery voltage

        @return the battery voltage
    */
    float getBatteryVoltage();

protected:


};


#endif