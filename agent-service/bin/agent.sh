#!/bin/bash

SERVER_PORT=9997
JAR=agent.jar

CONFIG_FILE=config/test-config.json

LOGS_FOLDER=logs

LOG_CONFIG=config/logging

LOGGING_CONFIG_SOURCE=${LOG_CONFIG}/logging.properties
LOGBACK_CONFIG_SOURCE=${LOG_CONFIG}/logback.xml

LOGGING_CONFIG=${LOG_CONFIG}/resolved.logging.properties
LOGBACK_CONFIG=${LOG_CONFIG}/resolved.logback.xml

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


        rm nohup.out;

        java \
            -Dlogback.configurationFile=$LOGBACK_CONFIG_SOURCE \
            -Djava.util.logging.config.file=$LOGGING_CONFIG_SOURCE \
            -Dlogs.folder=$LOGS_FOLDER \
            -cp "$JAR" sk.intersoft.vicinity.agent.service.config.PrepareLogging > nohup.out

        nohup java \
            -Dconfig.file=$CONFIG_FILE \
            -Dserver.port=$SERVER_PORT \
            -Dlogback.configurationFile=$LOGBACK_CONFIG \
            -Djava.util.logging.config.file=$LOGGING_CONFIG \
            -jar $JAR  &
        echo "agent started"
    fi


fi



