#!/bin/bash

PLUGIN_DIR="gradle-plugin/devkit"

VERSION_PROPERTIES=version.properties
DEP_VERSIONS_PROPERTIES=dep_versions.properties

function getProperty {
  PROP_FILE=$1
  PROP_KEY=$2
  PROP_VALUE=`cat $PROP_FILE | grep -w "$PROP_KEY" | cut -d'=' -f2`
  echo $PROP_VALUE
}

major=$(getProperty $VERSION_PROPERTIES "major")
minor=$(getProperty $VERSION_PROPERTIES "minor")
patch=$(getProperty $VERSION_PROPERTIES "patch")

version="${major}.${minor}.${patch}"

agp_version=$(getProperty $DEP_VERSIONS_PROPERTIES "AGP")
plugin_version="${version}-$(getProperty $DEP_VERSIONS_PROPERTIES "DEVKIT")"
ksp_version=$(getProperty $DEP_VERSIONS_PROPERTIES "KSP")
kotlin_version=$(getProperty $DEP_VERSIONS_PROPERTIES "KOTLIN")
kotlin_compiler_ext_version=$(getProperty $DEP_VERSIONS_PROPERTIES "KOTLIN_COMPILER_EXT")
room_version=$(getProperty $DEP_VERSIONS_PROPERTIES "ROOM")

echo "--------------------------------------"
echo "Dependencies for plugin [${plugin_version}]"
echo "--------------------------------------"
echo "Retrieving DevBricksX version from $VERSION_PROPERTIES: [${version}]"
echo "Retrieving Android Gradle Plugin versions from $DEP_VERSIONS_PROPERTIES: [${agp_version}]"
echo "Retrieving Kotlin versions from $DEP_VERSIONS_PROPERTIES: [${kotlin_version}]"
echo "Retrieving Kotlin Compiler Extension versions from $DEP_VERSIONS_PROPERTIES: [${kotlin_compiler_ext_version}]"
echo "Retrieving KSP versions from $DEP_VERSIONS_PROPERTIES: [${ksp_version}]"
echo "Retrieving Room versions from $DEP_VERSIONS_PROPERTIES: [${room_version}]"

echo
files="
  ${PLUGIN_DIR}/build.gradle.kts
  ${PLUGIN_DIR}/src/main/kotlin/com/dailystudio/devbricksx/Dependencies.kt
"

for f in ${files}; do
  if [ ! -f ${f} ]; then
    continue;
  fi

  echo "Updating version in file [${f}]..."
  sed -i "" "s/^version\ =\ \".*\"/version\ =\ \"${plugin_version}\"/g" ${f}
  sed -i "" "s/DEV_BRICKS_X_VERSION\ =\ \".*\"/DEV_BRICKS_X_VERSION\ =\ \"${version}\"/g" ${f}

  echo "Updating dependencies version in file [${f}]..."
  sed -i "" "s/\"com.android.tools.build:gradle:.*\"/\"com.android.tools.build:gradle:${agp_version}\"/g" ${f}
  sed -i "" "s/\"com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:.*\"/\"com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:${ksp_version}\"/g" ${f}
  sed -i "" "s/kotlin(\"jvm\") version \".*\"/kotlin(\"jvm\") version \"${kotlin_version}\"/g" ${f}
  sed -i "" "s/ROOM_VERSION\ =\ \".*\"/ROOM_VERSION\ =\ \"${room_version}\"/g" ${f}
  sed -i "" "s/KOTLIN_COMPILER_EXT_VERSION\ =\ \".*\"/KOTLIN_COMPILER_EXT_VERSION\ =\ \"${kotlin_compiler_ext_version}\"/g" ${f}

done

cd ${PLUGIN_DIR}
./gradlew publishAllPublicationsToLocalRepoRepository
cd -
