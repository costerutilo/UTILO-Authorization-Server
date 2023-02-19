package eu.utilo.authorization.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.utilo.authorization.entity.OauthClient;
import eu.utilo.authorization.entity.OauthUser;
import eu.utilo.authorization.jackson.OauthUserMixin;
import eu.utilo.authorization.jackson.SingletonMapMixin;
import eu.utilo.authorization.repository.ClientRepository;
import eu.utilo.authorization.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;

public class DefaultRegisteredClientRepositoryImpl implements RegisteredClientRepository {

    private Logger logger = LoggerFactory.getLogger(DefaultRegisteredClientRepositoryImpl.class);

    private final ClientRepository clientRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DefaultRegisteredClientRepositoryImpl(ClientRepository clientRepository) {

        Assert.notNull(clientRepository, "clientRepository cannot be null");
        this.clientRepository = clientRepository;

        ClassLoader classLoader = DefaultRegisteredClientRepositoryImpl.class.getClassLoader();
        this.objectMapper.setTimeZone(TimeZone.getTimeZone("Europe/Vienna"));
        this.objectMapper.registerModules(SecurityJackson2Modules.getModules(classLoader));
        this.objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
        this.objectMapper.registerModule(new JavaTimeModule());

        this.objectMapper.addMixIn(Collections.singletonMap(String.class, Object.class).getClass(),
                SingletonMapMixin.class);

        this.objectMapper.addMixIn(OauthUser.class, OauthUserMixin.class);

    }

    @Override
    public void save(RegisteredClient registeredClient) {
        
        logger.debug("save RegisteredClient" + registeredClient);
        
        Assert.notNull(registeredClient, "registeredClient cannot be null");
        
        Optional<OauthClient> byId = this.clientRepository.findById(registeredClient.getId());
        if (byId.isPresent()) {
            this.clientRepository.update(toEntity(registeredClient));
        } else {
            this.clientRepository.save(toEntity(registeredClient));
        }
        
    }

    @Override
    public RegisteredClient findById(String id) {

        logger.debug("findById RegisteredClient" + id);
        
        Assert.hasText(id, "id cannot be empty");
        return this.clientRepository.findById(id).map(this::toObject).orElse(null);
        
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {

        logger.debug("findByClientId RegisteredClient" + clientId);
        
        Assert.hasText(clientId, "clientId cannot be empty");
        return this.clientRepository.findByClientId(clientId).map(this::toObject).orElse(null);
        
    }

    private RegisteredClient toObject(OauthClient client) {
        
        Set<String> clientAuthenticationMethods = StringUtils.commaDelimitedListToSet(
                client.getClientAuthenticationMethods());
        Set<String> authorizationGrantTypes = StringUtils.commaDelimitedListToSet(
                client.getAuthorizationGrantTypes());
        Set<String> redirectUris = StringUtils.commaDelimitedListToSet(
                client.getRedirectUris());
        Set<String> clientScopes = StringUtils.commaDelimitedListToSet(
                client.getScopes());

        RegisteredClient.Builder builder = RegisteredClient.withId(client.getId())
                .clientId(client.getClientId())
                .clientIdIssuedAt(CommonUtils.dateToInstant(client.getClientIdIssuedAt()))
                .clientSecret(client.getClientSecret())
                .clientSecretExpiresAt(CommonUtils.dateToInstant(client.getClientSecretExpiresAt()))
                .clientName(client.getClientName())
                .clientAuthenticationMethods(authenticationMethods ->
                        clientAuthenticationMethods.forEach(authenticationMethod ->
                                authenticationMethods.add(resolveClientAuthenticationMethod(authenticationMethod))))
                .authorizationGrantTypes((grantTypes) ->
                        authorizationGrantTypes.forEach(grantType ->
                                grantTypes.add(resolveAuthorizationGrantType(grantType))))
                .redirectUris((uris) -> uris.addAll(redirectUris))
                .scopes((scopes) -> scopes.addAll(clientScopes));

        Map<String, Object> clientSettingsMap = parseMap(client.getClientSettings());
        builder.clientSettings(ClientSettings.withSettings(clientSettingsMap).build());

        Map<String, Object> tokenSettingsMap = parseMap(client.getTokenSettings());
        builder.tokenSettings(TokenSettings.withSettings(tokenSettingsMap).build());

        return builder.build();
        
    }

    private OauthClient toEntity(RegisteredClient registeredClient) {
        
        List<String> clientAuthenticationMethods =
                new ArrayList<>(registeredClient.getClientAuthenticationMethods().size());
        registeredClient.getClientAuthenticationMethods().forEach(clientAuthenticationMethod ->
                clientAuthenticationMethods.add(clientAuthenticationMethod.getValue()));

        List<String> authorizationGrantTypes = new ArrayList<>(registeredClient.getAuthorizationGrantTypes().size());
        registeredClient.getAuthorizationGrantTypes().forEach(authorizationGrantType ->
                authorizationGrantTypes.add(authorizationGrantType.getValue()));

        OauthClient entity = new OauthClient();
        entity.setId(registeredClient.getId());
        entity.setClientId(registeredClient.getClientId());
        entity.setClientIdIssuedAt(Objects.isNull(registeredClient.getClientIdIssuedAt()) ? null :
                Date.from(registeredClient.getClientIdIssuedAt()));
        entity.setClientSecret(registeredClient.getClientSecret());
        entity.setClientSecretExpiresAt(Objects.isNull(registeredClient.getClientSecretExpiresAt()) ? null :
                Date.from(registeredClient.getClientSecretExpiresAt()));
        entity.setClientName(registeredClient.getClientName());
        entity.setClientAuthenticationMethods(StringUtils.collectionToCommaDelimitedString(clientAuthenticationMethods));
        entity.setAuthorizationGrantTypes(StringUtils.collectionToCommaDelimitedString(authorizationGrantTypes));
        entity.setRedirectUris(StringUtils.collectionToCommaDelimitedString(registeredClient.getRedirectUris()));
        entity.setScopes(StringUtils.collectionToCommaDelimitedString(registeredClient.getScopes()));
        entity.setClientSettings(writeMap(registeredClient.getClientSettings().getSettings()));
        entity.setTokenSettings(writeMap(registeredClient.getTokenSettings().getSettings()));

        return entity;
        
    }

    private Map<String, Object> parseMap(String data) {
        
        try {
            return this.objectMapper.readValue(data, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
        
    }

    private String writeMap(Map<String, Object> data) {
        
        try {
            return this.objectMapper.writeValueAsString(data);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
        
    }

    private static AuthorizationGrantType resolveAuthorizationGrantType(String authorizationGrantType) {
        
        if (AuthorizationGrantType.AUTHORIZATION_CODE.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.AUTHORIZATION_CODE;
        } else if (AuthorizationGrantType.CLIENT_CREDENTIALS.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.CLIENT_CREDENTIALS;
        } else if (AuthorizationGrantType.REFRESH_TOKEN.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.REFRESH_TOKEN;
        }
        // Custom authorization grant type
        return new AuthorizationGrantType(authorizationGrantType);
        
    }

    private static ClientAuthenticationMethod resolveClientAuthenticationMethod(String clientAuthenticationMethod) {
        
        if (ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
        } else if (ClientAuthenticationMethod.CLIENT_SECRET_POST.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_POST;
        } else if (ClientAuthenticationMethod.NONE.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.NONE;
        }
        // Custom client authentication method
        return new ClientAuthenticationMethod(clientAuthenticationMethod);
        
    }
}
