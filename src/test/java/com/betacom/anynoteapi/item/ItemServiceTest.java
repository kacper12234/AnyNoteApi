package com.betacom.anynoteapi.item;

import com.betacom.anynoteapi.audit.AuditService;
import com.betacom.anynoteapi.exceptions.ForbiddenException;
import com.betacom.anynoteapi.exceptions.WrongVersionException;
import com.betacom.anynoteapi.item.dto.UpdateItemRequest;
import com.betacom.anynoteapi.item_permission.ItemPermissionRepository;
import com.betacom.anynoteapi.item_permission.ItemPermissionRole;
import com.betacom.anynoteapi.user.User;
import com.betacom.anynoteapi.user.UserProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private UserProvider userProvider;

    @Mock
    private AuditService auditService;

    @Mock
    private ItemPermissionRepository itemPermissionRepository;

    private ItemService itemService;

    @BeforeEach
    void setUp() {
        ItemAccessService itemAccessService = new ItemAccessService(itemPermissionRepository);
        itemService = new ItemService(itemRepository, itemAccessService, itemMapper, userProvider, auditService);
    }

    @Test
    void shouldAllowUpdate_whenUserIsOwner() {
        var itemId = UUID.randomUUID();

        var user = mock(User.class);
        when(user.getId()).thenReturn(UUID.randomUUID());

        var item = mock(Item.class);
        when(item.getOwner()).thenReturn(user);
        when(item.getVersion()).thenReturn(1);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userProvider.getCurrentUser()).thenReturn(user);

        itemService.updateItem(itemId, new UpdateItemRequest("test","test",1));

        verify(itemRepository).save(item);
        verify(itemMapper).toUpdateResponse(any());
    }

    @Test
    void shouldAllowUpdate_whenUserIsEditor() {
        UUID userId = UUID.randomUUID();

        var user = mock(User.class);
        when(user.getId()).thenReturn(userId);

        var owner = mock(User.class);
        when(owner.getId()).thenReturn(UUID.randomUUID());

        var item = mock(Item.class);
        when(item.getOwner()).thenReturn(owner);
        when(item.getVersion()).thenReturn(1);

        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(userProvider.getCurrentUser()).thenReturn(user);

        when(itemPermissionRepository.existsByItemIdAndUserIdAndRoleEquals(any(), eq(userId), eq(ItemPermissionRole.EDITOR)))
                .thenReturn(true);

        itemService.updateItem(UUID.randomUUID(), new UpdateItemRequest("test","test",1));

        verify(itemRepository).save(item);
        verify(itemMapper).toUpdateResponse(any());
    }

    @Test
    void shouldNotAllowUpdate_whenUserIsNotOwnerAndNotEditor() {
        UUID userId = UUID.randomUUID();

        var user = mock(User.class);
        when(user.getId()).thenReturn(userId);

        var owner = mock(User.class);
        when(owner.getId()).thenReturn(UUID.randomUUID());

        var item = mock(Item.class);
        when(item.getOwner()).thenReturn(owner);

        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(userProvider.getCurrentUser()).thenReturn(user);

        when(itemPermissionRepository.existsByItemIdAndUserIdAndRoleEquals(any(), eq(userId), eq(ItemPermissionRole.EDITOR)))
                .thenReturn(false);

        assertThrows(ForbiddenException.class, () -> itemService.updateItem(UUID.randomUUID(), new UpdateItemRequest("test","test",1)));
        verify(itemRepository, Mockito.never()).save(item);
    }

    @Test
    void shouldThrowWrongVersionException_whenVersionInRequestAndEntityDiffers() {
        UUID itemId = UUID.randomUUID();

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(UUID.randomUUID());

        var item = mock(Item.class);
        when(item.getOwner()).thenReturn(owner);
        when(item.getVersion()).thenReturn(5);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userProvider.getCurrentUser()).thenReturn(owner);

        WrongVersionException ex = assertThrows(WrongVersionException.class,
                () -> itemService.updateItem(itemId, new UpdateItemRequest("t", "t", 4)));

        assertEquals("Incorrect version, actual is 5", ex.getMessage());
    }

    @Test
    void shouldThrowWrongVersionException_whenOptimisticLockFails() {
        UUID itemId = UUID.randomUUID();

        User owner = mock(User.class);
        when(owner.getId()).thenReturn(UUID.randomUUID());

        var item = mock(Item.class);
        when(item.getOwner()).thenReturn(owner);
        when(item.getVersion()).thenReturn(4);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userProvider.getCurrentUser()).thenReturn(owner);

        when(itemRepository.save(item))
                .thenThrow(new ObjectOptimisticLockingFailureException(Item.class, itemId));

        when(itemRepository.getCurrentVersion(itemId)).thenReturn(5);

        WrongVersionException ex = assertThrows(WrongVersionException.class,
                () -> itemService.updateItem(itemId, new UpdateItemRequest("t", "t", 4)));

        assertEquals("Incorrect version, actual is 5", ex.getMessage());
    }
}