package demo.acme.keycloak;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import demo.acme.keycloak.KeycloakTestSupport.UserRef;
import demo.acme.keycloak.oidc.AgeInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.keycloak.TokenVerifier;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.token.TokenService;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.IDToken;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static demo.acme.keycloak.KeycloakTestSupport.ADMIN_CLI;
import static demo.acme.keycloak.KeycloakTestSupport.MASTER_REALM;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class AcmeKeycloakIntegrationTest {

    public static final String ACME_REALM = "acme";

    public static final String TEST_CLIENT = "test-client";

    public static final String TEST_USER_PASSWORD = "test";

    public static final String REALM_IMPORT_FILE = "acme-realm.json";

    public static KeycloakContainer keycloak;

    static boolean keycloakLocal = false;

    @BeforeAll
    public static void beforeAll() throws Exception {

        // use the previously copied realm file
        if (!Path.of("target/classes/" + REALM_IMPORT_FILE).toFile().exists()) {
            Files.copy(Path.of("../imex/" + REALM_IMPORT_FILE), Path.of("target/classes/" + REALM_IMPORT_FILE));
        }

        if (!Path.of("target/classes/cli/onstart-0001-init.cli").toFile().exists()) {
            Path targetFile = Path.of("target/classes/cli/onstart-0001-init.cli");
            targetFile.getParent().toFile().mkdirs();
            Files.copy(Path.of("../cli/onstart-0001-init.cli"), targetFile);
        }

        // TODO link theme folder

        keycloak = KeycloakTestSupport.createKeycloakContainer(keycloakLocal, REALM_IMPORT_FILE);

        keycloak.withReuse(true);
        keycloak.start();
        keycloak.followOutput(new Slf4jLogConsumer(log));
    }

    @AfterAll
    public static void afterAll() {
        if (keycloak != null) {
            keycloak.stop();
        }
    }

    @Test
    public void ageInfoMapperShouldAddAgeClassClaim() throws Exception {

        Keycloak keycloakAdminClient = Keycloak.getInstance(keycloak.getAuthServerUrl(), MASTER_REALM,
                keycloak.getAdminUsername(), keycloak.getAdminPassword(), ADMIN_CLI);

        RealmResource acmeRealm = keycloakAdminClient.realm(ACME_REALM);

        UserRef user22Years = KeycloakTestSupport.createOrUpdateTestUser(acmeRealm, "test-user-age22", TEST_USER_PASSWORD, user -> {
            user.setFirstName("Firstname");
            user.setLastName("Lastname");
            user.setAttributes(ImmutableMap.of("birthdate", List.of(LocalDate.now().minusYears(22).toString())));
        });

        TokenService tokenService = KeycloakTestSupport.getTokenService(keycloak);

        AccessTokenResponse accessTokenResponse = tokenService.grantToken(ACME_REALM, new Form()
                .param("grant_type", "password")
                .param("username", user22Years.getUsername())
                .param("password", TEST_USER_PASSWORD)
                .param("client_id", TEST_CLIENT)
                .param("scope", "openid acme.profile acme.ageinfo")
                .asMap());

//            System.out.println("Token: " + accessTokenResponse.getToken());

        // parse the received id-token
        TokenVerifier<IDToken> verifier = TokenVerifier.create(accessTokenResponse.getIdToken(), IDToken.class);
        verifier.parse();

        // check for the custom claim
        IDToken accessToken = verifier.getToken();
        String ageInfoClaim = (String) accessToken.getOtherClaims().get(AgeInfoMapper.AGE_CLASS_CLAIM);

        assertThat(ageInfoClaim).isNotNull();
        assertThat(ageInfoClaim).isEqualTo("over21");
    }

    @Test
    public void pingResourceShouldBeAccessibleForUser() {

        TokenService tokenService = KeycloakTestSupport.getTokenService(keycloak);

        AccessTokenResponse accessTokenResponse = tokenService.grantToken(ACME_REALM, new Form()
                .param("grant_type", "password")
                .param("username", "tester")
                .param("password", TEST_USER_PASSWORD)
                .param("client_id", TEST_CLIENT)
                .param("scope", "openid acme.profile acme.api")
                .asMap());

        String accessToken = accessTokenResponse.getToken();
        System.out.println("Token: " + accessToken);

        AcmeResources acmeResources = KeycloakTestSupport.getResteasyWebTarget(keycloak).proxy(AcmeResources.class);
        Map<String, Object> response = acmeResources.ping(ACME_REALM, "Bearer " + accessToken);
        System.out.println(response);

        assertThat(response).isNotNull();
        assertThat(response.get("user")).isEqualTo("tester");
    }


    interface AcmeResources {

        @GET
        @Consumes(MediaType.APPLICATION_JSON)
        @javax.ws.rs.Path("/realms/{realm}/acme-resources/ping")
        Map<String, Object> ping(@PathParam("realm") String realm, @HeaderParam("Authorization") String token);
    }
}
