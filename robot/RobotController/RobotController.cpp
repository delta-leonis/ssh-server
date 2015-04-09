#include "RobotController.h"

RobotController::RobotController(){
    // Setup motorcontrollers
    //                             name,   enable,   brake,  throttle,    tacho,  hallPin,   PID enabled
    motor_a = new MotorController( "A",      p10,     p11,     p21,        p14,     p15,       true);
    motor_b = new MotorController( "B",      p10,     p12,     p22,        p29,     p16,       true);
    motor_c = new MotorController( "C",      p10,     p13,     p23,        p30,     p17,       true);
    g_goalSpeed = 0;
}
//_________________________________________________________________________________________________________________________
// Function that controls the movement of the robot.
// direction : Received from packet. The direction the robot needs to move relative to itself. Think of strafing in videogames.
// directionSpeed : Received from packet. The velocity in mm/s we want to drive.
// rotationSpeed : Received from packet. The speed at which we rotate in mm/s
void RobotController::drive(int direction, int directionSpeed, int rotationSpeed){
    g_goalSpeed = directionSpeed;
    //ATTENTION: THIS WILL PROBABLY GET CHANGED, ONCE THE GoToPosition.java GETS DEBUGGED. We shouldn't have to add 180 to the direction.
    //add 180° to convert angle to unit circle
    //Example: direction 90° -> normally means right, but on the unit circle this discribes left. Add 180° to invert
    direction += 180;
    //mm/s -> m/s
    float speed = (float)directionSpeed / 1000;
    
    // speed * cos( Motor Angle - direction) = Speed a wheel needs to drive at to drive towards a certain direction
    // This part basically does the calculations needs to make the robot move sideways (or forwards..  .. Or backwards)
    // Note: The values are multiplied by 20 to turn it into a value between 0 - 100, which is used for the PID.
    double speedA = 20*speed*cos(deg2rad(MOTOR_ANGLE_A - direction));
    double speedB = 20*speed*cos(deg2rad(MOTOR_ANGLE_B - direction));                                                                                
    double speedC = 20*speed*cos(deg2rad(MOTOR_ANGLE_C - direction));
    
    #ifdef DEBUG
        printf("Motor A speed: %f%%  ",speedA);
        printf("Motor B speed: %f%%  ",speedB);
        printf("Motor C speed: %f%%\n",speedC);
    #endif

    //send data to motorcontrollers
    motor_a->drive(speedA, rotationSpeed);
    motor_b->drive(speedB, rotationSpeed);
    motor_c->drive(speedC, rotationSpeed);
}
// Stops all motors
void RobotController::stop(){
    DigitalOut _enable = PinName(p10);
    _enable = 0;
    //detach the interrupts
    motor_a->detach();
    motor_b->detach();
    motor_c->detach();
}
// Converts degrees to radians, making sure the value is between 0 : 2PI
double RobotController::deg2rad_restricted(int deg){                    
    //values restricted from 0 to 360
    deg =   deg%360;                                            
    //Example: -100 mod 360 = -100, but we want to have 260  ; 400 mod 360 = 40 is okay
    if( deg<0 ){
        deg =   360+deg;
    }
    return deg2rad(deg);
}
// Converts degrees to radians
double RobotController::deg2rad( int deg ){
    return deg*PI/180;
}
// Returns MotorController A, which is the motor left of the robot.
MotorController *RobotController::getMotorControllerA()
{
    return motor_a;
}
// Returns MotorController B, which is the motor right of the robot.
MotorController *RobotController::getMotorControllerB()
{
    return motor_b;
}
// Returns MotorController C, which is the motor in the back of the robot.
MotorController *RobotController::getMotorControllerC()
{
    return motor_c;
}
// Returns the speed given by the server application
// Mostly for debugging
int RobotController::getGoalSpeed(){
    return g_goalSpeed;
}