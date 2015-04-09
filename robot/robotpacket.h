#ifndef _ROBOTPACKET_H_
#define _ROBOTPACKET_H_

struct RobotPacket
{
    int8_t message_type;
    int8_t robot_id;
    int16_t direction;
    int16_t directionSpeed;
    int16_t rotationSpeed;
    int8_t kickerSpeed;
    int8_t dribbleSpeed;
    int8_t checksum;
};


#endif