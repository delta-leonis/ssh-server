#ifndef _CONFIGPACKET_H_
#define _CONFIGPACKET_H_

struct ConfigPacket
{
    int8_t message_type;
    int8_t padding;
    int16_t channel_freq;
};


#endif