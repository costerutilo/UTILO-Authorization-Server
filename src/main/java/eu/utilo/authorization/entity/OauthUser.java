package eu.utilo.authorization.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 */
@JsonAutoDetect
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "oauth_user", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username"}),
        @UniqueConstraint(columnNames = {"email"})
})
public class OauthUser extends AbstractEntity implements UserDetails {

    @Column(name = "username", nullable = false, length = 100, unique = true)
    private String username;
    @Column(name = "password", nullable = false, length = 100)
    private String password;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    @Column(name = "enabled", nullable = false)
    private boolean enabled;
    @Column(name = "first_name", nullable = true, length = 100)
    private String firstName;
    @Column(name = "last_name", nullable = true, length = 100)
    private String lastName;
    @Column(name = "email", nullable = true, length = 60, unique = true)
    private String email;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id",
                    referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id",
                    referencedColumnName = "id"
            )
    )
    private Collection<OauthRole> roles;

    public OauthUser(
            String username,
            String password
    ) {
        this.username = username;
        this.password = password;
        this.enabled = true;
        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.credentialsNonExpired = true;
    }

    public OauthUser(
             String username,
             String password,
             boolean accountNonExpired,
             boolean accountNonLocked,
             boolean credentialsNonExpired,
             boolean enabled
    ) {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Authorities von der Rolle holen
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.commaSeparatedStringToAuthorityList(
                getRoles().stream().map(r -> "ROLE_" + r)
                        .collect(Collectors.joining())
        );
    }

    public Collection<OauthRole> getRoles() {
        return roles;
    }

    public void setRoles(Collection<OauthRole> roles) {
        this.roles = roles;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

}
