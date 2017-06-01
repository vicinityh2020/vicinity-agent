#!/bin/bash

#CONFIG_FILE=/home/kostelni/work/eu-projekty/vicinity/bitbucket-workspace/vicinity-agent/agent/bin/agent-config.json
CONFIG_FILE=/home/kostelni/work/eu-projekty/vicinity/bitbucket-workspace/vicinity-agent/agent/bin/agent-config-gorenje.json

java -Dconfig.file=$CONFIG_FILE -jar ../target/dependency/jetty-runner.jar --port 9997 --path / ../target/*.war

