/*
Author: niet Aron Faas
Date: Februari 2015

Function:

Program for Robocup Basestation. Main fuction is to receive data to
control 6 robocup robots. Data is received from Robocup server via
UDP packets, and forwards this data with two NRF24L01 wireless 2,4ghz
modules.


*/
#include <iostream>
#include <sstream>
#include <string>
#include "mbed.h"                                   // MBED Library for basic IO
#include "EthernetInterface.h"                      // Ethernet Library (mbed.org, MIT License) 
#include "nRF24L01P.h"                              // NRF24L01 Library (2010 Owen Edwards)    
#include "rtos.h"                                   // Realtime OS Library (mbed.org, MIT License)
#include "robotpacket.h"
#include "datapacket.h"
#include "configpacket.h"
#include "IniManager.h"

#define TRANSFER_SIZE   16                          // Constant for setting buffersizes which are used for wireless transmission
#define RECEIVE_PIPE1 NRF24L01P_PIPE_P0
#define CONFIG "/local/config.ini"

using namespace std;

Serial pc(USBTX, USBRX);                            // Enable serial connection for Debug purpose


nRF24L01P NRF1(p5  ,p6  ,p7  ,p8  ,p9  ,p10);       // Configure 1st NRF->  Pin5->mosi || Pin6->miso || Pin7->sck || Pin8->csn || Pin9->ce || Pin10->irq
EthernetInterface eth;                              // Setup ethernet interface needed for creating socket
Endpoint hostEndpoint;                              // Endpoint needed for storing host IP and Port
Endpoint endpoint;                                  // Endpoint for sending data to serverApp

UDPSocket sendSocket;                               // create socket for sending udp messages
UDPSocket receiveSocket;                            // create socket for receiving udp messages

DigitalOut myled1(LED1);                            // initialize LED for debug purpose
DigitalOut myled2(LED2);                            // initialize LED for debug purpose
DigitalOut myled3(LED3);                            // initialize LED for debug purpose

INI ini(CONFIG);                                    //initialize config file

int frequencies[5] = {2436,2450,2490,2500,2525};    //frequencies for the nrf

/* -----------------------------------------
methode name: init()

FUNCTION:
initializes several important components

    * NRF1
    * NRF2
    * Ethernet (DHCP)
    * Sockets (Sending/Receiving)


KNOWN PROBLEMS IN THIS METHOD:
- NONE

*///----------------------------------------

void init()
{
    char buffer[100];
    cout << "DHCP init: " << (eth.init() == 0 ? "OK" : "Failed") << endl;

    cout << "Ethernet MAC address: " << eth.getMACAddress() << endl;
    cout << "Connecting ethernet: ";
    ini.ReadString("server", "timeout", buffer, sizeof(buffer));
    int timeout = atoi(buffer);
    cout << (eth.connect(timeout) == 0 ? "OK" : "Failed") << endl;
    sendSocket.init();

    ini.ReadString("server", "port", buffer, sizeof(buffer));
    int port = atoi(buffer);
    ini.ReadString("server", "ip", buffer, sizeof(buffer));
    char* ipAddress = buffer;
    cout << "Server address: " << ipAddress << ":" << port << endl;
    endpoint.set_address(ipAddress, port);

    ini.ReadString("multicast", "port", buffer, sizeof(buffer));
    int multicast_port = atoi(buffer);
    cout << "Binding port " << multicast_port << ": ";
    cout << (receiveSocket.bind(multicast_port) == 0 ? "OK" : "Failed") << endl;
    ini.ReadString("multicast", "ip", buffer, sizeof(buffer));
    char* multicast_group = buffer;
    cout << "Joining multicast group \"" << multicast_group << "\": ";
    cout << (receiveSocket.join_multicast_group(multicast_group) == 0 ? "OK" : "Failed") << endl;

    cout << "Network init completed" << endl;

    cout << endl;

    cout << "Starting NRF1" << endl;
    NRF1.powerUp();
    wait(0.5);

    ini.ReadString("nrf", "frequency", buffer, sizeof(buffer));
    int frequency = atoi(buffer);

    NRF1.setRfFrequency(frequency);
    NRF1.setAirDataRate(NRF24L01P_DATARATE_2_MBPS);

    NRF1.setTransferSize(TRANSFER_SIZE);
    NRF1.setRxAddress(0x1002, 4, RECEIVE_PIPE1);
    NRF1.setTxAddress(0x1001, 4);
    NRF1.setReceiveMode();
    NRF1.enable();
    NRF1.flushRXFIFO();

    cout << "Init completed" << endl;
}

/* -----------------------------------------
methode name: displayConfig()

FUNCTION:

Shows debug information on the Serial Connection like
    * NRF settings
    * acquired IP address

KNOWN PROBLEMS IN THIS METHOD:
- NONE

*///----------------------------------------

void displayConfig()
{
    pc.printf("---------------------------NRF1 DATA-----------------------------\r\n");
    pc.printf("NRF1 Frequency    : %d MHz\r\n",  NRF1.getRfFrequency());
    pc.printf("NRF1 Output power : %d dBm\r\n",  NRF1.getRfOutputPower());
    pc.printf("NRF1 Data Rate    : %d kbps\r\n", NRF1.getAirDataRate());
    pc.printf("NRF1 TX Address   : 0x%010llX\r\n", NRF1.getTxAddress());
    pc.printf("NRF1 RX Address   : 0x%010llX\r\n", NRF1.getRxAddress(RECEIVE_PIPE1));
    pc.printf("-----------------------------------------------------------------\r\n");
    pc.printf("Local IP Address  : %s\r\n", eth.getIPAddress());
}


/* -----------------------------------------
methode name: toString(int, char*)

FUNCTION: converts int to char array

*///----------------------------------------
void toString(int number, char* output){
    stringstream strs;
    strs << number;
    string temp_str = strs.str();
    
    strcpy(output, temp_str.c_str());
}


/* -----------------------------------------
methode name: editConfig(ConfigPacket*)

FUNCTION: Edits config.ini with new frequency,
possibility to expand for more features that 
have to be changed dynamicly

*///----------------------------------------
void editConfig(ConfigPacket* packet)
{
    __disable_irq();
    if(frequencies[0] == packet->channel_freq || 
       frequencies[1] == packet->channel_freq || 
       frequencies[2] == packet->channel_freq || 
       frequencies[3] == packet->channel_freq || 
       frequencies[4] == packet->channel_freq){
        pc.printf("Frequency changed to: %d\n", packet->channel_freq);
        char buff[16];
        toString(packet->channel_freq, buff);
        ini.WriteString("nrf", "frequency", buff);
        NRF1.setRfFrequency(packet->channel_freq);
        displayConfig();
    }else
        pc.printf("Frequency not supported, config.ini not updated");
    __enable_irq();
}

/* -----------------------------------------
methode name: parsePacket()

FUNCTION: parses the recieved packet and handles it

*///----------------------------------------

void parsePacket(char receiveBuffer[])
{
    DataPacket* packet = (DataPacket*)receiveBuffer;
    switch(packet->message_type) { // messagetype
        case 0x01:
        case 0x02: {
            RobotPacket* robotpacket = (RobotPacket*) receiveBuffer;
            NRF1.write(NRF24L01P_PIPE_P0, (char*)robotpacket, TRANSFER_SIZE);
            break;
        }

        case 0x7f: {
            ConfigPacket* configpacket = (ConfigPacket*) receiveBuffer;
            editConfig(configpacket);
            break;
        }
        default : {
            cout << "Unknown message_type: " << packet->message_type << endl;
            break;
        }
    }
}



/* -----------------------------------------
methode name: receiverThread()

FUNCTION:
This method should be started as a thread, and waits for a packet to
arrive at the receiving socket. when a packet is received this thread
will forward the information to the NRF module.

For debug purposes there are printf statements which prints the received
data over the serial line.

KNOWN PROBLEMNS IN THIS METHOD:

    !When this thread starts the NRF thread doesn`t function the the right way
    !Don`t know if this is the efficient\correct way to use UDP

*///----------------------------------------

void receiveFromServerThread(void const *args)
{
    cout << "Started receiveFromServerThread" << endl;
    char receiveBuffer[TRANSFER_SIZE];
    //receiveSocket.set_blocking(false, 10);
    cout << "Waiting for packets..." << endl;
    while (1) {
        int n = receiveSocket.receiveFrom(hostEndpoint, receiveBuffer, sizeof(receiveBuffer));
        if(n > 0) {
            //printf("Packet from \"%s\": %d bytes\r\n", hostEndpoint.get_address(), n);
            parsePacket(receiveBuffer);
            myled3 = !myled3;
        }
        Thread::yield();
    }
}
/* -----------------------------------------
methode name: nrf1Thread()
FUNCTION:
This method must be started as a thread. when started this method can receive
data from the NRF and send this data to the server via UDP packets.

KNOWN PROBLEMNS IN THIS METHOD:

 !this method is not implemented yet. now it should just receive data from the
 nrf and send it via the com port.

*///----------------------------------------

void nrfThread1(void const *args)
{
    cout << "Started NRF1 receive thread" << endl;
    char    rxData[TRANSFER_SIZE];
    int     rxDataCnt = 0;

    while (1) {
        if ( NRF1.readable(RECEIVE_PIPE1) ) {
            rxDataCnt = NRF1.read( RECEIVE_PIPE1, rxData, sizeof( rxData ) );       // ...read the data into the receive buffer
            //sendSocket.sendTo(hostEndpoint, rxData, sizeof(rxData));
            pc.printf( "Received data from NRF1:");
            for ( int i = 0; rxDataCnt > 0; rxDataCnt--, i++ ) {
                pc.putc( rxData[i] );
            }
            pc.printf( "\r\n");
            myled1 = !myled1;
        }
        Thread::wait(10);
        Thread::yield();
    }
}

void nrfHandler(const char* name, nRF24L01P& nrf, int pipe, DigitalOut& led)
{
    static char rxBuffer[TRANSFER_SIZE];

    if(nrf.readable(pipe)) {
        nrf.read(pipe, rxBuffer, sizeof(rxBuffer));
        sendSocket.sendTo(endpoint, rxBuffer, TRANSFER_SIZE);
        led = !led;
    }
}
/*
void spinControl()
{
    cout << "spinControl()" << endl;
    RobotPacketpacket= {
        1, 11,
        0, 0, 0,
        0, 0,
        0, 0, 0
    };
    packet.rotationAngle = 90;
    char* p = (char*)&packet;
    for(int i = 0; i < 14; i++) { packet.checksum ^= (char)*p++; }
    while(true)
    {
        if(pc.readable())
        {
            cin >> packet.rotationSpeed;
            cout << "New direction speed: " << packet.rotationSpeed << endl;
            cin >> packet.rotationAngle;
            cout << "New rotation angle: " << packet.rotationAngle << endl;
            packet.checksum = 0;
            char* p = (char*)&packet;
            for(int i = 0; i < 14; i++) { packet.checksum ^= (char)*p++; }

            sendToRobot(&packet);
            myled2 = !myled2;
        }
    }
} */

DigitalIn ethLinkIn(P1_25);
DigitalIn ethSpeedIn(P1_26);
DigitalOut ethLinkOut(p29);
DigitalOut ethSpeedOut(p30);

void ethernetLedHandler()
{
    ethLinkOut = ethLinkIn;
    ethSpeedOut = ethSpeedIn;
}

/* -----------------------------------------
methode name:main()
FUNCTION:
starts the mbed controller and runs the essential methods

KNOWN PROBLEMS IN THIS METHOD:

*///----------------------------------------
int main()
{
    pc.baud(115200);
    cout << endl << endl << "Revision 14" << endl;
    LocalFileSystem local("local");               // Create the local filesystem under the name "local"

    init();

    displayConfig();

    cout << endl;

    static char ethernetBuffer[TRANSFER_SIZE];
    //receiveSocket.set_blocking(true, 10);
    while (1) {
        int ethernetCount = receiveSocket.receiveFrom(hostEndpoint, ethernetBuffer, sizeof(ethernetBuffer));
        if(ethernetCount > 0) {
            pc.printf("Packet from \"%s\": %d bytes\r\n", hostEndpoint.get_address(), ethernetCount);
            parsePacket(ethernetBuffer);
            myled3 = !myled3;
        }
        nrfHandler("NRF1", NRF1, RECEIVE_PIPE1, myled1);
        ethernetLedHandler();
    }
}