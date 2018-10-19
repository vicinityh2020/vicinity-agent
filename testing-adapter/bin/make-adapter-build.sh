#!/bin/bash

VERSION=0.6.3

echo "preparing adapter build for $VERSION"

SKELETON=../adapter-skeleton
BUILD_SKELETON=adapter-build-$VERSION

BUILD=../build
JAR=../target/example-adapter-$VERSION.jar

rm -rf $BUILD/*
echo "build cleared"



cp -a $SKELETON $BUILD/$BUILD_SKELETON
echo "added skeleton $BUILD_SKELETON"

cp -a $JAR $BUILD/$BUILD_SKELETON/
echo "added jar"

cd $BUILD/
echo "running: zip -r $BUILD_SKELETON.zip $BUILD_SKELETON"
zip -r $BUILD_SKELETON.zip $BUILD_SKELETON
echo "created zip"

