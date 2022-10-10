#!/bin/sh

DEP_VERSIONS_PROPERTIES=dep_versions.properties

function getProperty {
   PROP_KEY=$1
   PROP_VALUE=`cat $DEP_VERSIONS_PROPERTIES | grep "$PROP_KEY" | cut -d'=' -f2`
   echo $PROP_VALUE
}

roomVersion=$(getProperty "ROOM")

echo "Updating Room version to [${roomVersion}]"

files=`find . -name "*.md"`

for f in ${files}; do
  if [ ! -f ${f} ]; then
    continue;
  fi

  echo "updating version in file [${f}]..."
  sed -i "" "s/\"androidx\.room:room-compiler:.*\"/\"androidx\.room:room-compiler:${roomVersion}\"/g" ${f}
#  sed -i "" "s/download.svg\?version=[0-9]\.[0-9]\.[0-9]/download.svg\?version=${version}/g" ${f}
#  sed -i "" "s/maven\/devbricksx\/[0-9]\.[0-9]\.[0-9]\//maven\/devbricksx\/${version}\//g" ${f}
done

