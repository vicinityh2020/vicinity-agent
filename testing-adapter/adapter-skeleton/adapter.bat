@echo off

set SERVER_PORT=9998
set JAR=example-adapter-0.6.3.jar

set ADAPTER_ID=example-adapter

set AGENT_ENDPOINT=http://localhost:9997/agent
set OBJECTS_FILE=objects/example-objects.json

java -Dadapter.id=%ADAPTER_ID% -Dagent.endpoint=%AGENT_ENDPOINT% -Dobjects.file=%OBJECTS_FILE% -Dserver.port=%SERVER_PORT% -jar %JAR%



