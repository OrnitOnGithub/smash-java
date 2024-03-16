#!/bin/bash

mvn clean
mvn package

command2() {
  java -jar target/smash-java-0.0.0-server.jar
}

command1() {
    java -jar target/smash-java-0.0.0-client.jar
}

# Run commands in parallel
command1 &
pid1=$!
command2 &
pid2=$!

wait $pid1
wait $pid2