#include "MotorController.h"

MotorController::MotorController(string name, PinName enablePin, PinName brakePin, PinName speedPin, PinName tachoPin, PinName hallPin, bool pidControlled)
    :g_name(name), _enablePin(enablePin), _brakePin(brakePin), _tachoPin(tachoPin), _hallPin(hallPin), _speedPin(speedPin), g_pidControlled(pidControlled), g_speedPID(Kc,Ti,Td,MC_PID_UPDATE_RATE)
{
    enableBrake();
    init();
}

void MotorController::init()
{
    g_currentSpeed = 0;
    _speedPin.period_us(40);
    //start speedtimer
    g_speedTimer.start();

    // Set rising edge interrupt to fire tachoInterrupt method
    _tachoPin.rise(this, &MotorController::tachoInterrupt);
    //if PID enabled init its values
    if(g_pidControlled)initPid();

    _enablePin = 1;
    
    stop();
}
//init parameters for the PID
void MotorController::initPid()
{
    g_speedPID.setBias(1.0);
    g_speedPID.setInputLimits(-100, 100); //input is from -100 to 100 // from -5 reverse m/s to 5 m/s
    g_speedPID.setOutputLimits(-100.0, 100.0); // from -100% to 100%
    g_speedPID.setMode(AUTO_MODE);
    g_speedPID.setSetPoint(0); // start with 0 m/s
}

// Sets the speed using the given goalSpeed and rotationSpeed as a percentage between -100% and 100%
void MotorController::drivePercent(float goalSpeed, int rotationSpeed)
{
    detach();
    disableBrake();
    //SET ROTATION
    goalSpeed += (float)rotationSpeed;  // Afstand per grad * (grad per s)/ (100/20)
    //if goalspeed == 0 -> do nothin in drive()
    if(goalSpeed != 0) {
        setSpeedInPercent(goalSpeed);
    } else {
        //stop
        setSpeedInPercent(0);
        enableBrake();
    }
}

// Attaches the PID interrupt. 
// If this isn't on, you can't drive in PID mode.
// See updatePid()
void MotorController::attach()
{
    g_pidTicker.attach(this, &MotorController::updatePid, MC_PID_UPDATE_RATE);
}

// Detaches the PID interrupt. 
// If this isn't on, you can't drive in PID mode.
// See updatePid()
void MotorController::detach()
{
    g_pidTicker.detach();
}

// Function that controls the movement of a wheel.
// goalSpeed :      The speed you want this motor to drive at in m/s
// rotationSpeed :  How much the wheel needs to turn in m/s. 
//                  This value is barely PID controlled, in order to get more accurate turns.
void MotorController::drive(float goalSpeed, int rotationSpeed)
{
    if(goalSpeed == 0){
        g_currentSpeed = 0;
    }
    
    //if drive with pid
    if(g_pidControlled) {
        drivePid(goalSpeed, rotationSpeed);
    }
    //for debug without PID
    else {
        drivePercent(goalSpeed, rotationSpeed);
    }
}

// Function that sets the variables to drive in PID mode.
// After which it makes sure the updatePID() function keeps getting called.
// The updatePID() function will make the 
// goalSpeed :      The speed you want this motor to drive at in m/s
// rotationSpeed :  How much the wheel needs to turn in m/s. 
//                  This value is barely PID controlled, in order to get more accurate turns.
void MotorController::drivePid( float goalSpeed, int rotationSpeed)
{
    disableBrake();
    //INIT
    g_goalSpeed = goalSpeed;
    g_rotation = rotationSpeed;
    setSpeedInPercent(0);

    //disable brake and set pulse mode
    
    //updateASR wont be called immediately so we have to set the first step
    //accelerate();
    g_speedPID.setSetPoint( g_goalSpeed );


    //if goalspeed and rotationspeed == 0 -> do nothin in drive()
    if(g_goalSpeed != 0 || g_rotation != 0) {
        //attach to the tickers so the system wil start.
        attach();
    } else {
        //stop
        detach();
        setSpeedInPercent(0);
        enableBrake();
    }
}

//stop the motor
void MotorController::stop()
{
    //set brake
    enableBrake();
    //if pid enabled -> set speed 0
    if(g_pidControlled) g_speedPID.setSetPoint(0);
    //delete pulses
    //detach PID
    g_pidTicker.detach();
}
// Sets the speed of the motor using the given float between -100% and 100%
void MotorController::setSpeedInPercent( float percent)
{
    if( percent > 100 ) {
        percent = 100;
    } else if( percent < -100 ) {
        percent = -100;
    }
    //Scale from -100% : 100% to -0.5 : 0.5
    percent *= 0.005;
    //Set percent to the pwm pin, turning it to a value of 0 : 1
    _speedPin  =   0.5+percent;
}

// Stops the motor, motor can't drive when this is on.
void MotorController::enableBrake()
{
    // brake = 1 -> free running (off)
    // brake = 0 -> brake (on)
    _brakePin = 0;
}
// Disables the brake of the motor, make sure to call this before driving.
void MotorController::disableBrake()
{
    // brake = 1 -> free running (off)
    // brake = 0 -> brake (on)
    _brakePin = 1;
}

void MotorController::tachoInterrupt()
{
    g_hallData = _hallPin.read();
    g_deltaTime = g_speedTimer.read_us(); 
    g_speedTimer.reset();
}

// Updates the velocity of this motor, based on the current speed
// and the speed we want to eventually drive at.
// Is attached as an interrupt by the attach() function
void MotorController::updatePid()
{
    // Meters_Per_Pulse / Time_Per_Pulse = m/s
    float temp_speed =  M_PER_PULSE / (g_deltaTime/1000000);
    if(g_hallData == 0) { //if _h3 is high then the wheel is rotating reverse
        g_currentSpeed = temp_speed;
    } else {
        g_currentSpeed = temp_speed * -1;
    }
    //rotate
    if(g_goalSpeed == 0 && g_rotation == 0) {
       stop();
    } else {
       g_speedPID.setSetPoint( g_goalSpeed + (g_rotation/50));
    }
        
    g_speedPID.setProcessValue(g_currentSpeed*20);  // g_currentSpeed is in m/s, we want the value to be between 0 and 100
    g_pidSpeed = g_speedPID.compute();
    // Give the PID a headstart, so that the rotating doesn't go wrong
    // This works because the PID adapts itself to the situation
    // For example, if you call setSetPoint(0) and then call setSpeedInPercent(g_pidSpeed + 100)
    //    Then the g_pidSpeed will eventually turn into -100, so that the motor is set to 0.
    setSpeedInPercent( g_pidSpeed + (g_rotation / 50));
}
// Returns a float containing the currentspeed of the robot.
float MotorController::getCurrentSpeed()
{
    return g_currentSpeed;
}
// Returns a float containing the current speed the PID tells the robot to drive at.
float MotorController::getPidSpeed()
{
    return g_pidSpeed;
}
// Returns a float containing the velocity we want this motor to drive at.
float MotorController::getGoalSpeed()
{
    return g_goalSpeed;
}
