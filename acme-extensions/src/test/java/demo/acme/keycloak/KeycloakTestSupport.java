package demo.acme.keycloak;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.token.TokenService;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.testcontainers.utility.MountableFile;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public class KeycloakTestSupport {

    public static KeycloakContainer createKeycloakContainer(boolean keycloakLocal, String realmImportFileName) {

        if (keycloakLocal) {
            return new LocalKeycloak("http://localhost:8080/auth", "admin", "admin");
        }

        return new KeycloakContainer("quay.io/keycloak/keycloak:12.0.4")
//                .withRealmImportFile(REALM_IMPORT_FILE) // broken for integration-tests outside of the IDE
                .withCopyFileToContainer(MountableFile.forHostPath(Path.of("target/classes/" + realmImportFileName)), "/tmp/" + realmImportFileName)
                .withEnv("KEYCLOAK_IMPORT", "/tmp/" + realmImportFileName)
                .withExtensionClassesFrom("target/classes");
    }

    public static TokenService getTokenService(KeycloakContainer keycloak) {
        return getResteasyWebTarget(keycloak).proxy(TokenService.class);
    }

    public static ResteasyWebTarget getResteasyWebTarget(KeycloakContainer keycloak) {
        ResteasyClient client = new ResteasyClientBuilder().build();
        return client.target(UriBuilder.fromPath(keycloak.getAuthServerUrl()));
    }

    public static UserRef createOrUpdateTestUser(RealmResource realm, String username, String password, Consumer<UserRepresentation> adjuster) {

        List<UserRepresentation> existingUsers = realm.users().search(username, true);

        String userId;
        UserRepresentation userRep;

        if (existingUsers.isEmpty()) {
            userRep = new UserRepresentation();
            userRep.setUsername(username);
            userRep.setEnabled(true);
            adjuster.accept(userRep);
            try (Response response = realm.users().create(userRep)) {
                userId = CreatedResponseUtil.getCreatedId(response);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } else {
            userRep = existingUsers.get(0);
            adjuster.accept(userRep);
            userId = userRep.getId();
        }

        CredentialRepresentation passwordRep = new CredentialRepresentation();
        passwordRep.setType(CredentialRepresentation.PASSWORD);
        passwordRep.setValue(password);
        realm.users().get(userId).resetPassword(passwordRep);

        return new UserRef(userId, username);
    }

    @Data
    @AllArgsConstructor
    public static class UserRef {
        String userId;
        String username;
    }


    @Data
    @AllArgsConstructor
    public static class LocalKeycloak extends KeycloakContainer {

        String authServerUrl;
        String adminUsername;
        String adminPassword;

        public void start() {
            // NOOP
        }
    }
}
