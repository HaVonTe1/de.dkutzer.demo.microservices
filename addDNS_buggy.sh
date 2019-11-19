#!/bin/bash -
set -o errexit
set -o pipefail
set -o nounset
set -x

# run as root
echo "before"
cat /etc/hosts

function modHostsForSingleService {
  local IP;
  IP=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' "$1")
  if [[ -z "$IP" ]]; then
    sed -i "/$1/d" /etc/hosts
  else
    if grep -q "$1" "/etc/hosts"; then
      sed -ri "s/^ *[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+( +$1)/$IP\1/" /etc/hosts
    else
      echo "$IP" "$1">> /etc/hosts
    fi
  fi
}

function modHostsForScaledService {

  local instances=( $(docker container ls -f "NAME=$1" -q) )
  for instance in ${instances[@]}; do
    local IP=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' "$instance")
    local containerName=$(docker inspect --format='{{.Name}}' ${instance})
    containerName=$(echo "${containerName//_/-}")
    if [[ -z "$IP" ]]; then
      sed -i "/$1/d" /etc/hosts
    else
      if grep -q "${containerName:1}" "/etc/hosts"; then
        sed -ri "s/^ *[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+( +$1)/$IP\1/" /etc/hosts
      else
        echo "$IP" "${containerName:1}">> /etc/hosts
      fi
    fi
  done

}

modHostsForSingleService buggy-mongo
modHostsForSingleService buggy-rabbitmq
modHostsForSingleService buggy-mysql-zipkin
modHostsForSingleService buggy-zipkin
modHostsForSingleService buggy-sba-service
modHostsForSingleService buggy-keycloak
modHostsForSingleService buggy-postgres

modHostsForScaledService buggy-developer-service
modHostsForScaledService buggy-issues-service
modHostsForScaledService buggy-planning-service
modHostsForScaledService buggy-gateway-service

echo "after"

cat /etc/hosts


