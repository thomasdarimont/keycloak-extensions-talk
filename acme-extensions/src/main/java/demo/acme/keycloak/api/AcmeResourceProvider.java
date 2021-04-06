package demo.acme.keycloak.api;

import lombok.RequiredArgsConstructor;
import org.keycloak.authorization.util.Tokens;
import org.keycloak.models.KeycloakSession;
import org.keycloak.representations.AccessToken;
import org.keycloak.services.resource.RealmResourceProvider;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

@RequiredArgsConstructor
public class AcmeResourceProvider implements RealmResourceProvider {

    public static final String ID = "acme-resources";

    private final KeycloakSession session;

    @Override
    public Object getResource() {

        AccessToken accessToken = Tokens.getAccessToken(session);

        // check access
//        if (accessToken == null) {
//            throw new NotAuthorizedException("Invalid Token", Response.status(UNAUTHORIZED).build());
//        } else if (!hasScope("acme.api", accessToken.getScope())) {
//            throw new ForbiddenException("No Access", Response.status(FORBIDDEN).build());
//        }

        return new AcmeResource(session, accessToken);
    }

    private boolean hasScope(String requiredScope, String scope) {

        if (scope == null || scope.isEmpty()) {
            return false;
        }

        String[] scopeEntries = scope.split(" ");
        for (String entry : scopeEntries) {
            if (entry.equals(requiredScope)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void close() {
        // NOOP
    }
}
