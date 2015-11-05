#!/bin/bash
# Make sure you have the right lib in the cwd as you run this tool
# Clones ssh-protobuf repository as available on gitlab to /tmp/ssh-protobuf and
# compiles this to java, compliles that to class' and that'll be compressed to a .jar
#
# @author Jeroen


#remove ssh-protobuf as precaution
rm -rf /tmp/ssh-protobuf/

# get the most resent protobuf files
git clone -b development git@gitlab.com:smallsizeholland/ssh-protobuf.git /tmp/ssh-protobuf
# get hash of current HEAD (used for naming later on)
HASH=$( cd /tmp/ssh-protobuf/ && git rev-parse --short HEAD )
# create org.ssh.senders folder for java files and class files
mkdir -p /tmp/ssh-protobuf/org.ssh.senders
# compile the .proto to .java
protoc --java_out=/tmp/ssh-protobuf/org.ssh.senders/ --proto_path=/tmp/ssh-protobuf/ /tmp/ssh-protobuf/*.proto
# compile .java to .class
javac -d /tmp/ssh-protobuf/org.ssh.senders/ -cp protobuf-java-3.0.0-beta-1.jar /tmp/ssh-protobuf/org.ssh.senders/*/*.java
# package .class in .jar
( cd /tmp/ssh-protobuf/org.ssh.senders/ ; jar cvf protobuf-$HASH.jar */*.class )
# copy protobuf-HASH.jar to cwd
cp /tmp/ssh-protobuf/org.ssh.senders/*.jar ./
# clean /tmp/ssh-protobuf
rm -rf /tmp/ssh-protobuf/
