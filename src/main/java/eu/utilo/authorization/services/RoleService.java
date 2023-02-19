package eu.utilo.authorization.services;

import eu.utilo.authorization.entity.Authority;
import eu.utilo.authorization.entity.OauthRole;
import eu.utilo.authorization.repository.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;

@Component
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    /**
     * neue Rolle anlegen falls noch nicht vorhanden
     * @param name
     * @param authorities
     * @return neue Rolle oder bestehende
     */
    @Transactional
    public OauthRole createRoleIfNotFound (
            String name,
            Collection<Authority> authorities
    ) {

        Optional<OauthRole> optionalRole = roleRepository.findByName(name);
        if (optionalRole == null || optionalRole.isEmpty()) {
            OauthRole role = new OauthRole(name);
            role.setAuthorities(authorities);
            roleRepository.save(role);
            return role;
        } else {
            return optionalRole.get();
        }

    }

}
