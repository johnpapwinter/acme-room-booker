package com.acme.roombooker.domain.repository;

import com.acme.roombooker.domain.entity.AcmeUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcmeUserRepository extends JpaRepository<AcmeUser, Long> {


}
