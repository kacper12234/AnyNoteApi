package com.betacom.anynoteapi.item_permission;

import com.betacom.anynoteapi.exceptions.SelfPermissionAssignmentException;
import com.betacom.anynoteapi.item.Item;
import com.betacom.anynoteapi.item.ItemAccessService;
import com.betacom.anynoteapi.item.ItemRepository;
import com.betacom.anynoteapi.item_permission.dto.ItemPermissionRequest;
import com.betacom.anynoteapi.user.User;
import com.betacom.anynoteapi.user.UserProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemPermissionServiceTest {

    @Mock
    private ItemPermissionRepository itemPermissionRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemAccessService accessService;
    @Mock
    private UserProvider userProvider;

    private ItemPermissionService itemPermissionService;

    @BeforeEach
    void setUp() {
        itemPermissionService = new ItemPermissionService(itemRepository, accessService,
                itemPermissionRepository, Mappers.getMapper(ItemPermissionMapper.class), userProvider);
    }

    @Test
    void shouldNotAllowShare_whenTargetUserIsOwner() {
        var user = mock(User.class);
        when(user.getId()).thenReturn(UUID.randomUUID());

        when(userProvider.getCurrentUser()).thenReturn(user);

        var itemPermissionRequest = new ItemPermissionRequest(user.getId(), ItemPermissionRole.EDITOR);
        UUID itemId = UUID.randomUUID();

        assertThrows(SelfPermissionAssignmentException.class,
                () -> itemPermissionService.shareItem(itemId, itemPermissionRequest));

        verify(itemPermissionRepository, never()).save(any());
    }

    @Test
    void shouldUpdatePermission_whenCurrentUserIsOwner() {
        var user = mock(User.class);
        when(user.getId()).thenReturn(UUID.randomUUID());

        var item = new Item();
        item.setOwner(user);

        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(userProvider.getCurrentUser()).thenReturn(user);
        when(userProvider.getUser(any())).thenReturn(user);

        ItemPermission permission = new ItemPermission();
        permission.setRole(ItemPermissionRole.VIEWER);

        when(itemPermissionRepository.findByItemIdAndUserId(any(), any())).thenReturn(Optional.of(permission));
        when(itemPermissionRepository.save(permission)).thenReturn(permission);

        var request = new ItemPermissionRequest(UUID.randomUUID(), ItemPermissionRole.EDITOR);
        ItemPermissionService.PermissionGrant grant = itemPermissionService.shareItem(UUID.randomUUID(), request);

        assertTrue(grant.updatedExisting());
        assertEquals(ItemPermissionRole.EDITOR, grant.permission().role());
    }

}