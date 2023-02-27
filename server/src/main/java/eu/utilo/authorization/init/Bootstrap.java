package eu.utilo.authorization.init;

import eu.utilo.authorization.entity.Authority;
import eu.utilo.authorization.entity.OauthClient;
import eu.utilo.authorization.entity.OauthRole;
import eu.utilo.authorization.entity.OauthUser;
import eu.utilo.authorization.repository.ClientRepository;
import eu.utilo.authorization.repository.UserRepository;
import eu.utilo.authorization.services.DefaultRegisteredClientRepositoryImpl;
import eu.utilo.authorization.services.AuthorityService;
import eu.utilo.authorization.services.RoleService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

/**
 * init Funktionenen
 * @author coster
 */
@Component
public class Bootstrap implements
        ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    @Resource
    private ClientRepository clientRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Resource
    private UserRepository userRepository;
    @Autowired
    private AuthorityService privilegeService;
    @Autowired
    private RoleService roleService;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup)
            return;

        // Privilegien anlegen falls nicht vorhanden:
        Authority readPrivilege
                = privilegeService.createPrivilegeIfNotFound(Authority.READ);
        Authority writePrivilege
                = privilegeService.createPrivilegeIfNotFound(Authority.WRITE);
        Authority updatePrivilege
                = privilegeService.createPrivilegeIfNotFound(Authority.UPDATE);
        Authority deletePrivilege
                = privilegeService.createPrivilegeIfNotFound(Authority.DELETE);

        // Rollen anlegen falls nicht vorhanden:
        // User with ROLE_ADMIN role have the authorities to READ,DELETE,WRITE,UPDATE.
        OauthRole roleAdmin = roleService.createRoleIfNotFound(
                OauthRole.ADMIN,
                Arrays.asList(readPrivilege, writePrivilege, updatePrivilege, deletePrivilege)
        );
        // A user with role ROLE_USER has authority to READ only.
        OauthRole roleUser = roleService.createRoleIfNotFound(
                OauthRole.USER,
                Arrays.asList(readPrivilege)
        );
        // User with ROLE_MANAGER can perform READ, WRITE and UPDATE operations.
        OauthRole roleManager = roleService.createRoleIfNotFound(
                OauthRole.MANAGER,
                Arrays.asList(readPrivilege, writePrivilege, updatePrivilege)
        );

        Optional<OauthClient> clientop = clientRepository.findByClientId("utilo-client");
        if (clientop.isEmpty()) {

            /*
             * default RegisteredClient für Administration generieren:
             * org.springframework.security.oauth2.server.authorization.oidc.web.OidcClientRegistrationEndpointFilter
             */
            RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                    .clientId("utilo-client")
                    .clientSecret(passwordEncoder.encode("secret"))
                    .clientIdIssuedAt(Instant.now())
                    .clientSecretExpiresAt(null)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                    .redirectUri("http://127.0.0.1:9010/admin")
                    .redirectUri("https://www.utilo.eu")
                    .scope(OidcScopes.OPENID)
                    .scope(OidcScopes.PROFILE)
                    .scope("message.read")
                    .scope("message.write")
                    .scope("UTILO")
                    .clientSettings(
                            ClientSettings.builder()
                                    .requireAuthorizationConsent(true)
                                    .requireProofKey(false)
                                    .build()
                    ).tokenSettings(
                            TokenSettings.builder()
                                    .accessTokenTimeToLive(Duration.ofMinutes(30))
                                    .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                                    .refreshTokenTimeToLive(Duration.ofDays(30))
                                    .build())
                    .build();

            RegisteredClientRepository registeredClientRepository
                    = new DefaultRegisteredClientRepositoryImpl(clientRepository);
            RegisteredClient byClientId = registeredClientRepository.findByClientId("utilo-client");
            if (byClientId != null) {
                throw new RuntimeException("RegisteredClient error.");
            }
            registeredClientRepository.save(registeredClient);

        }

        Optional<OauthUser> userop = userRepository.findUserByUsername("utilo");
        if (userop.isEmpty()) {

            System.out.println("Erzeuge utilo USER ======================================");

            OauthUser utiloUser = new OauthUser();
            utiloUser.setUsername("utilo");
            utiloUser.setPassword(passwordEncoder.encode("utilo")); // TODO: Passwort setzen!
            utiloUser.setRoles(Arrays.asList(roleAdmin));
            utiloUser.setEnabled(true);
            utiloUser.setAccountNonExpired(true);
            utiloUser.setCredentialsNonExpired(true);
            utiloUser.setAccountNonLocked(true);
            utiloUser.setFirstName("Christian");
            utiloUser.setLastName("Osterrieder-Schlick");
            utiloUser.setEmail("office@utilo.eu");
            userRepository.save(utiloUser);
            System.out.println("utilo USER angelegt ======================================");

        }

        alreadySetup = true;

    }

}
