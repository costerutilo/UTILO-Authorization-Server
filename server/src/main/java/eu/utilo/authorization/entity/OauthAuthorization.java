package eu.utilo.authorization.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.util.Date;

/**
 * Oauth Authorization
 * @author coster
 */
@Data
@Entity
@Table(name = "`oauth_authorization`")
public class OauthAuthorization {

    @Id
    @Column
    private String id;
    private String registeredClientId;
    private String principalName;
    private String authorizationGrantType;
    @Column(length = 1000)
    private String authorizedScopes;
    @Column(length = 4000, columnDefinition="TEXT")
    private String attributes;
    @Column(length = 500)
    private String state;
    @Column(length = 4000, columnDefinition="TEXT")
    private String authorizationCodeValue;
    private Date authorizationCodeIssuedAt;
    private Date authorizationCodeExpiresAt;
    private String authorizationCodeMetadata;
    @Column(length = 4000, columnDefinition="TEXT")
    private String accessTokenValue;
    private Date accessTokenIssuedAt;
    private Date accessTokenExpiresAt;
    @Column(length = 2000)
    private String accessTokenMetadata;
    private String accessTokenType;
    @Column(length = 1000)
    private String accessTokenScopes;
    @Column(length = 4000, columnDefinition="TEXT")
    private String refreshTokenValue;
    private Date refreshTokenIssuedAt;
    private Date refreshTokenExpiresAt;
    @Column(length = 2000)
    private String refreshTokenMetadata;
    @Column(length = 4000, columnDefinition="TEXT")
    private String oidcIdTokenValue;
    private Date oidcIdTokenIssuedAt;
    private Date oidcIdTokenExpiresAt;
    @Column(length = 2000)
    private String oidcIdTokenMetadata;
    @Column(length = 2000)
    private String oidcIdTokenClaims;

}