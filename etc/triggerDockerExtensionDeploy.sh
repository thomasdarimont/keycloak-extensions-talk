#!/usr/bin/env bash

set -eou pipefail

docker-compose exec -T keycloak \
  touch /opt/jboss/keycloak/standalone/deployments/acme-extensions.jar.dodeploy