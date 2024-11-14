package com.acme.roombooker.utils.listeners;

import com.acme.roombooker.model.Auditable;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;


public class DefaultAuditListener {

    @PrePersist
    public void setCreatedOn(Object entity) {
        if (entity != null) {
            if (entity instanceof Auditable auditable) {
                LocalDateTime now = LocalDateTime.now();
                String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

                auditable.setCreatedDate(now);
                auditable.setCreatedBy(currentUser);

            }
        }
    }

    @PreUpdate
    public void setModifiedOn(Object entity) {
        if (entity != null) {
            if (entity instanceof Auditable auditable) {
                LocalDateTime now = LocalDateTime.now();
                String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

                auditable.setLastModifiedDate(now);
                auditable.setLastModifiedBy(currentUser);
            }
        }
    }

}
