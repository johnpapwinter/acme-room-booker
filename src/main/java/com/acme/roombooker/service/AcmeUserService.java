package com.acme.roombooker.service;

import com.acme.roombooker.model.AcmeUser;
import com.acme.roombooker.dto.AcmeUserDTO;
import com.acme.roombooker.security.dto.RegistrationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AcmeUserService {

    Long createAcmeUser(RegistrationDTO dto);

    Long toggleAcmeUserRole(Long userId);

    Page<AcmeUserDTO> getAcmeUsers(Pageable pageable);

    AcmeUser findAcmeUserByUsername(String username);

}
