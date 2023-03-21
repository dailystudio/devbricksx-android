#!/bin/sh

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

ksp_version=$(getProperty $DEP_VERSIONS_PROPERTIES "KSP")
devkit_version="${version}-$(getProperty $DEP_VERSIONS_PROPERTIES "DEVKIT")"

echo "Retrieving version from $VERSION_PROPERTIES: [${version}]"
echo "Retrieving KSP versions from $DEP_VERSIONS_PROPERTIES: [${ksp_version}]"

files="
  README.md
  devbricksx-java-annotations/README.md
  devbricksx-kotlin-annotations/README.md
  docs/sample_notebook_tutorial_01.md
"

for f in ${files}; do
  if [ ! -f ${f} ]; then
    continue;
  fi

  echo "updating version in file [${f}]..."
  sed -i "" "s/devbricksx_version\ =\ \".*\"/devbricksx_version\ =\ \"${version}\"/g" ${f}
  sed -i "" "s/download.svg\?version=[0-9]\.[0-9]\.[0-9]/download.svg\?version=${version}/g" ${f}
  sed -i "" "s/maven\/devbricksx\/[0-9]\.[0-9]\.[0-9]\//maven\/devbricksx\/${version}\//g" ${f}

  echo "updating dependencies version in file [${f}]..."
  sed -i "" "s/ksp_version\ =\ \".*\"/ksp_version\ =\ \"${ksp_version}\"/g" ${f}
  sed -i "" "s/devkit_version\ =\ \".*\"/devkit_version\ =\ \"${devkit_version}\"/g" ${f}

done

