## prototester

A small CLI program to send protobuf packets containing random data to a given IP address. Sends the 'Radio' packet as provided in /ssh-protobuf

## usage arguments
    -w	number of wrappers/packets (default 2)
    -c 	number of commands per wrapper (default 3)
    -i 	destination ip (default 192.169.1.10)
    -p 	destination port (default: 2002)
    -t  timout between packets in ms(default: 0)

## Example data generation
    RobotID:		0
    VelocityX:		0.468248
    VelocityY:		0.240160
    VelocityR:		0.803460
    dribbleSpin:	0.720443

    RobotID:		1
    VelocityX:		0.072890
    VelocityY:		0.185611
    VelocityR:		0.775841
    flatKick:		2.607284
    chipKick:		1.354360
    dribbleSpin:		0.748387
    distance:		38

## problems
Currently only uses RadioCommand in the wrapper, since BasestationSettings and RobotSettings proto files aren't defined yet. 
Should not make any difference, because both fields aren't defined as required.


## Contributors

2015.1:
    Jeroen de Jong   - TI

## License

The license can be found in `LICENSE`. It is printed below for good measure.

    Copyright (c) 2015, Robocup SSH (smallsizeholland.com)
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are met:
        * Redistributions of source code must retain the above copyright
          notice, this list of conditions and the following disclaimer.
        * Redistributions in binary form must reproduce the above copyright
          notice, this list of conditions and the following disclaimer in the
          documentation and/or other materials provided with the distribution.
        * Neither the name of SmallSizeHolland nor the
          names of its contributors may be used to endorse or promote products
          derived from this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
    ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
    WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
    DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
    DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
    (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
    LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
    ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
    SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.