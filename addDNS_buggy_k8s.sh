#!/bin/bash -
#set -o errexit
set -o pipefail
set -o nounset
set -x

# run as root
echo "before"
cat /etc/hosts

TMPHOSTS=/tmp/hosts_"$(date +%s)"
cp /etc/hosts $TMPHOSTS

function modHostsForSingleService() {
  local IP
  IP=$(kubectl get svc $1 -n buggy --output=jsonpath={.status.loadBalancer.ingress..ip})
  if [[ -z "$IP" ]]; then
    sudo sed -i "/$1/d" "$TMPHOSTS"
  else
    if grep -q "$1" "$TMPHOSTS"; then
      sudo sed -ri "s/^ *[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+( +$1)/$IP\1/" "$TMPHOSTS"
    else
      sudo echo "$IP" "$1" >>"$TMPHOSTS"
    fi
  fi
}
modHostsForSingleService buggy-mongodb
modHostsForSingleService buggy-zipkin
modHostsForSingleService buggy-sba-service
modHostsForSingleService buggy-keycloak
modHostsForSingleService buggy-postgres
modHostsForSingleService buggy-elasticsearch
modHostsForSingleService buggy-kibana
modHostsForSingleService buggy-grafana
modHostsForSingleService buggy-prometheus
modHostsForSingleService buggy-prometheus-pushgateway
#modHostsForSingleService buggy-zookeeper
modHostsForSingleService buggy-kafka
modHostsForSingleService buggy-kafdrop

#modHostsForScaledService buggy-developer-service
#modHostsForScaledService buggy-issues-service
#modHostsForScaledService buggy-planning-service
#modHostsForScaledService buggy-gateway-service

echo "after"

cat $TMPHOSTS

echo "now cp %TMPHOSTS to /etc/hosts"