#!/bin/bash

JAR=testing-adapter.jar

ADAPTER_ENDPOINT=http://localhost:9995/adapter

OID=testing-oid
PID=testing-pid

GENERATOR=sk.intersoft.vicinity.adapter.testing.eventgenerator.EventGenerator

echo "running : $GENERATOR"
java -cp ../target/$JAR -Dadapter.endpoint=$ADAPTER_ENDPOINT -Doid=$OID -Dpid=$PID $GENERATOR



