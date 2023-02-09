#!/bin/sh

PLUGIN_DIR="gradle-plugin/devkit"

VERSION_PROPERTIES=version.properties
DEP_VERSIONS_PROPERTIES=dep_versions.properties

function getProperty {
  PROP_FILE=$1
  PROP_KEY=$2
  PROP_VALUE=`cat $PROP_FILE | grep "$PROP_KEY" | cut -d'=' -f2`
  echo $PROP_VALUE
}

major=$(getProperty $VERSION_PROPERTIES "major")
minor=$(getProperty $VERSION_PROPERTIES "minor")
patch=$(getProperty $VERSION_PROPERTIES "patch")

version="${major}.${minor}.${patch}"

agp_version=$(getProperty $DEP_VERSIONS_PROPERTIES "AGP")
ksp_version=$(getProperty $DEP_VERSIONS_PROPERTIES "KSP")
kotlin_version=$(getProperty $DEP_VERSIONS_PROPERTIES "KOTLIN")

echo "Retrieving version from $VERSION_PROPERTIES: [${version}]"
echo "Retrieving Android Gradle Plugin versions from $DEP_VERSIONS_PROPERTIES: [${agp_version}]"
echo "Retrieving Kotlin versions from $DEP_VERSIONS_PROPERTIES: [${kotlin_version}]"
echo "Retrieving KSP versions from $DEP_VERSIONS_PROPERTIES: [${ksp_version}]"

files="
  ${PLUGIN_DIR}/build.gradle.kts
"

for f in ${files}; do
  if [ ! -f ${f} ]; then
    continue;
  fi

  echo "updating version in file [${f}]..."
  sed -i "" "s/^version\ =\ \".*\"/version\ =\ \"${version}\"/g" ${f}

  echo "updating dependencies version in file [${f}]..."
  sed -i "" "s/\"com.android.tools.build:gradle:.*\"/\"com.android.tools.build:gradle:${agp_version}\"/g" ${f}
  sed -i "" "s/\"com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:.*\"/\"com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:${ksp_version}\"/g" ${f}
  sed -i "" "s/kotlin(\"jvm\") version \".*\"/kotlin(\"jvm\") version \"${kotlin_version}\"/g" ${f}

done

cd ${PLUGIN_DIR}
./gradlew publishAllPublicationsToLocalRepoRepository
cd -
