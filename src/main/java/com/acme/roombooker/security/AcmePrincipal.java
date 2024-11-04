package com.acme.roombooker.security;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Builder
public class AcmePrincipal implements UserDetails {

    private String username;
    private String password;
    @Getter
    private String email;
    @Getter
    private Long id;
    public Collection<? extends GrantedAuthority> authorities;


    public AcmePrincipal(String username,
                         String password,
                         String email,
                         Long id,
                         Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.id = id;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
