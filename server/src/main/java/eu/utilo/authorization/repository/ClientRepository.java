package eu.utilo.authorization.repository;

import eu.utilo.authorization.entity.OauthClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcOperations;

import java.util.Optional;

/**
 */
@Slf4j
public class ClientRepository {

    @Resource
    private JdbcOperations jdbcOperations;

    public Optional<OauthClient> findByClientId(String clientId) {

        log.debug("ClientRepository findByClientId " + clientId);

        OauthClient query = jdbcOperations
                .queryForObject(
                        "select id, client_id, client_id_issued_at, client_secret, client_secret_expires_at, " +
                                "client_name, client_authentication_methods, authorization_grant_types, " +
                                "redirect_uris, scopes, client_settings, token_settings from oauth_client where " +
                                "client_id = ?",
                        new BeanPropertyRowMapper<>(OauthClient.class),
                        clientId);
        return Optional.ofNullable(query);

    }

    public void save(OauthClient client) {

        log.debug("ClientRepository save " + client);

        int update = jdbcOperations.update(
                "insert into oauth_client(id, client_id, client_id_issued_at, client_secret, " +
                        "client_secret_expires_at, client_name, client_authentication_methods, " +
                        "authorization_grant_types, redirect_uris, scopes, client_settings, token_settings) values " +
                        "(?," +
                        "?,?,?,?,?,?,?,?,?,?,?)",
                client.getId(),
                client.getClientId(),
                client.getClientIdIssuedAt(),
                client.getClientSecret(),
                client.getClientSecretExpiresAt(),
                client.getClientName(),
                client.getClientAuthenticationMethods(),
                client.getAuthorizationGrantTypes(),
                client.getRedirectUris(),
                client.getScopes(),
                client.getClientSettings(),
                client.getTokenSettings());
        if (update == 0) {
            log.warn("ClientRepository save error!");
        }

    }

    public void update(OauthClient client) {

        log.debug("ClientRepository update " + client);

        int update = jdbcOperations.update("UPDATE oauth_client " +
                        " SET client_id= ?," +
                        "client_id_issued_at= ?," +
                        "client_secret= ?," +
                        "client_secret_expires_at= ?," +
                        "client_name= ?," +
                        "client_authentication_methods= ?," +
                        "authorization_grant_types= ?," +
                        "redirect_uris= ?," +
                        "scopes= ?," +
                        "client_settings= ?," +
                        "token_settings= ?" +
                        "WHERE ID = ?",
                client.getClientId(),
                client.getClientIdIssuedAt(),
                client.getClientSecret(),
                client.getClientSecretExpiresAt(),
                client.getClientName(),
                client.getClientAuthenticationMethods(),
                client.getAuthorizationGrantTypes(),
                client.getRedirectUris(),
                client.getScopes(),
                client.getClientSettings(),
                client.getTokenSettings(),
                client.getId());
        if (update == 0) {
            log.warn("ClientRepository update error!");
        }

    }

    public Optional<OauthClient> findById(String id) {

        log.debug("ClientRepository findById " + id);

        OauthClient query = jdbcOperations
                .queryForObject(
                        "select id, client_id, client_id_issued_at, client_secret, client_secret_expires_at, " +
                                "client_name, client_authentication_methods, authorization_grant_types, " +
                                "redirect_uris, scopes, client_settings, token_settings from oauth_client where id = ?",
                        new BeanPropertyRowMapper<>(OauthClient.class),
                        id);

        return Optional.ofNullable(query);

    }

}
