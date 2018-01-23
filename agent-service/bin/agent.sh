#!/bin/bash

SERVER_PORT=9997
JAR=agent.jar

CONFIG_FILE=config/test-config.json

COMMAND=$1

PID=$(ps -eaf | grep $JAR | grep server.port=$SERVER_PORT | grep -v grep | awk '{print $2}')

echo "command: $COMMAND"
echo "pid: $PID"

if [[ $COMMAND ==  "stop" ]]; then
  echo "stopping agent"

    if [[ "" !=  "$PID" ]]; then
      echo "killing: $PID"
      kill -15 $PID
    else
      echo "process not found"
    fi


else
  echo "starting agent"

    if [[ "" !=  "$PID" ]]; then
      echo "agent is running"
    else
        nohup java -Dconfig.file=$CONFIG_FILE -Dserver.port=$SERVER_PORT -jar ../target/$JAR  &
        echo "agent started"
    fi


fi



