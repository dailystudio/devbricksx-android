./gradlew clean build
./gradlew publishToMavenLocal
./gradlew bintrayUpload -PdryRun=false

./updateVersionInDocs.sh
./updateRoomVersionInDocs.sh
