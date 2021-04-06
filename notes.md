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

`AcmeAuditListener.java`
```java
package demo.acme.keycloak.audit;

import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;

public class AcmeAuditListener implements EventListenerProvider {

    public static final String ID = "acme-audit-listener";

    private static final Logger LOG = Logger.getLogger(AcmeAuditListener.class);

    @Override
    public void onEvent(Event event) {
        // called for each User-Event
        LOG.infof("audit userEvent=%s type=%s realm=%suserId=%s",
                event, event.getType(), event.getRealmId(), event.getUserId());
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        // called for each AdminEvent
        LOG.infof("audit adminEvent=%s type=%s resourceType=%s resourcePath=%s includeRepresentation=%s",
                event, event.getOperationType(), event.getResourceType(), event.getResourcePath(), includeRepresentation);
    }

    @Override
    public void close() {
        // NOOP
    }
}
```

`AcmeAuditListenerFactory.java`
```java
package demo.acme.keycloak.audit;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

// src/main/resources/META-INF/servcies -> org.keycloak.events.EventListenerProviderFactory
public class AcmeAuditListenerFactory implements EventListenerProviderFactory {

    private static final AcmeAuditListener INSTANCE = new AcmeAuditListener();

    @Override
    public String getId() {
        return AcmeAuditListener.ID;
    }

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        // return singleton instance, or create new AcmeAuditListener(session) if you need KeycloakSession access.
        return INSTANCE;
    }

    @Override
    public void init(Config.Scope config) {
        // we could read settings from the provider config in standalone(-ha).xml
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // we could init our provider with information from other providers
    }

    @Override
    public void close() {
        // close resources if providers
    }
}
```


`org.keycloak.events.EventListenerProviderFactory`
```
demo.acme.keycloak.audit.AcmeAuditListenerFactory
```

Test for Audit Listener in `AcmeKeycloakIntegrationTest.java`
```java
    @Test
    public void auditListenerShouldPrintLogMessage() throws Exception{

        Assumptions.assumeTrue(!keycloakLocal);

        ToStringConsumer consumer = new ToStringConsumer();
        keycloak.followOutput(consumer);

        TokenService tokenService = KeycloakTestSupport.getTokenService(keycloak);

        // trigger user login via ROPC
        AccessTokenResponse accessTokenResponse = tokenService.grantToken(ACME_REALM, new Form()
                .param("grant_type", "password")
                .param("username", "tester")
                .param("password", TEST_USER_PASSWORD)
                .param("client_id", TEST_CLIENT)
                .param("scope", "openid acme.profile acme.ageinfo")
                .asMap());

        // Allow the container log to flush
        TimeUnit.MILLISECONDS.sleep(750);

        assertThat(consumer.toUtf8String()).contains("audit userEvent");
    }

```
