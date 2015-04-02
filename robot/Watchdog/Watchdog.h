#include "mbed.h"

// Class copied from the internet.
// Makes sure that the robot doesn't get stuck in an infinite loop.
// Like: If the function takes to long to execute -> reset.
class Watchdog {
public:
// Load timeout value in watchdog timer and enable
    void kick(float s);
// "kick" or "feed" the dog - reset the watchdog timer
// by writing this required bit pattern
    void kick(void);
};