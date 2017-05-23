#!/bin/bash

java -jar ../target/dependency/jetty-runner.jar --port 9997 --path / ../target/*.war

