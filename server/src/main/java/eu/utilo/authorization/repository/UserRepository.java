package eu.utilo.authorization.repository;

import eu.utilo.authorization.entity.OauthUser;
import jakarta.annotation.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 */
@Repository
public interface UserRepository extends JpaRepository<OauthUser, Long>  {

    public Optional<OauthUser> findUserByUsername(String username);
    public void deleteByUsername(String username);

}
