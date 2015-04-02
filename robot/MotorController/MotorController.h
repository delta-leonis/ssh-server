#ifndef _MOTOR_CONTROLLER_H_
#define _MOTOR_CONTROLLER_H_

#include "mbed.h"
#include <string>
#include "PID.h"
#include "sstream"
#include "fstream"

#define Kc 0.5
#define Ti 0.5
#define Td 0.0
#define MC_PID_UPDATE_RATE 0.001

#ifndef WHEEL_DIA
#define WHEEL_DIA
    #define PULSES_PER_WHEEL_ROTATION 32 
    #define WHEEL_CIRCUMFERENCE 22.0 
    #define MM_PER_PULSE (WHEEL_CIRCUMFERENCE/PULSES_PER_WHEEL_ROTATION*10)
    #define PULSES_PER_ROTATION   32.0f
    #define CMS_PER_PULSE (WHEEL_CIRCUMFERENCE/PULSES_PER_ROTATION)
    #define M_PER_PULSE (CMS_PER_PULSE/100)
    #define PI 3.14159265358979323846
    #define ROBOT_RADIUS 7.25                     //  Robot Radius in cm
    #define ROBOT_CIRCUMFERENCE (2*PI*ROBOT_RADIUS) //  Circumference of the Robot
    #define LOG_SIZE 1024
    #define SAMPLING_SIZE 75      // Amount of data we average for the tacho speed m/s
#endif

class MotorController {
    private:
    string g_name;
    DigitalOut _enablePin, _brakePin;
    InterruptIn _tachoPin;
    DigitalIn _hallPin;
    PwmOut _speedPin;
    bool g_pidControlled;
    float g_goalSpeed, g_deltaTime, g_currentSpeed, g_pidSpeed, g_rotationSpeed;
    int g_hallData, g_rotation;
    Ticker g_pidTicker;
    Timer g_speedTimer;     //Used to determine the difference in time from the last interrupt
    PID g_speedPID;
    
    //methods
    void init(void);
    void initPid();
    void drivePercent(float goalSpeed,  int rotationSpeed);
    void drivePid( float goalSpeed, int rotationSpeed);
    void enableBrake(void);
    void disableBrake(void);
    void stop(void);
    void setSpeedInPercent( float percent);
    void tachoInterrupt(void);
    void updatePid(void);

    public:
    MotorController(string name, PinName enablePin, PinName brakePin, PinName speedPin, PinName tachoPin, PinName hallPin, bool pidControlled);
    ~MotorController(void);
    void drive( float goalSpeed, int rotationSpeed);
    void attach(void);
    void detach(void);
    float getCurrentSpeed();
    float getPidSpeed();
    float getGoalSpeed();
    void fillLogData(void);
};
#endif