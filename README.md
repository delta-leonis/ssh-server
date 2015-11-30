# ssh-server

This is the RoboCup server software for team SSH (Small Size Holland) for running SSL-games. Runnable jar-files are to be found on the CI-server (URI TBA).

## Directory structure

    .
    ├── bin                                 This folder contains binaries
    ├── config                              This folder contains application configuration and personal profiles
    │   └── default
    ├── lib                                 This folder contains third party libraries
    │   └── dist
    ├── LICENSE
    ├── README.md
    └── src                                 This folder contains the source code of the project
        ├── examples
        └── org
            └── ssh
                ├── controllers             This folder contains logic that deals with controllers (keyboard, mouse, etc.)
                ├── field3d                 This folder contains logic that deals with the 3D representation of the field
                │   ├── core
                │   └── gameobjects
                ├── managers                This folder contains logic that deals with the application framework
                ├── models                  This folder contains model implementations
                │   ├── enums
                │   └── message
                ├── pipelines               This folder contains pipeline implementations
                ├── senders                 This folder contains output/broadcasting implementations
                ├── services                This folder contains logic that deals with services their implementations
                │   ├── consumers
                │   ├── couplers
                │   ├── pipeline            This folder contains logic that deals with pipelines
                │   │   └── packets
                │   └── producers
                ├── strategy                This folder contains logic that deals with strategy
                │   └── shrouds
                ├── ui                      This folder contains logic that deals with the user interface
                │   ├── components
                │   ├── lua
                │   │   ├── console
                │   │   └── editor
                │   └── windows
                ├── util                    This folder contains utility classes
                └── view                    This folder contains all assets used by the application              
                    ├── components
                    ├── css
                    ├── models
                    ├── textures
                    ├── icons
                    └── windows    

## Motivation

All SSH repositories are currently maintained on [Gitlab](http://www.gitlab.com/) under the namespace `smallsizeholland`. Email [the RoboCup team](mailto:robocup.saxion+git@gmail.com) to request access.

## Installation

In order to compile you will need [Eclipse Mars](https://projects.eclipse.org/releases/mars), either [OpenJDK 8](http://openjdk.java.net/projects/jdk8/) or [Oracle JDK8](http://www.oracle.com/technetwork/java/javase/downloads/index.html), and [e(fx)clipse](http://www.eclipse.org/efxclipse/index.html). Open the project in Eclipse Mars and find `build.fxbuild`. Make sure that *Application class* is set to the right class, otherwise the program won't run. Next, click on *Generate ant build.xml and run*. The program should now be compiled and you can find the jar-file in `./build/dist/`.

Another option is to build the project with maven. In order to do so, you will need to add the `jimObjModelImporter` and `protobuf` jar-files:

```
  mvn install:install-file -Dfile=lib/jimObjModelImporterJFX.jar -DgroupId=com.interactivemesh.javafx -DartifactId=objmodelimporter -Dversion=0.8 -Dpackaging=jar
  mvn install:install-file -Dfile=lib/protobuf-2311e52a.jar -DgroupId=org.ssh.protobuf -DartifactId=protobuf -Dversion=0.1 -Dpackaging=jar
```

Maven can build the project using a simple `mvn package`

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
    Mark Lefering    - TI
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
