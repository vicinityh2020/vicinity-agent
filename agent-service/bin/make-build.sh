#!/bin/bash

SKELETON=agent-skeleton
BUILD=../build
JAR=agent.jar

rm -rf $SKELETON
rm -rf $BUILD
mkdir $BUILD
mkdir $SKELETON
mkdir $SKELETON/logs
mkdir $SKELETON/db
mkdir $SKELETON/config
mkdir $SKELETON/config/agents

echo "skeleton folder created"

cp config/service-config.json $SKELETON/config/
echo "added configurations"


cp -a ../bin/agent.sh $SKELETON/
echo "added script"

zip -r $BUILD/agent-skeleton.zip $SKELETON
echo "zipped"

cp -a ../target/$JAR $BUILD/
echo "added jar"

