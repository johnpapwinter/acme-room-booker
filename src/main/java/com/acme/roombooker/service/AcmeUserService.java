package com.acme.roombooker.service;

import com.acme.roombooker.domain.entity.AcmeUser;
import com.acme.roombooker.dto.AcmeUserDTO;
import com.acme.roombooker.security.RegistrationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AcmeUserService {

    Long createAcmeUser(RegistrationDTO dto);

    Page<AcmeUserDTO> getAcmeUsers(Pageable pageable);

    AcmeUser findAcmeUserByUsername(String username);

}
