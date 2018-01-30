#!/bin/bash

BUILD=build
JAR=agent.jar

rm -rf $BUILD
mkdir $BUILD
mkdir $BUILD/logs

echo "build folder created"

cp -a config $BUILD/
echo "added configurations"


cp -a ../target/$JAR $BUILD/
echo "added jar"


cp -a ../bin/agent.sh $BUILD/
echo "added script"

zip -r build/agent.zip build/.
echo "zipped"
