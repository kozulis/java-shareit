package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    LocalDateTime now = LocalDateTime.now();

    private final User user = User.builder().id(1).name("user").email("user@user.com").build();
    private final User booker = User.builder().id(2).name("booker").email("booker@user.com").build();
    private final Item item = Item.builder().id(3).name("itemName").description("itemDescription").available(true)
            .owner(user).requestId(1).build();
    private final ItemDto itemDto = ItemDto.builder().name("itemName").description("itemDescription").available(true)
            .build();
    private final ItemRequest itemRequest = ItemRequest.builder().description("itemRequest").build();
    private final CommentDto commentDto = CommentDto.builder().text("comment").build();
    private final Comment comment = CommentMapper.toComment(commentDto, user, item);
    private final BookingDto bookingDto = BookingDto.builder().start(now.plusHours(1)).end(now.plusHours(2))
            .itemId(3).build();
    private final Booking booking = BookingMapper.toBooking(bookingDto, item, booker);


    @Test
    @DisplayName("Добавление вещи")
    void saveItem_returnItem() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        itemDto.setRequestId(1);
        ItemDto actualItemDto = itemService.saveItem(user.getId(), itemDto);
        ItemDto expectItemDto = ItemMapper.toItemDto(item);

        assertEquals(actualItemDto, expectItemDto);
        verify(userRepository, times(1)).findById(anyInt());
        verify(itemRequestRepository, times(1)).findById(anyInt());
        verify(itemRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Ошибка добавления вещи, если пользователь не найден")
    void saveItem_whenUserNotFound_thanNotFoundExceptionThrown() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.saveItem(user.getId(), itemDto));
        verify(userRepository, times(1)).findById(anyInt());
        verify(itemRequestRepository, never()).findById(anyInt());
        verify(itemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Ошибка добавления вещи, если запрос на вещь не найден")
    void saveItem_whenItemRequestNotFound_thanNotFoundExceptionThrown() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyInt())).thenReturn(Optional.empty());

        itemDto.setRequestId(1);
        assertThrows(NotFoundException.class, () -> itemService.saveItem(user.getId(), itemDto));
        verify(userRepository, times(1)).findById(anyInt());
        verify(itemRequestRepository, times(1)).findById(anyInt());
        verify(itemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Получение списка вещей пользователя")
    void getAllByUserId_returnItemList() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyInt(), any(Pageable.class))).thenReturn(
                List.of(item));
        when(commentRepository.findByItemIn(anyList())).thenReturn(List.of(comment));
        when(bookingRepository.findAllByItemInAndStatusOrderByStartAsc(anyList(), any(BookingStatus.class)))
                .thenReturn(List.of(booking));

        List<ItemDto> actualItemDto = itemService.getAllByUserId(user.getId(), 0, 10);
        List<ItemDto> expectItemDto = List.of(ItemMapper.toItemDto(item, null, BookingMapper.toBookingDto(booking),
                List.of(CommentMapper.toCommentDto(comment))));

        assertEquals(actualItemDto, expectItemDto);
        verify(userRepository, times(1)).findById(anyInt());
        verify(itemRepository, times(1)).findAllByOwnerId(anyInt(), any(Pageable.class));
        verify(commentRepository, times(1)).findByItemIn(anyList());
        verify(bookingRepository, times(1))
                .findAllByItemInAndStatusOrderByStartAsc(anyList(), any(BookingStatus.class));
    }

    @Test
    @DisplayName("Ошибка получения списка вещей, если пользователь не найден")
    void getAllByUserId_whenUserNotFound_thanNotFoundExceptionThrown() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getAllByUserId(user.getId(), 0, 10));
        verify(userRepository, times(1)).findById(anyInt());
        verify(itemRepository, never()).findAllByOwnerId(anyInt(), any(Pageable.class));
        verify(commentRepository, never()).findByItemIn(anyList());
        verify(bookingRepository, never())
                .findAllByItemInAndStatusOrderByStartAsc(anyList(), any(BookingStatus.class));
    }

    @Test
    @DisplayName("Получение вещи по id")
    void getById_returnItem() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemInAndStatusOrderByStartAsc(anyList(), any(BookingStatus.class)))
                .thenReturn(List.of(booking));
        when(commentRepository.findByItemOrderByIdAsc(any(Item.class))).thenReturn(List.of(comment));

        ItemDto actualItemDto = itemService.getById(user.getId(), item.getId());
        ItemDto expectItemDto = ItemMapper.toItemDto(item, null, BookingMapper.toBookingDto(booking),
                List.of(CommentMapper.toCommentDto(comment)));

        assertEquals(actualItemDto, expectItemDto);
        verify(itemRepository, times(1)).findById(anyInt());
        verify(commentRepository, times(1)).findByItemOrderByIdAsc(any(Item.class));
        verify(bookingRepository, times(1))
                .findAllByItemInAndStatusOrderByStartAsc(anyList(), any(BookingStatus.class));
    }

    @Test
    @DisplayName("Получение вещи по id, не имеющей комментариев")
    void getById_returnItemWithoutComments() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemInAndStatusOrderByStartAsc(anyList(), any(BookingStatus.class)))
                .thenReturn(List.of(booking));
        when(commentRepository.findByItemOrderByIdAsc(any(Item.class))).thenReturn(List.of(comment));

        ItemDto actualItemDto = itemService.getById(6, item.getId());
        ItemDto expectItemDto = ItemMapper.toItemDto(item,
                List.of(CommentMapper.toCommentDto(comment)));

        assertEquals(actualItemDto, expectItemDto);
        verify(itemRepository, times(1)).findById(anyInt());
        verify(commentRepository, times(1)).findByItemOrderByIdAsc(any(Item.class));
        verify(bookingRepository, times(1))
                .findAllByItemInAndStatusOrderByStartAsc(anyList(), any(BookingStatus.class));
    }


    @Test
    @DisplayName("Ошибка получения вещи по id, если вещь не найдена")
    void getById_whenItemNotFound_thanNotFoundExceptionThrown() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getById(user.getId(), item.getId()));
        verify(itemRepository, times(1)).findById(anyInt());
        verify(commentRepository, never()).findByItemOrderByIdAsc(any(Item.class));
        verify(bookingRepository, never())
                .findAllByItemInAndStatusOrderByStartAsc(anyList(), any(BookingStatus.class));
    }

    @Test
    @DisplayName("Обновление параметров вещи")
    void updateItem_returnItem() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        itemDto.setName("itemUpdate");
        itemDto.setDescription("itemUpdateDescription");
        itemDto.setAvailable(false);

        ItemDto actualItemDto = itemService.updateItem(user.getId(), item.getId(), itemDto);
        ItemDto expectItemDto = ItemMapper.toItemDto(item);

        assertEquals(actualItemDto, expectItemDto);
        verify(itemRepository, times(1)).findById(anyInt());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    @DisplayName("Ошибка получения обновлении параметров вещи, если вещь не найдена")
    void updateItem_whenItemNotFound_thanNotFoundExceptionThrown() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(user.getId(), item.getId(), itemDto));

        verify(itemRepository, times(1)).findById(anyInt());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    @DisplayName("Ошибка получения обновлении параметров вещи при попытке обновления другим пользователем")
    void updateItem_whenWrongItemOwner_thanNotFoundExceptionThrown() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> itemService.updateItem(3, item.getId(), itemDto));

        verify(itemRepository, times(1)).findById(anyInt());
        verify(itemRepository, never()).save(any(Item.class));
    }


    @Test
    @DisplayName("Удаление вещи")
    void deleteById() {
        itemService.deleteById(item.getId());
        verify(itemRepository, times(1)).deleteById(anyInt());
    }

    @Test
    @DisplayName("Поиск вещи по названию или описанию")
    void searchItem_ReturnItemList() {
        when(itemRepository.search(anyString(), any(Pageable.class))).thenReturn(List.of(item));

        List<ItemDto> actualItemDtoList = itemService.searchItem(user.getId(), "item", 0, 10);
        List<ItemDto> expectItemDtoList = List.of(ItemMapper.toItemDto(item));

        assertEquals(actualItemDtoList.size(), 1);
        assertEquals(actualItemDtoList, expectItemDtoList);
    }

    @Test
    @DisplayName("Поиск вещи по названию или описанию возвращает пустой список")
    void searchItem_ReturnEmptyItemList() {
        List<ItemDto> actualItemDtoList = itemService.searchItem(user.getId(), "", 0, 10);

        assertEquals(actualItemDtoList.size(), 0);
        verify(itemRepository, never()).search(anyString(), any(Pageable.class));
    }
}