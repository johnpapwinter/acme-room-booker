package com.acme.roombooker.repository;

import com.acme.roombooker.model.AcmeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AcmeUserRepository extends JpaRepository<AcmeUser, Long> {

    Optional<AcmeUser> findByUsername(String username);

}
