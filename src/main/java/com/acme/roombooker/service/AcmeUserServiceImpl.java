package com.acme.roombooker.service;

import com.acme.roombooker.domain.entity.AcmeUser;
import com.acme.roombooker.domain.enums.AcmeRole;
import com.acme.roombooker.domain.repository.AcmeUserRepository;
import com.acme.roombooker.dto.AcmeUserDTO;
import com.acme.roombooker.exception.EntityNotFoundException;
import com.acme.roombooker.exception.ErrorMessages;
import com.acme.roombooker.security.dto.RegistrationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AcmeUserServiceImpl implements AcmeUserService {

    private final AcmeUserRepository acmeUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AcmeUserServiceImpl(AcmeUserRepository acmeUserRepository, PasswordEncoder passwordEncoder) {
        this.acmeUserRepository = acmeUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Long createAcmeUser(RegistrationDTO dto) {
        AcmeUser acmeUser = new AcmeUser();
        acmeUser.setName(dto.getName());
        acmeUser.setUsername(dto.getUsername());
        acmeUser.setEmail(dto.getEmail());
        acmeUser.setRole(AcmeRole.USER); // default role
        acmeUser.setPassword(passwordEncoder.encode(dto.getPassword()));


        acmeUserRepository.save(acmeUser);
        return acmeUser.getId();
    }

    @Override
    @Transactional
    public Long toggleAcmeUserRole(Long userId) {
        AcmeUser acmeUser = acmeUserRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessages.ARB_101_USER_NOT_FOUND.name())
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
                .map(this::toAcmeUserDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public AcmeUser findAcmeUserByUsername(String username) {
        return acmeUserRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessages.ARB_101_USER_NOT_FOUND.name())
        );
    }

    private AcmeUserDTO toAcmeUserDTO(AcmeUser acmeUser) {
        AcmeUserDTO dto = new AcmeUserDTO();
        dto.setName(acmeUser.getName());
        dto.setEmail(acmeUser.getEmail());
        dto.setRole(acmeUser.getRole());
        return dto;
    }

}
