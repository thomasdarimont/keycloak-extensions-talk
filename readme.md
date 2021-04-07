Easy Keycloak Extension Development
---

This repository contains the code & slides of my talk `Keycloak Extension Development - Overview & Best Practices`.

This example shows how to develop and deploy a set of Keycloak extensions, custom themens and configuration to a Keycloak docker container.
In addition to that, the example also shows how to write integration tests via [Keycloak-Testcontainers](https://github.com/dasniko/testcontainers-keycloak) and how to package all extensions and themes as a 
custom docker image.

The example contains the following Keycloak extensions:
- OIDC ProtocolMapper to compute age-class information based on a `birthdate` user attribute: `AgeInfoMapper`
- Audit Event Listener sketch to forward certain Keycloak user and admin events to an external service: `AcmeAuditListener`
- Custom REST Endpoint the can expose additional custom APIs: `AcmeResource`

# Some Highlights
- Support for deploying extensions to running Keycloak container
- Support for instant reloading of theme and extension code changes
- Support Keycloak configuration customization via CLI scripts
- Examples for Integration Tests with Keycloak-Testcontainers

# Build
The example can be build with the following maven command: 
```
mvn clean verify
```

## Build with Integration Tests
The example can be build with integration tests by running the following maven command:
```
mvn clean verify -Pwith-integration-tests
```

## Build Docker Image
To build a custom Keycloak Docker image that contains the custom extensions and themes, you can run the following command:
```
mvn clean verify -Pwith-integration-tests io.fabric8:docker-maven-plugin:build
```

# Run

## Start Keycloak container with docker-compose

Keycloak will be available on http://localhost:8080/auth.
The default Keycloak admin username is `admin` with password `admin`.

You can start the Keycloak container via:
```
docker-compose up
```

## Run custom Docker Image
The custom docker image created during the build can be stared with the following command:
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

# Example environment

The example environment contains a Keycloak realm named `acme`, which contains a simple demo application as well as a test user.
The test user has the username `tester` and password `test`.

### Example App

A simple demo app can be used to show information from the Access-Token, ID-Token and UserInfo endpoint provided by Keycloak.

The demo app can be started by running `etc/runDemoApp.sh` and will be accessible via http://localhost:4000.

# Scripts

## Manually trigger Extension Deployment
```
etc/triggerDockerExtensionDeploy.sh
```

## Exporting the 'Acme' Realm
```
etc/exportRealm.sh
```
