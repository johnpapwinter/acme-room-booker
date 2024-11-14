package com.acme.roombooker.service;

import com.acme.roombooker.model.AcmeUser;
import com.acme.roombooker.enums.AcmeRole;
import com.acme.roombooker.repository.AcmeUserRepository;
import com.acme.roombooker.dto.AcmeUserDTO;
import com.acme.roombooker.exception.EntityNotFoundException;
import com.acme.roombooker.exception.ErrorMessages;
import com.acme.roombooker.security.dto.RegistrationDTO;
import com.acme.roombooker.utils.mappers.AcmeUserMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AcmeUserServiceImpl implements AcmeUserService {

    private final AcmeUserRepository acmeUserRepository;
    private final AcmeUserMapper mapper;

    public AcmeUserServiceImpl(AcmeUserRepository acmeUserRepository, AcmeUserMapper mapper) {
        this.acmeUserRepository = acmeUserRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Long createAcmeUser(RegistrationDTO dto) {
        AcmeUser acmeUser = mapper.toAcmeUserEntity(dto);

        acmeUserRepository.save(acmeUser);
        return acmeUser.getId();
    }

    @Override
    @Transactional
    public Long toggleAcmeUserRole(Long userId) {
        AcmeUser acmeUser = acmeUserRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessages.ARB_101_USER_NOT_FOUND)
        );

        if (acmeUser.getRole().equals(AcmeRole.USER)) {
            acmeUser.setRole(AcmeRole.ADMIN);
        } else {
            acmeUser.setRole(AcmeRole.USER);
        }

        return acmeUser.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AcmeUserDTO> getAcmeUsers(Pageable pageable) {
        return acmeUserRepository.findAll(pageable)
                .map(mapper::toAcmeUserDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public AcmeUser findAcmeUserByUsername(String username) {
        return acmeUserRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessages.ARB_101_USER_NOT_FOUND)
        );
    }

}
