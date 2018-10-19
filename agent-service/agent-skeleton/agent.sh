#!/bin/bash

SERVER_PORT=9997
CONFIG_FILE=config/service-config.json
AGENT_CONFIG_FOLDER=config/agents

LOGS_FOLDER=logs

JAR=agent-service-full-0.6.3.jar

# ==============================
# DON'T TOUCH ANYTHING BELOW
# ==============================


# ------------------------------
# Persistence
# ------------------------------
PERSISTENCE_FILE=config/db/thing.db
# ------------------------------

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


        rm $DEFAULT_LOG;

        nohup java \
            -Dservice.config=$CONFIG_FILE \
            -Dagents.config=$AGENT_CONFIG_FOLDER \
            -Dserver.port=$SERVER_PORT \
            -Dpersistence.file=$PERSISTENCE_FILE \
            -Dlogs.folder=$LOGS_FOLDER \
            -jar $JAR  >> $LOGS_FOLDER/agent.log 2>&1 &
        echo "agent started"
    fi


fi



