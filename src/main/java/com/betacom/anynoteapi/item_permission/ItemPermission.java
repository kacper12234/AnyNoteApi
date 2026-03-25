package com.betacom.anynoteapi.item_permission;

import com.betacom.anynoteapi.item.Item;
import com.betacom.anynoteapi.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "item_permissions", uniqueConstraints = @UniqueConstraint(columnNames = {"item_id", "user_id"}))
public class ItemPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Enumerated(EnumType.STRING)
    @Getter
    @Column(nullable = false)
    private ItemPermissionRole role;
}
