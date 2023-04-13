#!/bin/bash

if [ $# -lt 1 ]; then
  echo "Error: missing relative file path!"
  exit 1
fi

echo "Using relative file path: $1"

docker run \
	--rm \
	-e RUN_LOCAL=true \
	-e USE_FIND_ALGORITHM=true \
	-e VALIDATE_JAVA=true  \
	-e VALIDATE_JAVASCRIPT_ES=true \
	-e VALIDATE_JAVASCRIPT_STANDARD=true \
	-e VALIDATE_MARKDOWN=true \
	-v $(pwd)/$1:/tmp/lint/$1 \
	-v $(pwd)/.github/linters:/tmp/lint/.github/linters  \
	github/super-linter
