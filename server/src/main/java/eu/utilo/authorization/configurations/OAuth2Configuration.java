package eu.utilo.authorization.configurations;

import eu.utilo.authorization.repository.AuthorizationConsentRepository;
import eu.utilo.authorization.repository.AuthorizationRepository;
import eu.utilo.authorization.repository.ClientRepository;
import eu.utilo.authorization.services.DefaultOAuth2AuthorizationConsentServiceImpl;
import eu.utilo.authorization.services.DefaultOAuth2AuthorizationServiceImpl;
import eu.utilo.authorization.services.DefaultRegisteredClientRepositoryImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

/**
 */
@Configuration(proxyBeanMethods = false)
public class OAuth2Configuration {

    /**
     * @param authorizationConsentRepository
     * @param registeredClientRepository
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public OAuth2AuthorizationConsentService oAuth2AuthorizationConsentService(
            AuthorizationConsentRepository authorizationConsentRepository,
            RegisteredClientRepository registeredClientRepository) {

        return new DefaultOAuth2AuthorizationConsentServiceImpl(
                authorizationConsentRepository,
                registeredClientRepository
        );

    }

    /**
     *
     * @param authorizationRepository
     * @param registeredClientRepository
     * @return org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
     */
    @Bean
    @ConditionalOnMissingBean
    public OAuth2AuthorizationService oAuth2AuthorizationService(
             AuthorizationRepository authorizationRepository,
             RegisteredClientRepository registeredClientRepository
    ) {

        return new DefaultOAuth2AuthorizationServiceImpl(authorizationRepository, registeredClientRepository);

    }

    /**
     *
     * @param clientRepository
     * @return org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
     */
    @Bean
    @ConditionalOnMissingBean
    public RegisteredClientRepository registeredClientRepository(ClientRepository clientRepository) {
        return new DefaultRegisteredClientRepositoryImpl(clientRepository);
    }

    /**
     *
     * @param
     * @return eu.utilo.authorization.oauth.repository.AuthorizationConsentRepository
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthorizationConsentRepository authorizationConsentRepository() {
        return new AuthorizationConsentRepository();
    }

    /**
     * @return eu.utilo.authorization.oauth.repository.AuthorizationRepository
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthorizationRepository authorizationRepository() {
        return new AuthorizationRepository();
    }

    /**
     * @return eu.utilo.authorization.oauth.repository.ClientRepository
     */
    @Bean
    @ConditionalOnMissingBean
    public ClientRepository clientRepository() {
        return new ClientRepository();
    }

}
