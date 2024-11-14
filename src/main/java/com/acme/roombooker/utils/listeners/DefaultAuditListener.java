package com.acme.roombooker.utils.listeners;

import com.acme.roombooker.model.Auditable;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.LocalDateTime;


public class DefaultAuditListener {

    @PrePersist
    public void setCreatedOn(Object entity) {
        if (entity != null) {
            if (entity instanceof Auditable<?>) {
                ((Auditable<?>) entity).setCreatedDate(LocalDateTime.now());
            }
        }
    }

    @PreUpdate
    public void setModifiedOn(Object entity) {
        if (entity != null) {
            if (entity instanceof Auditable<?>) {
                ((Auditable<?>) entity).setLastModifiedDate(LocalDateTime.now());
            }
        }
    }

}
