package demo.acme.keycloak.api;

import org.keycloak.models.KeycloakSession;
import org.keycloak.representations.AccessToken;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * {@code
 * curl -v http://localhost:8080/auth/realms/acme/acme-resources/ping | jq -C .
 * }
 */
public class AcmeResource {

    private final KeycloakSession session;
    private final AccessToken token;

    public AcmeResource(KeycloakSession session, AccessToken accessToken) {
        this.session = session;
        this.token = accessToken;
    }

    @GET
    @Path("ping")
    @Produces(MediaType.APPLICATION_JSON)
    public Response ping() {

        Map<String, Object> payload = new HashMap<>();
        payload.put("realm", session.getContext().getRealm().getName());
        payload.put("user", token == null ? "anonymous" : token.getPreferredUsername());
        payload.put("timestamp", System.currentTimeMillis());

        return Response.ok(payload).build();
    }
}
