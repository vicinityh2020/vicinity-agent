#!/bin/bash

SERVER_PORT=9995
JAR=testing-adapter.jar

#OBJECTS_FILE=/home/kostelni/work/eu-projekty/vicinity/unikl-workspace/vicinity-agent/testing-adapter/src/test/resources/objects/unikl-objects.json
OBJECTS_FILE=/home/kostelni/work/eu-projekty/vicinity/unikl-workspace/vicinity-agent/testing-adapter/src/test/resources/objects/disco-objects.json

COMMAND=$1

PID=$(ps -eaf | grep $JAR | grep server.port=$SERVER_PORT | grep -v grep | awk '{print $2}')

echo "command: $COMMAND"
echo "pid: $PID"

if [[ $COMMAND ==  "stop" ]]; then
  echo "stopping adapter"

    if [[ "" !=  "$PID" ]]; then
      echo "killing: $PID"
      kill -15 $PID
    else
      echo "process not found"
    fi


else
  echo "starting adapter"

    if [[ "" !=  "$PID" ]]; then
      echo "adapter is running"
    else
        nohup java -Dobjects.file=$OBJECTS_FILE -Dserver.port=$SERVER_PORT -jar ../target/$JAR  &
        echo "adapter started"
    fi


fi



