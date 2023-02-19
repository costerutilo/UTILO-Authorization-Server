package eu.utilo.authorization.services;

import eu.utilo.authorization.entity.Authority;
import eu.utilo.authorization.repository.AuthorityRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthorityService {

    @Autowired
    private AuthorityRepository authorityRepository;

    /**
     * neues Authority anlegen falls noch nicht vorhanden
     * @param name
     * @return vorhandenes oder neues Privilege
     */
    @Transactional
    public Authority createPrivilegeIfNotFound(String name) {

        Optional<Authority> authority = authorityRepository.findByAuthority(name);
        if (authority == null || authority.isEmpty()) {
            Authority p = new Authority(name);
            return authorityRepository.save(p);
        }
        return authority.get();

    }

}
