package demo.acme.keycloak.api;

import com.google.auto.service.AutoService;
import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

@AutoService(RealmResourceProviderFactory.class)
public class AcmeResourceProviderFactory implements RealmResourceProviderFactory {

    @Override
    public String getId() {
        return AcmeResourceProvider.ID;
    }

    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        return new AcmeResourceProvider(session);
    }

    @Override
    public void init(Config.Scope config) {
        // NOOP
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // NOOP
    }

    @Override
    public void close() {
        // NOOP
    }
}
