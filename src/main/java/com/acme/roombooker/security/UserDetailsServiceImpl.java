package com.acme.roombooker.security;

import com.acme.roombooker.model.AcmeUser;
import com.acme.roombooker.service.AcmeUserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AcmeUserService userService;

    public UserDetailsServiceImpl(AcmeUserService userService) {
        this.userService = userService;
    }

    @Override
    public AcmePrincipal loadUserByUsername(String username) throws UsernameNotFoundException {
        AcmeUser acmeUser = userService.findAcmeUserByUsername(username);

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(acmeUser.getRole().name());
        List<GrantedAuthority> authorities = new ArrayList<>(Collections.singleton(simpleGrantedAuthority));

        return AcmePrincipal.builder()
                .username(acmeUser.getUsername())
                .password(acmeUser.getPassword())
                .email(acmeUser.getEmail())
                .id(acmeUser.getId())
                .authorities(authorities)
                .build();
    }

}
