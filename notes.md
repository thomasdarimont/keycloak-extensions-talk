Notes
---

```
docker run \
  --name keycloak-extdev \
  --rm \
  -e KEYCLOAK_USER=admin \
  -e KEYCLOAK_PASSWORD=admin \
  -e KEYCLOAK_THEME_CACHING=false \
  -e KEYCLOAK_THEME_TEMPLATE_CACHING=false \
  -v $PWD/acme-extensions/target/classes:/opt/jboss/keycloak/standalone/deployments/acme-extensions.jar:z \
  -v $PWD/acme-themes/target/classes/theme/acme:/opt/jboss/keycloak/themes/acme:z \
  -v $PWD/testrun/data:/opt/jboss/keycloak/standalone/data:z \
  -v $PWD/cli:/opt/jboss/startup-scripts:z \
  -v $PWD/imex:/opt/jboss/imex:z \
    -p 8080:8080 \
  -p 8787:8787 \
  quay.io/keycloak/keycloak:12.0.4 --debug '*:8787' --server-config standalone.xml
```

```
mvn clean verify io.fabric8:docker-maven-plugin:build -Pwith-integration-tests

mvn clean verify -Pwith-integration-tests
```