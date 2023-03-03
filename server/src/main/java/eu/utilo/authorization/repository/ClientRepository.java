package eu.utilo.authorization.repository;

import eu.utilo.authorization.entity.OauthClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 */
@Slf4j
public class ClientRepository {

    @Resource
    private JdbcOperations jdbcOperations;
    // Spring Boot will create and configure DataSource and JdbcTemplate
    // To use it, just @Autowired
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<OauthClient> findAll() {

        return jdbcTemplate.query(
                "select * from oauth_client",
                (rs, rowNum) ->
                        new OauthClient(
                                rs.getString("id"),
                                rs.getString("client_id"),
                                rs.getDate("client_id_issued_at"),
                                null, // rs.getString("client_secret"),
                                rs.getDate("client_secret_expires_at"),
                                rs.getString("client_name"),
                                rs.getString("client_authentication_methods"),
                                rs.getString("authorization_grant_types"),
                                rs.getString("redirect_uris"),
                                rs.getString("scopes"),
                                rs.getString("client_settings"),
                                rs.getString("token_settings")
                        )
        );

    }

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
