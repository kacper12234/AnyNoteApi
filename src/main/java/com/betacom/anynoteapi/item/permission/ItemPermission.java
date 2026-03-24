package com.betacom.anynoteapi.item.permission;

import com.betacom.anynoteapi.item.Item;
import com.betacom.anynoteapi.user.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Entity
@Table(name = "item_permissions", uniqueConstraints = @UniqueConstraint(columnNames = {"item_id", "user_id"}))
public class ItemPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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
