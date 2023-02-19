package eu.utilo.authorization.repository;

import eu.utilo.authorization.entity.OauthAuthorization;
import eu.utilo.authorization.utils.CommonUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

/**
 */
@Slf4j
public class AuthorizationRepository {

    @Resource
    private JdbcTemplate jdbcOperations;

    public void save(OauthAuthorization authorization) {

        int update = jdbcOperations.update(
                    "insert into " +
                            "oauth_authorization " +
                        "(" +
                            "id, registered_client_id, " +
                            "principal_name, " +
                            "authorization_grant_type,authorized_scopes,attributes, state, authorization_code_value, " +
                            "authorization_code_issued_at," +
                            " authorization_code_expires_at, authorization_code_metadata, access_token_value, " +
                            "access_token_issued_at, access_token_expires_at, access_token_metadata, access_token_type, " +
                            "access_token_scopes, refresh_token_value, refresh_token_issued_at, refresh_token_expires_at," +
                            "refresh_token_metadata, oidc_id_token_value, oidc_id_token_issued_at, " +
                            "oidc_id_token_expires_at, " +
                            "oidc_id_token_metadata, oidc_id_token_claims" +
                        ") " +
                        "values (" +
                            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?" +
                        ")",
                authorization.getId(),
                authorization.getRegisteredClientId(),
                authorization.getPrincipalName(),
                authorization.getAuthorizationGrantType(),
                authorization.getAuthorizedScopes(),
                authorization.getAttributes(),
                authorization.getState(),
                authorization.getAuthorizationCodeValue(),
                authorization.getAuthorizationCodeIssuedAt(),
                authorization.getAuthorizationCodeExpiresAt(),
                authorization.getAuthorizationCodeMetadata(),
                authorization.getAccessTokenValue(),
                authorization.getAccessTokenIssuedAt(),
                authorization.getAccessTokenExpiresAt(),
                authorization.getAccessTokenMetadata(),
                authorization.getAccessTokenType(),
                authorization.getAccessTokenScopes(),
                authorization.getRefreshTokenValue(),
                authorization.getRefreshTokenIssuedAt(),
                authorization.getRefreshTokenExpiresAt(),
                authorization.getRefreshTokenMetadata(),
                authorization.getOidcIdTokenValue(),
                authorization.getOidcIdTokenIssuedAt(),
                authorization.getOidcIdTokenExpiresAt(),
                authorization.getOidcIdTokenMetadata(),
                authorization.getOidcIdTokenClaims());
        if (update == 0) {
            log.error("save oauth_authorization = null: " + authorization);
        }
    }

    public void update(OauthAuthorization authorization) {

        int update = jdbcOperations.update("update oauth_authorization " +
                        "set registered_client_id=?, " +
                        "principal_name=?, " +
                        "authorization_grant_type=?, " +
                        "authorized_scopes=?, " +
                        "attributes=?, " +
                        "state=?, " +
                        "authorization_code_value=?, " +
                        "authorization_code_issued_at=?, " +
                        "authorization_code_expires_at=?, " +
                        "authorization_code_metadata=?, " +
                        "access_token_value=?, " +
                        "access_token_issued_at=?, " +
                        "access_token_expires_at=?, " +
                        "access_token_metadata=?, " +
                        "access_token_type=?, " +
                        "access_token_scopes=?, " +
                        "refresh_token_value=?, " +
                        "refresh_token_issued_at=?, " +
                        "refresh_token_expires_at=?, " +
                        "refresh_token_metadata=?, " +
                        "oidc_id_token_value=?, " +
                        "oidc_id_token_issued_at=?, " +
                        "oidc_id_token_expires_at=?, " +
                        "oidc_id_token_metadata=?, " +
                        "oidc_id_token_claims=? " +
                        "where id = ?",
                authorization.getRegisteredClientId(),
                authorization.getPrincipalName(),
                authorization.getAuthorizationGrantType(),
                authorization.getAuthorizedScopes(),
                authorization.getAttributes(),
                authorization.getState(),
                authorization.getAuthorizationCodeValue(),
                authorization.getAuthorizationCodeIssuedAt(),
                authorization.getAuthorizationCodeExpiresAt(),
                authorization.getAuthorizationCodeMetadata(),
                authorization.getAccessTokenValue(),
                authorization.getAccessTokenIssuedAt(),
                authorization.getAccessTokenExpiresAt(),
                authorization.getAccessTokenMetadata(),
                authorization.getAccessTokenType(),
                authorization.getAccessTokenScopes(),
                authorization.getRefreshTokenValue(),
                authorization.getRefreshTokenIssuedAt(),
                authorization.getRefreshTokenExpiresAt(),
                authorization.getRefreshTokenMetadata(),
                authorization.getOidcIdTokenValue(),
                authorization.getOidcIdTokenIssuedAt(),
                authorization.getOidcIdTokenExpiresAt(),
                authorization.getOidcIdTokenMetadata(),
                authorization.getOidcIdTokenClaims(),
                authorization.getId());
        if (update == 0) {
            log.error("oauth_authorization update == null " + authorization);
        }

    }

    public void deleteById(String id) {

        int update = jdbcOperations.update("delete from oauth_authorization where id=?", id);
        if (update == 0) {
            log.error("id = {}!", id);
        }

    }

    public Optional<OauthAuthorization> findById(String id) {

        OauthAuthorization authorization = jdbcOperations.queryForObject("select id, registered_client_id, " +
                        "principal_name, authorization_grant_type, authorized_scopes," +
                        "attributes, state, authorization_code_value, authorization_code_issued_at, " +
                        "authorization_code_expires_at, authorization_code_metadata, access_token_value, " +
                        "access_token_issued_at, access_token_expires_at, access_token_metadata, access_token_type, " +
                        "access_token_scopes, refresh_token_value, refresh_token_issued_at, refresh_token_expires_at," +
                        " " +
                        "refresh_token_metadata, oidc_id_token_value, oidc_id_token_issued_at, " +
                        "oidc_id_token_expires_at, " +
                        "oidc_id_token_metadata, oidc_id_token_claims from oauth_authorization where id = ?",
                new BeanPropertyRowMapper<>(OauthAuthorization.class), id);
        return Optional.ofNullable(authorization);

    }

    public Optional<OauthAuthorization> findByState(String state) {

        OauthAuthorization authorization = jdbcOperations.queryForObject("select id, registered_client_id, " +
                        "principal_name, authorization_grant_type, authorized_scopes," +
                        "attributes, state, authorization_code_value, authorization_code_issued_at, " +
                        "authorization_code_expires_at, authorization_code_metadata, access_token_value, " +
                        "access_token_issued_at, access_token_expires_at, access_token_metadata, access_token_type, " +
                        "access_token_scopes, refresh_token_value, refresh_token_issued_at, refresh_token_expires_at," +
                        " " +
                        "refresh_token_metadata, oidc_id_token_value, oidc_id_token_issued_at, " +
                        "oidc_id_token_expires_at, " +
                        "oidc_id_token_metadata, oidc_id_token_claims from oauth_authorization where state = ?",
                new BeanPropertyRowMapper<>(OauthAuthorization.class), state);
        return Optional.ofNullable(authorization);

    }

    private String dbType() {

        Optional<String> databaseType = CommonUtils.getDatabaseType(jdbcOperations);
        if (databaseType.isPresent()) {
            return databaseType.get();
        } else {
            log.error("Datenbanktyp Fehler!");
            throw new RuntimeException("Datenbanktyp Fehler!");
        }

    }

    public Optional<OauthAuthorization> findByAuthorizationCodeValue(String authorizationCode) {

        OauthAuthorization authorization = jdbcOperations.queryForObject("select id, registered_client_id, " +
                        "principal_name, authorization_grant_type, authorized_scopes," +
                        "attributes, state, authorization_code_value, authorization_code_issued_at, " +
                        "authorization_code_expires_at, authorization_code_metadata, access_token_value, " +
                        "access_token_issued_at, access_token_expires_at, access_token_metadata, access_token_type, " +
                        "access_token_scopes, refresh_token_value, refresh_token_issued_at, refresh_token_expires_at," +
                        " " +
                        "refresh_token_metadata, oidc_id_token_value, oidc_id_token_issued_at, " +
                        "oidc_id_token_expires_at, " +
                        "oidc_id_token_metadata, oidc_id_token_claims from oauth_authorization where " +
                        " authorization_code_value = ? ",
                new BeanPropertyRowMapper<>(OauthAuthorization.class), authorizationCode);
        return Optional.ofNullable(authorization);

    }

    public Optional<OauthAuthorization> findByAccessTokenValue(String accessToken) {

        OauthAuthorization authorization = jdbcOperations.queryForObject("select id, registered_client_id, " +
                        "principal_name, authorization_grant_type,authorized_scopes, " +
                        "attributes, state, authorization_code_value, authorization_code_issued_at, " +
                        "authorization_code_expires_at, authorization_code_metadata, access_token_value, " +
                        "access_token_issued_at, access_token_expires_at, access_token_metadata, access_token_type, " +
                        "access_token_scopes, refresh_token_value, refresh_token_issued_at, refresh_token_expires_at," +
                        " " +
                        "refresh_token_metadata, oidc_id_token_value, oidc_id_token_issued_at, " +
                        "oidc_id_token_expires_at, " +
                        "oidc_id_token_metadata, oidc_id_token_claims from oauth_authorization where " +
                        " access_token_value = ? ",
                new BeanPropertyRowMapper<>(OauthAuthorization.class), accessToken);
        return Optional.ofNullable(authorization);

    }

    public Optional<OauthAuthorization> findByRefreshTokenValue(String refreshToken) {

        OauthAuthorization authorization = jdbcOperations.queryForObject("select id, registered_client_id, " +
                        "principal_name, authorization_grant_type,authorized_scopes, " +
                        "attributes, state, authorization_code_value, authorization_code_issued_at, " +
                        "authorization_code_expires_at, authorization_code_metadata, access_token_value, " +
                        "access_token_issued_at, access_token_expires_at, access_token_metadata, access_token_type, " +
                        "access_token_scopes, refresh_token_value, refresh_token_issued_at, refresh_token_expires_at," +
                        " " +
                        "refresh_token_metadata, oidc_id_token_value, oidc_id_token_issued_at, " +
                        "oidc_id_token_expires_at, " +
                        "oidc_id_token_metadata, oidc_id_token_claims from oauth_authorization where " +
                        " refresh_token_value = ? ",
                new BeanPropertyRowMapper<>(OauthAuthorization.class), refreshToken);
        return Optional.ofNullable(authorization);

    }

    public Optional<OauthAuthorization> findByStateOrAuthorizationCodeValueOrAccessTokenValueOrRefreshTokenValue(String token) {

        OauthAuthorization authorization = jdbcOperations.queryForObject("select id, registered_client_id, " +
                        "principal_name, authorization_grant_type,authorized_scopes, attributes, state, " +
                        "authorization_code_value, " +
                        "authorization_code_issued_at, authorization_code_expires_at, authorization_code_metadata, " +
                        "access_token_value, access_token_issued_at, access_token_expires_at, access_token_metadata, " +
                        "access_token_type, access_token_scopes, refresh_token_value, refresh_token_issued_at, " +
                        "refresh_token_expires_at, refresh_token_metadata, oidc_id_token_value, " +
                        "oidc_id_token_issued_at, oidc_id_token_expires_at, oidc_id_token_metadata, " +
                        "oidc_id_token_claims from oauth_authorization where state = ? or  " +
                        "authorization_code_value = ? "  +
                        "or " +
                        " access_token_value = ? " +
                        "or " +
                        " refresh_token_value = ? ",
                new BeanPropertyRowMapper<>(OauthAuthorization.class), token, token, token, token);
        return Optional.ofNullable(authorization);

    }

}
