package eu.utilo.authorization.repository;

import eu.utilo.authorization.entity.OauthRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<OauthRole, Long> {

    Optional<OauthRole> findByName(String name);

}
