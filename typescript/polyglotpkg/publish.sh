#!/bin/bash
VERSION=$(cat package.json | jq -r .version)
npm run clean
npm run test
PAYLOAD='{"repoKey":"npm-local","path":"@vmware-pscoe/polyglotpkg/-/@vmware-pscoe/polyglotpkg-'$VERSION'.tgz"}'
curl -X POST -H "Content-Type: application/json" --data $PAYLOAD http://artifactory.pscoe.vmware.com/artifactory/ui/artifactactions/delete
npm publish --registry http://artifactory.pscoe.vmware.com/artifactory/api/npm/npm-local/
