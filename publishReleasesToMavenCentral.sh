#!/bin/sh
artifactsToPublish=(
  "devbricksx"
  "devbricksx-annotations"
  "devbricksx-compiler"
  "devbricksx-compose"
  "devbricksx-audio"
  "devbricksx-camera"
  "devbricksx-network"
  "devbricksx-music"
)

echo "--------- [STEP 1: Building artifacts] ---------"
for artifact in ${artifactsToPublish[@]}; do
  task=":${artifact}:assemble"
  echo "Processing artifact: [${artifact}] ..."
  ./gradlew ${task} > /dev/null
done

echo "--------- [STEP 2: Publishing artifacts] ---------"
for artifact in ${artifactsToPublish[@]}; do
  task=":${artifact}:publishToMavenCentral"
  echo "Uploading artifact: [${artifact}] ..."
  ./gradlew ${task} --no-configuration-cache > /dev/null
done

#./completeMavenCentralStaging.sh

./updateVersionInDocs.sh
./updateRoomVersionInDocs.sh
