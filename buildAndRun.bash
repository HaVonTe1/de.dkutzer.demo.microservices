#!/bin/bash -
set -o errexit
set -o pipefail
set -o nounset
#set -x

pushd developer-spring
./gradlew build
popd
pushd developer-quarkus
./gradlew build
popd
pushd issues-spring
./gradlew build
popd
pushd planning-spring
./gradlew build
popd
pushd planning-quarkus
./gradlew build
popd
pushd gateway
./gradlew build
popd
pushd sba-server
./gradlew build
popd
docker-compose build
docker-compose --compatibility up -d
