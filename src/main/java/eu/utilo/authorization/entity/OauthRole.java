package eu.utilo.authorization.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "oauth_roles")
public class OauthRole extends AbstractEntity {

    /** User with ROLE_ADMIN role have the authorities to READ,DELETE,WRITE,UPDATE. */
    public static final String ADMIN = "ROLE_ADMIN";
    /** A user with role ROLE_USER has authority to READ only. */
    public static final String USER  = "ROLE_USER";
    /** User with ROLE_MANAGER can perform READ, WRITE and UPDATE operations. */
    public static final String MANAGER = "ROLE_MANAGER";

    @Column(name = "name", nullable = false, length = 20, unique = true)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "roles_authority",
            joinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "authority_id", referencedColumnName = "id"))
    private Collection<Authority> authorities;

    public OauthRole(String name) {
        this.name = name;
    }

    public Long getId() {
        return super.getId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<Authority> authorities) {
        this.authorities = authorities;
    }

}
