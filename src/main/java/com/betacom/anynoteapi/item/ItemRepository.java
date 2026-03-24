package com.betacom.anynoteapi.item;

import com.betacom.anynoteapi.user.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface ItemRepository extends CrudRepository<Item, UUID> {

    @Query("SELECT i FROM Item i " +
            "LEFT JOIN i.permissions p with p.user = :user " +
            "WHERE i.owner =: user OR p IS NOT NULL")
    List<Item> findAllAvailableItemsForUser(User user);

    @Query("SELECT i.version FROM Item i WHERE i.id = :id")
    Integer getCurrentVersion(UUID id);
}
