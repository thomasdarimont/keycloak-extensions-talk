ARG KEYCLOAK_VERSION=12.0.4
FROM quay.io/keycloak/keycloak:$KEYCLOAK_VERSION

COPY --chown=jboss:jboss maven/cli/ /opt/jboss/startup-scripts
COPY --chown=jboss:jboss maven/acme-extensions/ /opt/jboss/keycloak/standalone/deployments
COPY --chown=jboss:jboss maven/acme-theme /opt/jboss/keycloak/themes/acme

