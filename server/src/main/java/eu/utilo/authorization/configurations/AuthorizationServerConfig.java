package eu.utilo.authorization.configurations;

import eu.utilo.authorization.entity.OauthUser;
import eu.utilo.authorization.jose.Jwks;
import eu.utilo.authorization.repository.UserRepository;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.*;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.util.*;
import java.util.function.Consumer;

/**
 * Spring Security Authorization Servere main config Klasse
 * @author coster
 */
@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfig {

    private static final String CUSTOM_CONSENT_PAGE_URI = "/oauth2/consent";
    private static final String O_AUTH_LOGIN = "/login";

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Arrays.asList("*"));
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
        //
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return urlBasedCorsConfigurationSource;
    }

    /**
     * SecurityFilter Chain
     * überschreibt die defaultSecurityFilterChain von Spring Security
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {

        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();

        authorizationServerConfigurer
                .authorizationEndpoint(authorizationEndpoint ->
                        // Oauth URL
                        authorizationEndpoint.consentPage(CUSTOM_CONSENT_PAGE_URI)
                );

        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();

        http.securityMatcher(endpointsMatcher)
                .authorizeHttpRequests(authorize ->
                        authorize.
                                anyRequest().authenticated()
                )
                //.csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
                .csrf().disable()
                .apply(authorizationServerConfigurer)
                .oidc(oidc -> {
                            // userinfo
                            oidc.userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userInfoMapper(
                                    oidcUserInfoAuthenticationContext -> {

                                        OAuth2AccessToken accessToken =
                                                oidcUserInfoAuthenticationContext.getAccessToken();
                                        Map<String, Object> claims = new HashMap<>();
                                        claims.put("accessToken", accessToken);
                                        claims.put("sub",
                                                oidcUserInfoAuthenticationContext.getAuthorization().getPrincipalName());
                                        return new OidcUserInfo(claims);

                                    }));
                        }

                        // es gibt noch den clientRegistrationEndpoint der per Default disabled ist
                        // .clientRegistrationEndpoint()

                );

        /* erlaube localhost in redirect_uri */
        authorizationServerConfigurer
                .authorizationEndpoint(authorizationEndpoint ->
                        authorizationEndpoint
                                .authenticationProviders(configureAuthenticationValidator())
                );

        // Enable OpenID Connect 1.0
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());

        http.exceptionHandling(exceptions ->
                        exceptions.authenticationEntryPoint(
                                new LoginUrlAuthenticationEntryPoint(O_AUTH_LOGIN))
                )
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);

        return http.build();

    }

    /**
     * erlaube localhost für redirect_uri
     * @see //docs.spring.io/spring-authorization-server/docs/current/reference/html/protocol-endpoints.html
     * @return
     */
    private Consumer<List<AuthenticationProvider>> configureAuthenticationValidator() {
        return (authenticationProviders) ->
                authenticationProviders.forEach((authenticationProvider) -> {
                    if (authenticationProvider instanceof OAuth2AuthorizationCodeRequestAuthenticationProvider) {
                        Consumer<OAuth2AuthorizationCodeRequestAuthenticationContext> authenticationValidator =
                                // Override default redirect_uri validator
                                new CustomRedirectUriValidator()
                                        // Reuse default scope validator
                                        .andThen(OAuth2AuthorizationCodeRequestAuthenticationValidator.DEFAULT_SCOPE_VALIDATOR);

                        ((OAuth2AuthorizationCodeRequestAuthenticationProvider) authenticationProvider)
                                .setAuthenticationValidator(authenticationValidator);
                    }
                });
    }
    /**
     * erlaube localhost für redirect_uri
     * @see //docs.spring.io/spring-authorization-server/docs/current/reference/html/protocol-endpoints.html
     */
    static class CustomRedirectUriValidator implements Consumer<OAuth2AuthorizationCodeRequestAuthenticationContext> {

        @Override
        public void accept(OAuth2AuthorizationCodeRequestAuthenticationContext authenticationContext) {
            OAuth2AuthorizationCodeRequestAuthenticationToken authorizationCodeRequestAuthentication =
                    authenticationContext.getAuthentication();
            RegisteredClient registeredClient = authenticationContext.getRegisteredClient();
            String requestedRedirectUri = authorizationCodeRequestAuthentication.getRedirectUri();

            // Use exact string matching when comparing client redirect URIs against pre-registered URIs
            if (!registeredClient.getRedirectUris().contains(requestedRedirectUri)) {
                OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST);
                throw new OAuth2AuthorizationCodeRequestAuthenticationException(error, null);
            }
        }
    }
    /**
     * tokens
     */
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(UserRepository userRepository) {

        return (context) -> {
            if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
                Optional<OauthUser> userByUsername
                        = userRepository.findUserByUsername(context.getPrincipal().getName());
                OidcUserInfo oidcUserInfo = userByUsername.map(oauthUser -> OidcUserInfo.builder()
                        .email("christian.osterrieder@utilo.eu")
                        .birthdate("0000-00-00")
                        .nickname("coster")
                        .claim("username", oauthUser.getUsername())
                        .claim("utilo", "coster")
                        .build()).orElseGet(() -> new OidcUserInfo(new HashMap<>()));
                // id_token
                context.getClaims().claims(claims -> claims.putAll(oidcUserInfo.getClaims()));
            }
            if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                context.getClaims().claims((claims) -> {
                    claims.put("utilo", "coster");
                    claims.put("coster", "utilo");
                });
            }
        };

    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = Jwks.generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    /**
     * JwtDecoder
     */
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    /**
     * jdbctemplate
     */
    @Bean
    public CustomJdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new CustomJdbcTemplate(dataSource);
    }

}
