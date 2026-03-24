package com.betacom.anynoteapi.audit;

import lombok.RequiredArgsConstructor;
import org.hibernate.envers.RevisionType;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditRepository auditRepository;

    public <T> List<AuditEntry<T>> getHistory(Class<T> entityClass, UUID id) {
        List<Object[]> results = auditRepository.findRevisions(entityClass, id);

        return results.stream()
                .map(r -> mapToAuditEntry(r, entityClass))
                .toList();
    }

    private <T> AuditEntry<T> mapToAuditEntry(Object[] r, Class<T> entityClass) {
        T entity = entityClass.cast(r[0]);
        UserAwareRevisionEntity rev = (UserAwareRevisionEntity) r[1];
        RevisionType type = (RevisionType) r[2];

        return new AuditEntry<>(
                rev.getId(),
                type,
                Instant.ofEpochMilli(rev.getTimestamp()),
                rev.getUsername(),
                entity
        );
    }
}
