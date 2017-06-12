#!/bin/bash


java -jar ../target/dependency/jetty-runner.jar --port 9996 --path / ../target/*.war

