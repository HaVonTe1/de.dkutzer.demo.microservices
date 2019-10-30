#!/usr/bin/env bash

# run as root
echo "before"
cat /etc/hosts

function modHostsForService {
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

modHostsForService buggy-mongo
modHostsForService buggy-rabbitmq
modHostsForService buggy-developer-service
modHostsForService buggy-issues-service
modHostsForService buggy-planning-service
modHostsForService buggy-gateway-service

echo "after"

cat /etc/hosts


