#!/usr/bin/env bash

set -eou pipefail

docker-compose exec keycloak \
  /opt/jboss/keycloak/bin/standalone.sh -c standalone.xml \
  -Djboss.socket.binding.port-offset=10000 \
  -Dkeycloak.migration.action=export \
  -Dkeycloak.migration.file=/opt/jboss/imex/acme-realm.json \
  -Dkeycloak.migration.provider=singleFile \
  -Dkeycloak.migration.realmName=acme
