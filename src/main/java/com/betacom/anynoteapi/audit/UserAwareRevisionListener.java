package com.betacom.anynoteapi.audit;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserAwareRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        UserAwareRevisionEntity entity = (UserAwareRevisionEntity) revisionEntity;
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        entity.setUsername(username);
    }
}
