package eu.utilo.authorization.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Authority wie z. B. READ oder WRITE
 * Authorities werden Rollen zugeteilt
 */
@Entity
public class Authority implements GrantedAuthority {

    public static final String READ = "READ_AUTHORITY";
    public static final String WRITE = "WRITE_AUTHORITY";
    public static final String UPDATE = "UPDATE_AUTHORITY";
    public static final String DELETE = "DELETE_AUTHORITY";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "authority", nullable = false, length = 20, unique = true)
    private String authority;

    public Authority() {
    }

    public Authority(String authority) {
        this.authority = authority;
    }

    public Long getId() {
        return id;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

}
