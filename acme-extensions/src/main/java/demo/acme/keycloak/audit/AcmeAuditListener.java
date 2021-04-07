package demo.acme.keycloak.audit;

import lombok.extern.jbosslog.JBossLog;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;

@JBossLog
public class AcmeAuditListener implements EventListenerProvider {

    public static final String ID = "acme-audit-listener";

    @Override
    public void onEvent(Event event) {
        // called for each User-Event
        log.infof("audit userEvent=%s type=%s realm=%suserId=%s",
                event, event.getType(), event.getRealmId(), event.getUserId());
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        // called for each AdminEvent
        log.infof("audit adminEvent=%s type=%s resourceType=%s resourcePath=%s includeRepresentation=%s",
                event, event.getOperationType(), event.getResourceType(), event.getResourcePath(), includeRepresentation);
    }

    @Override
    public void close() {
        // NOOP
    }
}
