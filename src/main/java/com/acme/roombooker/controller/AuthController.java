package com.acme.roombooker.controller;

import com.acme.roombooker.security.*;
import com.acme.roombooker.security.dto.JwtResponseDTO;
import com.acme.roombooker.security.dto.LoginDTO;
import com.acme.roombooker.security.dto.RegistrationDTO;
import com.acme.roombooker.service.AcmeUserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AcmeUserService userService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public AuthController(AcmeUserService userService,
                          JwtUtils jwtUtils,
                          AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<Long> register(@Valid @RequestBody RegistrationDTO dto) {
        Long response = userService.createAcmeUser(dto);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@Valid @RequestBody LoginDTO dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateToken(authentication);
        AcmePrincipal principal = (AcmePrincipal) authentication.getPrincipal();

        JwtResponseDTO responseDTO = JwtResponseDTO.builder()
                .token(jwt)
                .username(principal.getUsername())
                .email(principal.getEmail())
                .id(principal.getId())
                .roles(principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .build();

        return ResponseEntity.ok().body(responseDTO);
    }

}
