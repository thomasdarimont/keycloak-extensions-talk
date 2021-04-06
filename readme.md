Developing Keycloak Extensions
---

# Build

```
mvn clean verify
```

## Build Docker Image
```
mvn clean package -DskipTests io.fabric8:docker-maven-plugin:build
```

## Build with Integration Tests
```
mvn clean verify -Pwith-integration-tests
```

# Run

## Run with docker-compose

```
docker-compose up
```

## Run example App

```

```

## Run with Docker

```
docker run \
  --name acme-keycloak \
  -e KEYCLOAK_USER=admin \
  -e KEYCLOAK_PASSWORD=admin \
  -e KEYCLOAK_IMPORT=/opt/jboss/imex/acme-realm.json \
  -v $PWD/imex:/opt/jboss/imex:z \
  -it \
  --rm \
  -p 8080:8080 \
  thomasdarimont/acme-keycloak:latest
```

# Misc

## Trigger Extension Deployment
```
etc/triggerDockerExtensionDeploy.sh
```

## Export 'Acme' Realm
```
etc/exportRealm.sh
```