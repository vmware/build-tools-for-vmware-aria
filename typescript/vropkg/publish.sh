#!/bin/bash
VERSION=$(cat package.json | jq -r .version)
npm run clean
npm run build
PAYLOAD='{"repoKey":"npm-local","path":"@vmware-pscoe/vropkg/-/@vmware-pscoe/vropkg-'$VERSION'.tgz"}'
curl -X POST -H "Content-Type: application/json" --data $PAYLOAD http://artifactory.pscoe.vmware.com/artifactory/ui/artifactactions/delete
npm publish --registry http://artifactory.pscoe.vmware.com/artifactory/api/npm/npm-local/
