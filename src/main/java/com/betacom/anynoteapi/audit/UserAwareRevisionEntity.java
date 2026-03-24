package com.betacom.anynoteapi.audit;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

@Entity
@RevisionEntity(UserAwareRevisionListener.class)
@Getter
@Setter
public class UserAwareRevisionEntity extends DefaultRevisionEntity {

    private String username;
}
