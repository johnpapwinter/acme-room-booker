package com.acme.roombooker.service;

import com.acme.roombooker.domain.entity.AcmeUser;
import com.acme.roombooker.dto.AcmeUserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AcmeUserService {

    Long createAcmeUser(AcmeUserDTO dto);

    Page<AcmeUserDTO> getAcmeUsers(Pageable pageable);

    AcmeUser findAcmeUserByUsername(String username);

}
