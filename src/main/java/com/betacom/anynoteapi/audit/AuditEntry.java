package com.betacom.anynoteapi.audit;

import org.hibernate.envers.RevisionType;

import java.time.Instant;

public record AuditEntry<T>(int revision,
                            RevisionType revisionType,
                            Instant timestamp,
                            String changedBy,
                            T entity) {
}
