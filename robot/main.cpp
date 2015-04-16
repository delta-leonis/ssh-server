/*

Date:       June 2014
Updated:    April 2015

Project:    Robocup 2014

Classname:  Main.cpp

Description:

This class is started by the Mbed controller. The main method is
called when the system is turned on. This class starts the connected
hardware like the wireless controllers and motorcontrollers.
*/


//--------------------Includes-------------------------------//
#include "mbed.h"
#include "RobotController.h"
#include "batteryCheck.h"
#include <iostream>
#include <iomanip>
#include <sstream>
#include "rtos.h"

#include "nRF24L01P.h"
#include "robotpacket.h"
#include "Watchdog.h"

//--------------------Defines-------------------------------//
#define TRANSMIT_PIPE NRF24L01P_PIPE_P0
#define RECEIVE_PIPE NRF24L01P_PIPE_P0
#define NRF_TX_ADDRESS 0x1002
#define NRF_RX_ADDRESS 0x1001
#define NRF_PIPE_WIDTH 4

#define SERIAL_BAUDRATE 115200

#define MINIMAL_BATTERY_VOLTAGE 12.8

#define DATALOG_INTERVAL 1000
#define KICKER_INTERVAL 100

//#define DATALOG

//--------------------Debug Mode-----------------------------//
/*
To turn on the debugmode and receive data over the serial connection
remove the "//" from the next line.
*/

#define DEBUG

//--------------------Pin assignments-----------------------//
PwmOut kicker(p24);                                 // Pin to set kicker speed (Value between 0.0 and 1.0)
PwmOut chipper(p25);                                // Pin to set the chipper speed (Value between 0.0 and 1.0)
PwmOut dribbler(p26);                               // Pin to set the dribbler speed (Value between 0.0 and 1.0)
nRF24L01P nerf(p5, p6, p7, p8, p9);                 // Wirless NRF module (mosi, miso, sck, csn, ce, irq)   //change pin 24 to something else
batteryCheck battery(p20);                          // Battery voltage inputpin

DigitalOut chargeDisable(p19);                      // Set high to disable charge circuit
DigitalIn irSensor(p18);                            // Input pin for the infra red sensor. 




using namespace std;

//--------------------Method declarations-------------------//
void init(void);
void receive();
void showDebugInformation();
void flushSerialBuffer(Serial mSerial);
void fillLogData(void);
void datalogThread(void const *args);
void kickThread(void const *args);
void checkBatteryVoltageThread(void const *args);
void showDebugInformation();
void showPacketDebug(RobotPacket *packet,bool checksumCheck,int checksum,int rxDataCnt);
int getFrequency(char channel);
void turnOffSolenoids(void);


//-----------------------------------------------Variable declarations-----------------//
int packetsize          = 16;                               // packetsize used for NRF communications in bytes. Don't change this unless actualSize surpasses packetSize
int actualSize          = 11;                               // The amount of bytes of data we'll be receiving. Change this variable when adding arguments to packet. 
int datarate            = NRF24L01P_DATARATE_2_MBPS;        // datarate used for NRF communications
int frequency           = 2525;                             // frequency used for NRF communications
int robotID             = 0x1;                              // will be read by DIP switches
int failedPacketCounter = 0;                                // counter for packets with checksumfail
int temp                = 0;                                // none essential variable which can be used when needed
Watchdog wdt;                                               // make new watchdog timer


bool flag           = false;                                // Flag to declare data read to LOG file to server
bool flagA          = true;                                 //
bool flagB          = false;                                // Flags A,B,C which are used for debug purpose to send motorcontroller datalog to server
bool flagC          = false;                                //
bool batteryFlag    = false;                                // Used in battery thread. when set to true the current battery voltage is sent to server
bool batteryLowFlag = false;                                // When battery voltage reaches its minimal set value this flag will be set to True and robot goes in emergencymode
bool dribbelSet     = false;                                // This flag is set to true when the dribbel speed is set in the reived packed from the NRF
// In the dribbelthread this wil result in spinning the dibbler at the front of the robot
bool kicked = false;
bool logData = false;

//Arrays are used for sending data to Wireless module
char charArray[16];
char MAArray[16];
char MBArray[16];
char MCArray[16];

RobotController *rc;                                        // make an instance of motorcontroller

Thread *t1;
Thread *t2;
Thread *t4;

I2C i2c(p28, p27);
const int addr = 0x70; //0x70
char cmd[3];
char batteryError[3];

int chipkickSpeed = 0;
int frequencies[5] = {2436,2450,2490,2500,2525};    //frequencies for the nrf

//Ticker ticker;

#ifdef DATALOG
    LocalFileSystem local("local");     // Prepares for data storage
#endif



/* -----------------------------------------
methode name: main(void)

FUNCTION:
    This method is called at start of the controller. In this method serveral
    threads are declared and started. In an endless loop the Wireless controller is checked
    for received data, data to send back to the server and checks if the robot has to go into
    emergencymode due to low batteryvoltage.


KNOWN PROBLEMNS IN THIS METHOD:
    None.

*///----------------------------------------

int main(void)
{
    init();
    printf("STARTING PROGRAM\n");

    showDebugInformation();

    wdt.kick(1.0); //watchdog set

    // Endless mainloop
    while(1) {
        //if(!batteryLowFlag) {     // Not implemented yet. So far, no data is sent back to the server.
        if( nerf.readable(RECEIVE_PIPE) ){
            receive();
        }

        wdt.kick();
    }
}

// Initializes the robot.
void init(void)
{
    kicker.period_us(40);   // 
    chipper.period_us(40);  // Set the pwm frequency for the kicked, chipper and dribbler.
    dribbler.period_us(40); //
    kicker.write(0);
    chipper.write(0);
    chargeDisable = 0;

    // initialize robot controller
    rc = new RobotController();

    //initialize I/O expander
    char input[1];
    i2c.read(addr, input, 1);
    cmd[0] = input[0]&0x7F;
    batteryError[0] = 0xA0;
    i2c.write(addr, cmd, 1);
    i2c.stop();

    char highByte = (cmd[0]&0x70)>>4;
    char lowByte = cmd[0]&0x0F;
    robotID = lowByte;
    frequency = getFrequency(highByte);
    printf("robotID: %d, frequency: %d", robotID, frequency);


    // initialize threads
    #ifdef DATALOG
        t1 = new Thread(datalogThread);
    #endif
    t2 = new Thread(checkBatteryVoltageThread);
    t4 = new Thread(kickThread);

    // intitialize Serial Connection
    Serial mSerial(USBTX, USBRX);
    flushSerialBuffer(mSerial);
    mSerial.baud(SERIAL_BAUDRATE);

    // initialize NRF24L01 wireless module
    nerf.powerUp();
    nerf.setAirDataRate(datarate);
    nerf.setRfFrequency(frequency);
    nerf.setTransferSize(packetsize,NRF24L01P_PIPE_P0);
    nerf.setRxAddress(NRF_RX_ADDRESS, NRF_PIPE_WIDTH, RECEIVE_PIPE);
    nerf.setTxAddress(NRF_TX_ADDRESS, NRF_PIPE_WIDTH);
    nerf.setReceiveMode();
    nerf.enable();
    //clear old data from receiver FIFO buffer
    nerf.flushRXFIFO();


    dribbler.write(0); //handled with hardware pull down resistor
}

//changes channel identifier (0-5) to a frequency (like 2525)
int getFrequency(char channel)
{
    if(channel > 5)
        return 2525;
    return frequencies[channel];
}

// Constantly called by the main() function.
// Receives incomming packets, parses them to a robotpacket.h and sends the data to the RobotController.
void receive()
{
    char rxData[packetsize];
    //read the data into the receive buffer
    int rxDataCnt = nerf.read(RECEIVE_PIPE, rxData, packetsize);
    //Data received
    RobotPacket* packet = (RobotPacket*)rxData;

    //compare data to checksum
    int8_t checksum = 0;
    for(int i = 0; i < actualSize -1; i++) {
        checksum ^= rxData[i];
    }
    //if data is correct
    if(packet->checksum == checksum) {
        //if robotID incorrect -> return
        if(packet->robot_id != robotID) return;
        // Only log data if the robot is driving.
        if(packet->directionSpeed != 0 && packet->rotationSpeed != 0){
            logData = true;    
        }
        else{
            logData = false;
        }
        //else send received data to RobotController
        rc->drive(
            packet->direction,
            packet->directionSpeed,
            packet->rotationSpeed
        );
        if(packet->dribbleSpeed == 1) {
            dribbler = 1.0f;//.write(packet->dribbleSpeed*1.0f);
        } else if(packet->dribbleSpeed == 0) {
            dribbler.write(0);
        }
        if(packet->kickerSpeed != 0) {
            chipkickSpeed = packet->kickerSpeed;
            t4->signal_set(0x4);
            //TODO fix having kicker always on when packet is received
        }
#ifdef DEBUG
        showPacketDebug(packet,true,checksum,rxDataCnt);
#endif
    }
    //if checksum failed
    else {
#ifdef DEBUG
        showPacketDebug(packet,false,checksum,rxDataCnt );
#endif
        failedPacketCounter++;

    }
}

// Logs all relevant data of the motors to the LogFile.csv 
// This function is called by datalogThread() every DATALOG_INTERVAL milliseconds
void fillLogData()
{
    // Detach ALL interrupts. If this is not done, the memory of the entire robot may be wiped.
    rc->getMotorControllerA()->detach();
    rc->getMotorControllerB()->detach();
    rc->getMotorControllerB()->detach();

    FILE *fp = fopen("/local/LogFile.csv", "a");
    // Print the dat to LogFile.csv
    fprintf(fp, "Final Goal: %d,", rc->getGoalSpeed());
    fprintf(fp, "MA,CurrentSpeed: %.02f,PIDSpeed: %.02f,GoalSpeed: %.02f,",
            rc->getMotorControllerA()->getCurrentSpeed(),
            rc->getMotorControllerA()->getPidSpeed(),
            rc->getMotorControllerA()->getGoalSpeed()
           );
    fprintf(fp, "MB,CurrentSpeed: %.02f,PIDSpeed: %.02f,GoalSpeed: %.02f,",
            rc->getMotorControllerB()->getCurrentSpeed(),
            rc->getMotorControllerB()->getPidSpeed(),
            rc->getMotorControllerB()->getGoalSpeed()
           );
    fprintf(fp, "MC,CurrentSpeed: %.02f,PIDSpeed: %.02f,GoalSpeed: %.02f\n",
            rc->getMotorControllerC()->getCurrentSpeed(),
            rc->getMotorControllerC()->getPidSpeed(),
            rc->getMotorControllerC()->getGoalSpeed()
           );
           
    fclose(fp);
    // Don't forget to re-attach all the interrupts.
    rc->getMotorControllerA()->attach();
    rc->getMotorControllerB()->attach();
    rc->getMotorControllerC()->attach();

    flag = true;
}

//remove all characters from buffer
void flushSerialBuffer(Serial mSerial)
{
    while (mSerial.readable()) {
        mSerial.getc();
    }
}

// Shows some cool debug information when you start your robot.
// Plug robot in with a USB and check on cutecom whether this is printed correctly
// If this is not the case, there might be something wrong with the robot.
void showDebugInformation()
{
    printf( "\r\n---------------------------Robot Info-----------------------------\r\n");
    printf( "RobotID           : %d" , robotID );
    printf( "\r\n---------------------------NRF1 DATA-----------------------------\r\n");
    printf( "NRF1 Frequency    : %d MHz\r\n",  nerf.getRfFrequency() );
    printf( "NRF1 Output power : %d dBm\r\n",  nerf.getRfOutputPower() );
    printf( "NRF1 Data Rate    : %d kbps\r\n", nerf.getAirDataRate() );
    printf( "NRF1 TX Address   : 0x%010llX\r\n", nerf.getTxAddress() );
    printf( "NRF1 RX Address   : 0x%010llX\r\n", nerf.getRxAddress() );
    printf( "Packetsize        : %d Bytes\r\n",packetsize);
    printf( "\r\n---------------------------Battery Status-----------------------------\r\n");
    printf( "\r\nBattery voltage is: %f\r\n",battery.getBatteryVoltage());
}

// Prints whatever the packet contained to the cout
void showPacketDebug(RobotPacket *packet,bool checksumCheck,int checksum,int rxDataCnt)
{
    if (checksumCheck==true) {
        cout
                << "Packet OK! " << packet->direction << endl
                << "Received " << rxDataCnt << " bytes:" << endl
                << "Robot id: " << (int)packet->robot_id << endl
                << "direction: " << packet->direction << endl
                << "directionSpeed: " << packet->directionSpeed << endl
                << "rotationSpeed: " << packet->rotationSpeed << endl
                << "kickerSpeed: " << (int)packet->kickerSpeed << endl
                << "dribbleSpeed: " << (int)packet->dribbleSpeed << endl;
    } else {
        cout
                << "Invalid packet!" << endl
                << "Received " << rxDataCnt << " bytes:" << endl
                << "Robot id: " << (int)packet->robot_id << endl
                << "direction: " << packet->direction << endl
                << "directionSpeed: " << packet->directionSpeed << endl
                << "rotationSpeed: " << packet->rotationSpeed << endl
                << "kickerSpeed: " << (int)packet->kickerSpeed << endl
                << "dribbleSpeed: " << (int)packet->dribbleSpeed << endl
                << "checksum: " << (int)packet->checksum << endl
                << "Calculated checksum: " << (int)checksum << endl;

    }
}

// Checks the battery voltage every 5 seconds. 
// If the battery is low, the buzzer will make an annoying sound.
// Sadly, the buzzer hasn't been connected correctly.
void checkBatteryVoltageThread(void const *args)
{
    while (true) {
        Thread::wait(5000);
        // check for battery critical voltage level
        if(battery.getBatteryVoltage() < MINIMAL_BATTERY_VOLTAGE) {
            cmd[0] = 0xA0;
            i2c.write(addr, cmd, 1);
            i2c.stop();
        } else {
            batteryLowFlag = false;
        }
    }
}

// Logs standard data every DATALOG_INTERVAL milliseconds
void datalogThread(void const *args)
{
    while (true) {
        Thread::wait(DATALOG_INTERVAL);
        // Fix to make sure we can access the Mbed folder.
        // If this if-statement is removed, the mbed will write 
        // even when we try to change its code
        if(logData){
            fillLogData();
        }
    }
}

// Handles the kicking AND the chipping the robot has to do.
// This function runs inside a seperate thread.
// When a signal 0x4 is sent to thread t4, this function will kick or chip, 
// based on what is contained within the chipkickSpeed variable.
void kickThread(void const *args)
{
    while(true) {
        Thread::signal_wait(0x4);
        float pwmValue = abs(chipkickSpeed)/100.0;
        PwmOut solanoid = (chipkickSpeed > 0) ? kicker : chipper;
        solanoid.write(pwmValue);
        wait_ms(10);
        solanoid.write(0);
    }
}
