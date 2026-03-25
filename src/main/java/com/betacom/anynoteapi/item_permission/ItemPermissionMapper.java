package com.betacom.anynoteapi.item_permission;

import com.betacom.anynoteapi.item.Item;
import com.betacom.anynoteapi.item_permission.dto.ItemPermissionRequest;
import com.betacom.anynoteapi.item_permission.dto.ItemPermissionResponse;
import com.betacom.anynoteapi.user.User;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemPermissionMapper {
    @Mapping(source = "userId", target = "user.id")
    ItemPermission toItemPermission(ItemPermissionRequest itemPermissionRequest, Item item, User user);

    @InheritInverseConfiguration(name = "toEntity")
    ItemPermissionResponse toPermissionResponse(ItemPermission itemPermission);

}