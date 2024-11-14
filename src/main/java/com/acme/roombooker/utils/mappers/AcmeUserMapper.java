package com.acme.roombooker.utils.mappers;

import com.acme.roombooker.model.AcmeUser;
import com.acme.roombooker.enums.AcmeRole;
import com.acme.roombooker.dto.AcmeUserDTO;
import com.acme.roombooker.security.dto.RegistrationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AcmeUserMapper {

    @Autowired
    private PasswordEncoder passwordEncoder;


    public AcmeUserDTO toAcmeUserDTO(AcmeUser acmeUser) {
        return AcmeUserDTO.builder()
                .name(acmeUser.getName())
                .email(acmeUser.getEmail())
                .role(acmeUser.getRole())
                .build();
    }

    public AcmeUser toAcmeUserEntity(RegistrationDTO dto) {
        AcmeUser acmeUser = new AcmeUser();
        acmeUser.setName(dto.getName());
        acmeUser.setUsername(dto.getUsername());
        acmeUser.setEmail(dto.getEmail());
        acmeUser.setRole(AcmeRole.USER); // default role
        acmeUser.setPassword(passwordEncoder.encode(dto.getPassword()));

        return acmeUser;
    }


}
