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


# Scratch Notes

## Prepare Scratch Keycloak

```
cd scratch
cp ~/Downloads/keycloak-12.0.4.tar.gz .
tar xzf keycloak-12.0.4.tar.gz
keycloak-12.0.4/bin/add-user-keycloak.sh --user admin --password admin
```

## Start Scratch Keycloak
```
cd scratch/keycloak-12.0.4
bin/standalone.sh --debug -Dwildfly.statistics-enabled=true -c standalone.xml
```

## Deploy extensions
```
cp acme-extensions/target/acme-extensions-1.0.0-SNAPSHOT.jar scratch/keycloak-12.0.4/standalone/deployments
```

## Undeploy extensions
```
rm scratch/keycloak-12.0.4/standalone/deployments/*.jar
```


Scratch
---

