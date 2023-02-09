#!/bin/sh
echo "--------- [STEP 1: Building Gradle plugin] ---------"
./buildGradlePlugin.sh

echo "--------- [STEP 2: Publishing plugin to Gradle portal] ---------"
cd gradle-plugin/devkit
./gradlew publishPlugin
cd -
