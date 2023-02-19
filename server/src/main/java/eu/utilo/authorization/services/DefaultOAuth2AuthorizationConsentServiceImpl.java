package eu.utilo.authorization.services;

import eu.utilo.authorization.entity.OauthAuthorizationConsent;
import eu.utilo.authorization.repository.AuthorizationConsentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class DefaultOAuth2AuthorizationConsentServiceImpl implements OAuth2AuthorizationConsentService {

    private Logger logger = LoggerFactory.getLogger(DefaultOAuth2AuthorizationConsentServiceImpl.class);
    private final AuthorizationConsentRepository authorizationConsentRepository;
    private final RegisteredClientRepository registeredClientRepository;

    public DefaultOAuth2AuthorizationConsentServiceImpl(
            AuthorizationConsentRepository authorizationConsentRepository,
            RegisteredClientRepository registeredClientRepository
    ) {

        Assert.notNull(authorizationConsentRepository, "authorizationConsentRepository cannot be null");
        Assert.notNull(registeredClientRepository, "registeredClientRepository cannot be null");
        this.authorizationConsentRepository = authorizationConsentRepository;
        this.registeredClientRepository = registeredClientRepository;

    }

    @Override
    public void save(OAuth2AuthorizationConsent authorizationConsent) {

        logger.debug("save OAuth2AuthorizationConsent: " + authorizationConsent);
        Assert.notNull(authorizationConsent, "authorizationConsent cannot be null");

        Optional<OauthAuthorizationConsent> byRegisteredClientIdAndPrincipalName = this.authorizationConsentRepository
                .findByRegisteredClientIdAndPrincipalName(authorizationConsent.getRegisteredClientId(),
                        authorizationConsent.getPrincipalName());
        if (byRegisteredClientIdAndPrincipalName.isPresent()) {
            this.authorizationConsentRepository.update(toEntity(authorizationConsent));
        } else {
            this.authorizationConsentRepository.save(toEntity(authorizationConsent));
        }

    }

    @Override
    public void remove(OAuth2AuthorizationConsent authorizationConsent) {

        logger.debug("remove OAuth2AuthorizationConsent: " + authorizationConsent);
        Assert.notNull(authorizationConsent, "authorizationConsent cannot be null");
        this.authorizationConsentRepository.deleteByRegisteredClientIdAndPrincipalName(
                authorizationConsent.getRegisteredClientId(), authorizationConsent.getPrincipalName()
        );

    }

    @Override
    public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {

        logger.debug("findById OAuth2AuthorizationConsent: " + registeredClientId + " " + principalName);

        Assert.hasText(registeredClientId, "registeredClientId cannot be empty");
        Assert.hasText(principalName, "principalName cannot be empty");

        return this.authorizationConsentRepository.findByRegisteredClientIdAndPrincipalName(
                registeredClientId, principalName
        ).map(this::toObject).orElse(null);

    }


    private OAuth2AuthorizationConsent toObject(OauthAuthorizationConsent authorizationConsent) {

        logger.debug("toObject OAuth2AuthorizationConsent: " + authorizationConsent);

        String registeredClientId = authorizationConsent.getRegisteredClientId();
        RegisteredClient registeredClient = this.registeredClientRepository.findById(registeredClientId);
        if (registeredClient == null) {

            String message = "The RegisteredClient with id '" + registeredClientId + "' was not found in the " +
                    "RegisteredClientRepository.";
            logger.warn(message);
            throw new DataRetrievalFailureException(message);

        }

        OAuth2AuthorizationConsent.Builder builder = OAuth2AuthorizationConsent.withId(
                registeredClientId, authorizationConsent.getPrincipalName());
        if (authorizationConsent.getAuthorities() != null) {
            for (String authority : StringUtils.commaDelimitedListToSet(authorizationConsent.getAuthorities())) {
                builder.authority(new SimpleGrantedAuthority(authority));
            }
        }

        return builder.build();

    }

    private OauthAuthorizationConsent toEntity(OAuth2AuthorizationConsent authorizationConsent) {

        logger.debug("toEntity OAuth2AuthorizationConsent: " + authorizationConsent);

        OauthAuthorizationConsent entity = new OauthAuthorizationConsent();
        entity.setRegisteredClientId(authorizationConsent.getRegisteredClientId());
        entity.setPrincipalName(authorizationConsent.getPrincipalName());

        Set<String> authorities = new HashSet<>();
        for (GrantedAuthority authority : authorizationConsent.getAuthorities()) {
            authorities.add(authority.getAuthority());
        }
        entity.setAuthorities(StringUtils.collectionToCommaDelimitedString(authorities));

        return entity;

    }

}
