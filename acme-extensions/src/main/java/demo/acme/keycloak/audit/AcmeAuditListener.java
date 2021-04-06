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
