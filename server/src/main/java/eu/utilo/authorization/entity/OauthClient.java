package eu.utilo.authorization.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import java.util.Date;

/**
 * OAuth Client
 * @author coster
 */
@Data
@Entity
public class OauthClient {

    @Id
    private String id;
    private String clientId;
    private Date clientIdIssuedAt;
    private String clientSecret;
    private Date clientSecretExpiresAt;
    private String clientName;
    @Column(length = 1000)
    private String clientAuthenticationMethods;
    @Column(length = 1000)
    private String authorizationGrantTypes;
    @Column(length = 1000)
    private String redirectUris;
    @Column(length = 1000)
    private String scopes;
    @Column(length = 2000)
    private String clientSettings;
    @Column(length = 2000)
    private String tokenSettings;

}