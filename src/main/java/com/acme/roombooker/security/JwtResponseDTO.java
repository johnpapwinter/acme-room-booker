package com.acme.roombooker.security;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class JwtResponseDTO {

    private String token;
    private String username;
    private String email;
    private Long id;
    private List<String> roles;

}
