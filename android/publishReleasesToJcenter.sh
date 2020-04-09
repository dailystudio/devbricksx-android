./gradlew clean build
./gradlew publishToMavenLocal
./gradlew bintrayUpload -PdryRun=false