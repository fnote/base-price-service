package com.sysco.payplus.entity.security;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * Author: rohana.kumara@sysco.com
 * Date: 3/15/20
 * Time: 12:54 PM
 */

@Entity
@Table(name = "application_user")
public class ApplicationUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "username", length = 45, nullable = false, unique = true)
    @NotBlank(message = "Username is mandatory")
    private String username;
    @Column(name = "password", length = 100, nullable = false, unique = false)
    @NotBlank(message = "Password is mandatory")
    private String password;
    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy="applicationUser")
    private Collection<Role> applicationUserRoles;

    public Collection<Role> getApplicationUserRoles() {
        return applicationUserRoles;
    }

    public void setApplicationUserRoles(Collection<Role> applicationUserRoles) {
        this.applicationUserRoles = applicationUserRoles;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
