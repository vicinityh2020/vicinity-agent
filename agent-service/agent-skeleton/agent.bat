@echo off

set SERVER_PORT=9997
set CONFIG_FILE=config/service-config.json
set AGENT_CONFIG_FOLDER=config/agents

set LOGS_FOLDER=logs

set JAR=agent-service-full-0.6.3.2.jar

# ==============================
# DON'T TOUCH ANYTHING BELOW
# ==============================


# ------------------------------
# Persistence
# ------------------------------
set PERSISTENCE_FILE=config/db/thing.db
# ------------------------------


java -Dservice.config=%CONFIG_FILE% -Dagents.config=%AGENT_CONFIG_FOLDER% -Dserver.port=%SERVER_PORT% -Dpersistence.file=%PERSISTENCE_FILE% -Dlogs.folder=%LOGS_FOLDER% -jar $JAR  >> %LOGS_FOLDER%/agent.log


