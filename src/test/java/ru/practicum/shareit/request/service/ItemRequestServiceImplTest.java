package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@WebMvcTest(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    private final User requestor = User.builder().id(1).name("user").email("user@user.com").build();
    private final User owner = User.builder().id(2).name("owner").email("owner@user.com").build();
    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder().id(1).description("description").build();
    private final ItemRequest itemRequest = ItemRequestMapper.toItemRequest(requestor, itemRequestDto);


    @Test
    void saveItemRequest_ReturnItemRequest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto actualItemRequestDto = itemRequestService.saveItemRequest(requestor.getId(), itemRequestDto);
        ItemRequestDto expectItemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);

        assertEquals(actualItemRequestDto, expectItemRequestDto);
        verify(userRepository, times(1)).findById(anyInt());
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void saveItemRequest_whenUserNotFound_thanNotFoundExceptionThrown() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.saveItemRequest(requestor.getId(), itemRequestDto));
        verify(userRepository, times(1)).findById(anyInt());
        verify(itemRequestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    void getOwnRequests_ReturnItemRequest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.findAllByRequestorOrderByCreatedDesc(any(User.class)))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestDto> actualOwnRequests = itemRequestService.getOwnRequests(requestor.getId());
        List<ItemRequestDto> expectOwnRequests = List.of(ItemRequestMapper.toItemRequestDto(itemRequest));

        assertEquals(actualOwnRequests, expectOwnRequests);
        verify(userRepository, times(1)).findById(anyInt());
        verify(itemRequestRepository, times(1)).findAllByRequestorOrderByCreatedDesc(any(User.class));
    }

    @Test
    void getOwnRequests_whenUserNotFound_thanNotFoundExceptionThrown() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getOwnRequests(requestor.getId()));
        verify(userRepository, times(1)).findById(anyInt());
        verify(itemRequestRepository, never()).findAllByRequestorOrderByCreatedDesc(any(User.class));
    }

    @Test
    void getAllRequests_ReturnItemRequests() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(itemRequest)));

        List<ItemRequestDto> actualRequestDtos = itemRequestService.getAllRequests(owner.getId(), 0, 10);
        List<ItemRequestDto> expectRequestDtos = List.of(ItemRequestMapper.toItemRequestDto(itemRequest));

        assertEquals(actualRequestDtos, expectRequestDtos);
        verify(userRepository, times(1)).findById(anyInt());
        verify(itemRequestRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void getAllRequests_whenUserNotFound_thanNotFoundExceptionThrown() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getAllRequests(owner.getId(), 0, 10));
        verify(userRepository, times(1)).findById(anyInt());
        verify(itemRequestRepository, never()).findAll(any(PageRequest.class));
    }

    @Test
    void getById_returnItemRequest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.of(itemRequest));

        ItemRequestDto actualItemRequestDto = itemRequestService.getById(requestor.getId(), itemRequest.getId());
        ItemRequestDto expectItemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);

        assertEquals(actualItemRequestDto, expectItemRequestDto);
        verify(userRepository, times(1)).findById(anyInt());
        verify(itemRequestRepository, times(1)).findById(anyInt());
    }

    @Test
    void getById_whenUserNotFound_thanNotFoundExceptionThrown() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getById(requestor.getId(), itemRequest.getId()));
        verify(userRepository, times(1)).findById(anyInt());
        verify(itemRequestRepository, never()).findById(anyInt());
    }

    @Test
    void getById_whenItemRequestNotFound_thanNotFoundExceptionThrown() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.empty());


        assertThrows(NotFoundException.class, () -> itemRequestService.getById(requestor.getId(), itemRequest.getId()));
        verify(userRepository, times(1)).findById(anyInt());
        verify(itemRequestRepository, times(1)).findById(anyInt());
    }
}