package com.betacom.anynoteapi.item;

import com.betacom.anynoteapi.audit.AuditEntry;
import com.betacom.anynoteapi.item.dto.*;
import com.betacom.anynoteapi.item.permission.ItemPermission;
import com.betacom.anynoteapi.item.permission.ItemPermissionRole;
import com.betacom.anynoteapi.user.User;
import org.mapstruct.*;

import java.time.Instant;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING,
        imports = Instant.class)
public interface ItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", source = "user")
    @Mapping(target = "createdAt", expression = "java(Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(Instant.now())")
    Item toItem(CreateItemRequest createItemRequest, User user);

    @Mapping(source = "owner.id", target = "ownerId")
    CreateItemResponse toDto(Item item);

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "permissions", target = "myRole", qualifiedByName = "getRole")
    ItemResponse toResponse(Item item);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "updatedAt", expression = "java(Instant.now())")
    void updateItem(UpdateItemRequest dto, @MappingTarget Item entity);

    @Named("getRole")
    default ItemPermissionRole getRole(List<ItemPermission> permissions) {
        return permissions.isEmpty() ? ItemPermissionRole.OWNER : permissions.getFirst().getRole();
    }

    UpdateItemResponse toUpdateResponse(Item item);

    @Mapping(target = ".", source = "entity")
    ItemHistoryResponse toDto(AuditEntry<Item> entry);
}