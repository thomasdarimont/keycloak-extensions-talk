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
