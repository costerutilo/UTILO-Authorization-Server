package eu.utilo.authorization.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.utilo.authorization.entity.OauthAuthorization;
import eu.utilo.authorization.entity.OauthUser;
import eu.utilo.authorization.jackson.OauthUserMixin;
import eu.utilo.authorization.jackson.SingletonMapMixin;
import eu.utilo.authorization.repository.AuthorizationRepository;
import eu.utilo.authorization.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import java.sql.Date;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

public class DefaultOAuth2AuthorizationServiceImpl implements OAuth2AuthorizationService {

    private Logger logger = LoggerFactory.getLogger(DefaultOAuth2AuthorizationServiceImpl.class);
    private final AuthorizationRepository authorizationRepository;
    private final RegisteredClientRepository registeredClientRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DefaultOAuth2AuthorizationServiceImpl(
             AuthorizationRepository authorizationRepository,
             RegisteredClientRepository registeredClientRepository
    ) {

        Assert.notNull(authorizationRepository, "authorizationRepository cannot be null");
        Assert.notNull(registeredClientRepository, "registeredClientRepository cannot be null");

        this.authorizationRepository = authorizationRepository;
        this.registeredClientRepository = registeredClientRepository;

        ClassLoader classLoader = DefaultOAuth2AuthorizationServiceImpl.class.getClassLoader();
        this.objectMapper.setTimeZone(TimeZone.getTimeZone("Europe/Vienna"));
        this.objectMapper.registerModules(SecurityJackson2Modules.getModules(classLoader));
        this.objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
        this.objectMapper.registerModule(new JavaTimeModule());

        this.objectMapper.addMixIn(Collections.singletonMap(String.class, Object.class).getClass(),
                SingletonMapMixin.class);

        this.objectMapper.addMixIn(OauthUser.class, OauthUserMixin.class);

    }

    @Override
    public void save(OAuth2Authorization authorization) {

        logger.debug("save OAuth2Authorization" + authorization);

        Assert.notNull(authorization, "authorization cannot be null");
        Optional<OauthAuthorization> byId = this.authorizationRepository.findById(authorization.getId());
        if (byId.isPresent()) {
            this.authorizationRepository.update(toEntity(authorization));
        } else {
            this.authorizationRepository.save(toEntity(authorization));
        }

    }

    @Override
    public void remove(OAuth2Authorization authorization) {

        logger.debug("remove OAuth2Authorization" + authorization);

        Assert.notNull(authorization, "authorization cannot be null");
        this.authorizationRepository.deleteById(authorization.getId());

    }

    @Override
    public OAuth2Authorization findById(String id) {

        logger.debug("findById OAuth2Authorization" + id);

        Assert.hasText(id, "id cannot be empty");
        return this.authorizationRepository.findById(id).map(this::toObject).orElse(null);

    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {

        logger.debug("save findByToken" + token + " " + tokenType);

        Assert.hasText(token, "token cannot be empty");

        Optional<OauthAuthorization> result;
        if (tokenType == null) {
            result = this.authorizationRepository
                    .findByStateOrAuthorizationCodeValueOrAccessTokenValueOrRefreshTokenValue(token);
        } else if (OAuth2ParameterNames.STATE.equals(tokenType.getValue())) {
            result = this.authorizationRepository.findByState(token);
        } else if (OAuth2ParameterNames.CODE.equals(tokenType.getValue())) {
            result = this.authorizationRepository.findByAuthorizationCodeValue(token);
        } else if (OAuth2ParameterNames.ACCESS_TOKEN.equals(tokenType.getValue())) {
            result = this.authorizationRepository.findByAccessTokenValue(token);
        } else if (OAuth2ParameterNames.REFRESH_TOKEN.equals(tokenType.getValue())) {
            result = this.authorizationRepository.findByRefreshTokenValue(token);
        } else {
            result = Optional.empty();
        }

        return result.map(this::toObject).orElse(null);

    }

    private OAuth2Authorization toObject(OauthAuthorization entity) {

        logger.debug("toObject OAuth2Authorization" + entity);

        RegisteredClient registeredClient = this.registeredClientRepository.findById(entity.getRegisteredClientId());
        if (registeredClient == null) {

            String message =  "The RegisteredClient with id '" + entity.getRegisteredClientId() + "' was not found in the " +
                    "RegisteredClientRepository.";
            logger.warn(message);

            throw new DataRetrievalFailureException(message);

        }

        OAuth2Authorization.Builder builder = OAuth2Authorization.withRegisteredClient(registeredClient)
                .id(entity.getId())
                .principalName(entity.getPrincipalName())
                .authorizationGrantType(resolveAuthorizationGrantType(entity.getAuthorizationGrantType()))
                .authorizedScopes(StringUtils.commaDelimitedListToSet(entity.getAuthorizedScopes()))
                .attributes(attributes -> attributes.putAll(parseMap(entity.getAttributes())));
        if (entity.getState() != null) {
            builder.attribute(OAuth2ParameterNames.STATE, entity.getState());
        }

        if (entity.getAuthorizationCodeValue() != null) {
            OAuth2AuthorizationCode authorizationCode = new OAuth2AuthorizationCode(
                    entity.getAuthorizationCodeValue(),
                    CommonUtils.dateToInstant(entity.getAuthorizationCodeIssuedAt()),
                    CommonUtils.dateToInstant(entity.getAuthorizationCodeExpiresAt()));
            builder.token(authorizationCode,
                    metadata -> metadata.putAll(parseMap(entity.getAuthorizationCodeMetadata())));
        }

        if (entity.getAccessTokenValue() != null) {
            OAuth2AccessToken accessToken = new OAuth2AccessToken(
                    OAuth2AccessToken.TokenType.BEARER,
                    entity.getAccessTokenValue(),
                    CommonUtils.dateToInstant(entity.getAccessTokenIssuedAt()),
                    CommonUtils.dateToInstant(entity.getAccessTokenExpiresAt()),
                    StringUtils.commaDelimitedListToSet(entity.getAccessTokenScopes()));
            builder.token(accessToken, metadata -> metadata.putAll(parseMap(entity.getAccessTokenMetadata())));
        }

        if (entity.getRefreshTokenValue() != null) {
            OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(
                    entity.getRefreshTokenValue(),
                    CommonUtils.dateToInstant(entity.getRefreshTokenIssuedAt()),
                    CommonUtils.dateToInstant(entity.getRefreshTokenExpiresAt()));
            builder.token(refreshToken, metadata -> metadata.putAll(parseMap(entity.getRefreshTokenMetadata())));
        }

        if (entity.getOidcIdTokenValue() != null) {
            OidcIdToken idToken = new OidcIdToken(
                    entity.getOidcIdTokenValue(),
                    CommonUtils.dateToInstant(entity.getOidcIdTokenIssuedAt()),
                    CommonUtils.dateToInstant(entity.getOidcIdTokenExpiresAt()),
                    parseMap(entity.getOidcIdTokenClaims()));
            builder.token(idToken, metadata -> metadata.putAll(parseMap(entity.getOidcIdTokenMetadata())));
        }

        return builder.build();

    }

    private OauthAuthorization toEntity(OAuth2Authorization authorization) {

        logger.debug("toEntity OAuth2Authorization" + authorization);

        OauthAuthorization entity = new OauthAuthorization();
        entity.setId(authorization.getId());
        entity.setRegisteredClientId(authorization.getRegisteredClientId());
        entity.setPrincipalName(authorization.getPrincipalName());
        entity.setAuthorizationGrantType(authorization.getAuthorizationGrantType().getValue());

        String authorizedScopes = StringUtils.collectionToDelimitedString(authorization.getAuthorizedScopes(), ",");
        entity.setAuthorizedScopes(
                StringUtils.hasText(authorizedScopes) ? authorizedScopes : null);

        entity.setAttributes(writeMap(authorization.getAttributes()));

        Object state = authorization.getAttribute(OAuth2ParameterNames.STATE);
        entity.setState(Objects.isNull(state) ? null : String.valueOf(state));

        OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode =
                authorization.getToken(OAuth2AuthorizationCode.class);
        setTokenValues(
                authorizationCode,
                entity::setAuthorizationCodeValue,
                item -> entity.setAuthorizationCodeIssuedAt(Date.from(item)),
                item -> entity.setAuthorizationCodeExpiresAt(Date.from(item)),
                entity::setAuthorizationCodeMetadata
        );

        OAuth2Authorization.Token<OAuth2AccessToken> accessToken =
                authorization.getToken(OAuth2AccessToken.class);
        setTokenValues(
                accessToken,
                entity::setAccessTokenValue,
                item -> entity.setAccessTokenIssuedAt(Date.from(item)),
                item -> entity.setAccessTokenExpiresAt(Date.from(item)),
                entity::setAccessTokenMetadata
        );
        if (accessToken != null && accessToken.getToken().getScopes() != null) {
            entity.setAccessTokenScopes(StringUtils.collectionToDelimitedString(accessToken.getToken().getScopes(),
                    ","));
        }

        OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken =
                authorization.getToken(OAuth2RefreshToken.class);
        setTokenValues(
                refreshToken,
                entity::setRefreshTokenValue,
                item -> entity.setRefreshTokenIssuedAt(Date.from(item)),
                item -> entity.setRefreshTokenExpiresAt(Date.from(item)),
                entity::setRefreshTokenMetadata
        );

        OAuth2Authorization.Token<OidcIdToken> oidcIdToken =
                authorization.getToken(OidcIdToken.class);
        setTokenValues(
                oidcIdToken,
                entity::setOidcIdTokenValue,
                item -> entity.setOidcIdTokenIssuedAt(Date.from(item)),
                item -> entity.setOidcIdTokenExpiresAt(Date.from(item)),
                entity::setOidcIdTokenMetadata
        );
        if (oidcIdToken != null) {
            entity.setOidcIdTokenClaims(writeMap(oidcIdToken.getClaims()));
        }

        return entity;

    }

    private void setTokenValues(
            OAuth2Authorization.Token<?> token,
            Consumer<String> tokenValueConsumer,
            Consumer<Instant> issuedAtConsumer,
            Consumer<Instant> expiresAtConsumer,
            Consumer<String> metadataConsumer) {

        if (token != null) {
            OAuth2Token oAuth2Token = token.getToken();
            tokenValueConsumer.accept(oAuth2Token.getTokenValue());
            issuedAtConsumer.accept(oAuth2Token.getIssuedAt());
            expiresAtConsumer.accept(oAuth2Token.getExpiresAt());
            metadataConsumer.accept(writeMap(token.getMetadata()));
        }

    }

    private Map<String, Object> parseMap(String data) {

        try {
            return this.objectMapper.readValue(data, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }

    }

    private String writeMap(Map<String, Object> metadata) {

        try {
            return this.objectMapper.writeValueAsString(metadata);
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

        return new AuthorizationGrantType(authorizationGrantType);

    }

}

