#!/bin/bash

SERVER_PORT=9994
JAR=aau-adapter.jar

OBJECTS_FILE=/home/kostelni/work/eu-projekty/vicinity/bitbucket-workspace/vicinity-agent/aau-adapter/src/test/resources/objects/aau.json

AGENT_ENDPOINT=http://localhost:9997/agent

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
        nohup java -Dobjects.file=$OBJECTS_FILE -Dserver.port=$SERVER_PORT -Dagent.endpoint=$AGENT_ENDPOINT -jar ../target/$JAR  &
        echo "adapter started"
    fi


fi



