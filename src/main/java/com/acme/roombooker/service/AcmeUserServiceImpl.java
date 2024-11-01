package com.acme.roombooker.service;

import com.acme.roombooker.domain.entity.AcmeUser;
import com.acme.roombooker.domain.enums.AcmeRole;
import com.acme.roombooker.domain.repository.AcmeUserRepository;
import com.acme.roombooker.dto.AcmeUserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AcmeUserServiceImpl implements AcmeUserService {

    private final AcmeUserRepository acmeUserRepository;

    public AcmeUserServiceImpl(AcmeUserRepository acmeUserRepository) {
        this.acmeUserRepository = acmeUserRepository;
    }

    @Override
    @Transactional
    public Long createAcmeUser(AcmeUserDTO dto) {
        AcmeUser acmeUser = new AcmeUser();
        acmeUser.setName(dto.getName());
        acmeUser.setEmail(dto.getEmail());
        acmeUser.setRole(AcmeRole.USER); // default role

        acmeUserRepository.save(acmeUser);
        return acmeUser.getId();
    }


    @Override
    @Transactional(readOnly = true)
    public Page<AcmeUserDTO> getAcmeUsers(Pageable pageable) {
        return acmeUserRepository.findAll(pageable)
                .map(this::toAcmeUserDTO);
    }


    private AcmeUserDTO toAcmeUserDTO(AcmeUser acmeUser) {
        AcmeUserDTO dto = new AcmeUserDTO();
        dto.setName(acmeUser.getName());
        dto.setEmail(acmeUser.getEmail());
        dto.setRole(acmeUser.getRole());
        return dto;
    }

}
