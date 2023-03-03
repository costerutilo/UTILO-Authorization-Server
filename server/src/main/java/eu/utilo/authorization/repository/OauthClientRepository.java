package eu.utilo.authorization.repository;

import eu.utilo.authorization.entity.OauthClient;
import eu.utilo.authorization.entity.OauthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 */
@Repository
public interface OauthClientRepository extends JpaRepository<OauthClient, Long>  {

    public List<OauthClient> findAll();

}
