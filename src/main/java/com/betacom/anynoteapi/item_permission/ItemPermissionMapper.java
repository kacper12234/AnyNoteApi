package com.betacom.anynoteapi.item_permission;

import com.betacom.anynoteapi.item.Item;
import com.betacom.anynoteapi.item_permission.dto.ItemPermissionRequest;
import com.betacom.anynoteapi.item_permission.dto.ItemPermissionResponse;
import com.betacom.anynoteapi.user.User;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemPermissionMapper {
    ItemPermission toItemPermission(ItemPermissionRequest itemPermissionRequest, Item item, User user);

    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "userId", source = "user.id")
    ItemPermissionResponse toPermissionResponse(ItemPermission itemPermission);

}