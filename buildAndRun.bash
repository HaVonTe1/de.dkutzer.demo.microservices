#!/bin/bash -
set -o errexit
set -o pipefail
set -o nounset
#set -x

pushd developer
./gradlew build
popd
pushd issues
./gradlew build
popd
pushd planning
./gradlew build
popd
pushd gateway
./gradlew build
popd
docker-compose build
docker-compose up -d
