#!/bin/bash

SERVER_PORT=9996
JAR=testing-adapter.jar

ADAPTER_ID=adapter-2

AGENT_ENDPOINT=http://localhost:9997/agent
#AGENT_ENDPOINT=http://1c9adfbd.ngrok.io/agent

OBJECTS_FILE=/home/kostelni/work/eu-projekty/vicinity/github-workspace/vicinity-agent/testing-adapter/src/test/resources/objects/disco-objects-2.json
ACTIVE_OBJECTS_FILE=/home/kostelni/work/eu-projekty/vicinity/github-workspace/vicinity-agent/testing-adapter/src/test/resources/objects/disco-objects-2.json

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
        nohup java -Dadapter.id=$ADAPTER_ID -Dagent.endpoint=$AGENT_ENDPOINT -Dobjects.file=$OBJECTS_FILE  -Dactive.objects.file=$ACTIVE_OBJECTS_FILE -Dserver.port=$SERVER_PORT -jar ../target/$JAR  &
        echo "adapter started"
    fi


fi



