#!/bin/bash -
#set -o errexit
set -o pipefail
set -o nounset
#set -x

# run as root
echo "before"
cat /private/etc/hosts

function modHostsForSingleService() {
  local IP
  IP=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' "$1")
  if [[ -z "$IP" ]]; then
    sed -i "/$1/d" /etc/hosts
  else
    if grep -q "$1" "/private/etc/hosts"; then
      sed -ri "s/^ *[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+( +$1)/$IP\1/" /etc/hosts
    else
      echo "$IP" "$1" >>/private/etc/hosts
    fi
  fi
}

function modHostsForScaledService() {

  local instances
  local IP
  local containerName
  instances=("$(docker container ls -f "NAME=$1" -q)")
  for instance in "${instances[@]}"; do
    IP=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' "$instance")
    containerName=$(docker inspect --format='{{.Name}}' "${instance}")
    containerName=$("${containerName//_/-}")
    if [[ -z "$IP" ]]; then
      sed -i "/$1/d" /etc/hosts
    else
      if grep -q "${containerName:1}" "/private/etc/hosts"; then
        sed -ri "s/^ *[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+( +$1)/$IP\1/" /etc/hosts
      else
        echo "$IP" "${containerName:1}" >>/etc/hosts
      fi
    fi
  done

}

modHostsForSingleService buggy-mongo
modHostsForSingleService buggy-zipkin
modHostsForSingleService buggy-sba-service
modHostsForSingleService buggy-keycloak
modHostsForSingleService buggy-postgres
modHostsForSingleService buggy-elasticsearch
modHostsForSingleService buggy-kibana
modHostsForSingleService buggy-grafana
modHostsForSingleService buggy-prometheus
modHostsForSingleService buggy-prometheus-pushgateway
modHostsForSingleService buggy-kafka
modHostsForSingleService buggy-kafdrop

modHostsForScaledService buggy-developer-service
modHostsForScaledService buggy-issues-service
modHostsForScaledService buggy-planning-service
modHostsForScaledService buggy-gateway-service

echo "after"

cat /private/etc/hosts
