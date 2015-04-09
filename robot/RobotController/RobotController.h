#ifndef _ROBOT_CONTROL_H_
#define _ROBOT_CONTROL_H_

#include "mbed.h"
#include "MotorController.h"

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

#endif

#define MOTOR_ANGLE_A 160        //70 + 90          Left motor
#define MOTOR_ANGLE_B 20         //280 + 90         Right Motor
#define MOTOR_ANGLE_C 270        //180 + 90         Back motor
#define ROBOT_RADIUS_M ((7.25/100)*(3*PI/180))

class RobotController
{
private:
    MotorController *motor_a, *motor_b, *motor_c;
    int g_goalSpeed;

public:
    RobotController();
    ~RobotController();
    void drive(int direction, int directionSpeed, int rotationSpeed);
    double deg2rad( int deg );
    double deg2rad_restricted(int deg);
    void stop(void);
    MotorController *getMotorControllerA(void);
    MotorController *getMotorControllerB(void);
    MotorController *getMotorControllerC(void);
    int getGoalSpeed(void);
};
#endif