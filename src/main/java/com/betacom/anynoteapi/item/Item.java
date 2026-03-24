package com.betacom.anynoteapi.item;

import com.betacom.anynoteapi.item.permission.ItemPermission;
import com.betacom.anynoteapi.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Audited
@Getter
@Setter
@SQLRestriction("deleted = false")
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    private String title;
    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    @Version
    private Integer version;
    private Boolean deleted = false;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    @OneToMany(mappedBy = "item")
    @NotAudited
    private List<ItemPermission> permissions;


    public void markDeleted() {
        this.deleted = true;
    }
}
