# ssh-server

This is the RoboCup server software for team SSH (Small Size Holland) for running SSL-games. Runnable jar-files are located in `build/dist`.

## Directory structure

    dist/                 This folder contains binaries
      |-  bin
      \-  build
    docs/                 This folder contains the JavaDoc
    lib/                  This folder contains external libraries
    protobuf/             This folder contains the protobuf files
    src/                  This folder contains the project's source code
      |-  org.ssh.controllers/    This folder contains logic for external (handheld) org.ssh.controllers
      |-  gui/            This folder contains logic for the graphical interface
        |-  lua           This folder contains logic for the LUA-engine
        \-  media         This folder contains media related to the graphical interface
      |-  input/          This folder contains logic for the data-org.ssh.services.pipeline
        \-  parser/       This folder contains the data-org.ssh.services.pipeline's parser
      |-  org.ssh.models/          This folder contains the org.ssh.models of the org.ssh.managers
        |-  enum/         This folder contains all enum's for the project.
        \-  message/      This folder contains logic for the SSL-Vision messages (protobuf)
      |-  org.ssh.senders/         This folder contains logic for org.ssh.senders (sending commands to the robots)
      \-  org.ssh.strategy/       This folder contains logic for the org.ssh.strategy
        \-  shrouds       This folder contains mathematical models of game objects
    config/               This folder contains configuration files for the project
    test/                 This folder contains tests
      |-  benchmarks/     This folder contains benchmark tests
      |-  integrations/   This folder contains integrations tests
      \-  unit/           This folder contains unit tests
    tools/                This folder contains (helper-)tools related to the project

## Motivation

All SSH repositories are currently maintained on [Gitlab](http://www.gitlab.com/) under the namespace `smallsizeholland`. Email [the RoboCup team](mailto:robocup.saxion+git@gmail.com) to request access.

## Installation

In order to compile you will need [Eclipse Mars](https://projects.eclipse.org/releases/mars), either [OpenJDK 8](http://openjdk.java.net/projects/jdk8/) or [Oracle JDK8](http://www.oracle.com/technetwork/java/javase/downloads/index.html), and [e(fx)clipse](http://www.eclipse.org/efxclipse/index.html). Open the project in Eclipse Mars and find `build.fxbuild`. Make sure that *Application class* is set to the right class, otherwise the program won't run. Next, click on *Generate ant build.xml and run*. The program should now be compiled and you can find the jar-file in `./build/dist/`.

## Tests

The project currently doesn't have any testing facilities.

## Contributors

2015.1:

    Rob van den Berg - ELT
    Joost Overeem    - TI
    Jeroen de Jong   - TI
    Thomas Hakkers   - TI
    Ryan Meulenkamp  - MT
    Nanko Schrijver  - ELT
    Nick van Deth    - MT
    Jelle Meijerink  - MT
    Mark Leferink    - TI
    Michel Teterissa - MT
    Sven Dicker      - MT
    Emre Elmas       - ELT
    Rimon Oz         - TI

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
