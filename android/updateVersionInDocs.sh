#!/bin/sh

VERSION_PROPERTIES=version.properties
TEMP_FILE=file.tmp

function getProperty {
   PROP_KEY=$1
   PROP_VALUE=`cat $VERSION_PROPERTIES | grep "$PROP_KEY" | cut -d'=' -f2`
   echo $PROP_VALUE
}

major=$(getProperty "major")
minor=$(getProperty "minor")
patch=$(getProperty "patch")

version="${major}.${minor}.${patch}"

echo "Retrieving version from $VERSION_PROPERTIES: [${version}]"

files="
  README.md
  devbricksx-java-annotations/README.md
"

for f in ${files}; do
  if [ ! -f ${f} ]; then
    continue;
  fi

  echo "updating version in file [${f}]..."
  sed "s/devbricksx_version\ =\ \".*\"/devbricksx_version\ =\ \"${version}\"/g" ${f} >> ${TEMP_FILE}
  mv ${TEMP_FILE} ${f}
done

