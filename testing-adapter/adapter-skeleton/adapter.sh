#!/bin/bash

SERVER_PORT=9998
JAR=example-adapter-0.6.3.1.jar

ADAPTER_ID=example-adapter

AGENT_ENDPOINT=http://localhost:9997/agent
OBJECTS_FILE=objects/example-objects.json

java -Dadapter.id=$ADAPTER_ID -Dagent.endpoint=$AGENT_ENDPOINT -Dobjects.file=$OBJECTS_FILE -Dactive.objects.file=$ACTIVE_OBJECTS_FILE -Dserver.port=$SERVER_PORT -jar $JAR



