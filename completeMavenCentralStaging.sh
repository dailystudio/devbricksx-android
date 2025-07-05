#!/bin/sh

NEXUS_USER=$(grep '^NEXUS_USER=' private.properties | cut -d'=' -f2-)
NEXUS_PASS=$(grep '^NEXUS_PASS=' private.properties | cut -d'=' -f2-)
OSSRH_API_URL="https://ossrh-staging-api.central.sonatype.com"

ENDPOINT_SEARCH="manual/search/repositories"
ENDPOINT_UPLOAD="manual/upload/repository"

AUTH="${NEXUS_USER}:${NEXUS_PASS}"
ENCODED=$(printf "%s" "$AUTH" | base64)

echo "Base64: $ENCODED"
RESPONSE=$(curl -s -H "Authorization: Bearer ${ENCODED}" \
  "${OSSRH_API_URL}/${ENDPOINT_SEARCH}")

KEY=$(echo "$RESPONSE" | sed 's/},{/}\n{/g' | grep '"state":"open"' | sed -n 's/.*"key":"\([^"]*\)".*/\1/p')

echo "Repository Key: $KEY"

if [ -z "$KEY" ]; then
  echo "ERROR：No 'open' repository found。"
  echo "RESP: $RESPONSE"
  exit 1
fi

curl -H "Authorization: Bearer ${ENCODED}" \
       -i -X POST "${OSSRH_API_URL}/${ENDPOINT_UPLOAD}/${KEY}" -v